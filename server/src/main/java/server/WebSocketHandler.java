package server;

import chess.ChessGame;
import com.google.gson.Gson;
import commands.UserGameCommand;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsMessageContext;
import messages.ServerMessage;
import model.AuthData;
import model.GameData;
import model.GameInfo;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final DataAccess dataAccess;

    public WebSocketHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void handleConnect(WsConnectContext context) {
        System.out.println("Websocket connected");
        context.enableAutomaticPings();
    }

    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws Exception {
        int gameID = -1;
        Session session = wsMessageContext.session;
        try {
            UserGameCommand command = new Gson().fromJson(
                    wsMessageContext.message(), UserGameCommand.class);
            gameID = command.getGameID();
            String username = getUsername(command.getAuthToken());
            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, command.getAuthToken(), command.getGameID());
                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
                case LEAVE -> leaveGame(session, username, command.getAuthToken(), command.getGameID());
                case RESIGN -> resign(session, username, command.getAuthToken(), command.getGameID());
            }
        } catch(DataAccessException ex) {
            var updatedNotification = ServerMessage.message(ServerMessage.ServerMessageType.ERROR, null, null, null, null, null);
            session.getRemote().sendString(updatedNotification);
        }
        catch (Exception ex) {
            var updatedNotification = ServerMessage.message(ServerMessage.ServerMessageType.ERROR, null, null, null, null, null);
            connections.broadcast(session, gameID, updatedNotification);
        }
    }

    private void saveSession(int gameID, Session session) {
        // figure out how to saveSession
        connections.add(gameID, session);
    }

    private String getUsername(String authToken) throws DataAccessException {
        AuthData.AuthRecord authData = dataAccess.getAuthData(authToken);
        return authData.username();
    }

    private String getPlayerColor(String username, Integer gameID) throws DataAccessException {
        List<GameInfo> gameData = dataAccess.listGames();
        for (GameInfo info : gameData) {
            if (info.gameID() == gameID) {
                // check null first to avoid errors
                if (info.whiteUsername() != null && info.whiteUsername().equals(username)) {
                    return "WHITE";
                }
                if (info.blackUsername() != null && info.blackUsername().equals(username)) {
                    return "BLACK";
                }
                return "OBSERVER";
            }
        }
        throw new DataAccessException("Error: Invalid");
    }

    private void checkAuth(String authToken) throws DataAccessException {
        dataAccess.getAuthData(authToken);
    }

    public void connect(Session session, String username, String authToken, Integer gameID) throws Exception {
        checkAuth(authToken); // will throw an error if not there
        var updatedNotification = ServerMessage.message(ServerMessage.ServerMessageType.NOTIFICATION, "connect", username, getPlayerColor(username, gameID), null, null);
        saveSession(gameID, session);
        connections.broadcast(session, gameID, updatedNotification);
    }

    public GameData getGameData (Integer gameID) throws DataAccessException {
        List<GameInfo> gameInfos = dataAccess.listGames();
        for (GameInfo info : gameInfos) {
            if (info.gameID() == gameID) {
                return dataAccess.getGame(info.gameName());
            }
        }
        return null;
    }

    public void leaveGame(Session session, String username, String authToken, Integer gameID) throws Exception {
        checkAuth(authToken);
        var updatedNotification = ServerMessage.message(ServerMessage.ServerMessageType.NOTIFICATION, "leave", username, null, getGameData(gameID).game(), null);
        connections.broadcast(session, gameID, updatedNotification);
        connections.remove(gameID, session);
        var sendGame = ServerMessage.message(ServerMessage.ServerMessageType.LOAD_GAME, "leave", username, null, getGameData(gameID).game(), null);
        session.getRemote().sendString(sendGame);
    }

    public void resign(Session session, String username, String authToken, Integer gameID) throws Exception {
        checkAuth(authToken);
        GameData gameData = getGameData(gameID);
        String playerColor = getPlayerColor(username, gameID);
        String opposingUsername;
        if (playerColor.equals("WHITE")) {
            opposingUsername = gameData.blackUsername();
        }
        else {
            opposingUsername = gameData.whiteUsername();
        }
        var updatedNotification = ServerMessage.message(ServerMessage.ServerMessageType.NOTIFICATION, "resign", username, getPlayerColor(username, gameID), null, opposingUsername);
        connections.broadcast(session, gameID, updatedNotification);
        connections.remove(gameID, session);
    }

    public String sendMessage(Session session, Integer gameID, DataAccessException ex) {
        return String.format("Error: %s for %s session for %d", ex.getMessage(), session, gameID);
    }
}
