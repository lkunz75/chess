package server;

import chess.ChessGame;
import com.google.gson.Gson;
import io.javalin.websocket.*;
import websocket.commands.UserGameCommand;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import websocket.messages.ServerMessage;
import model.AuthData;
import model.GameData;
import model.GameInfo;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Objects;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final DataAccess dataAccess;

    public WebSocketHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @Override
    public void handleConnect(WsConnectContext context) {
        System.out.println("Websocket connected");
        context.enableAutomaticPings();
    }

    @Override
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
                case MAKE_MOVE -> makeMove(session, username, command.getAuthToken(), command.getGameID());
                case LEAVE -> leaveGame(session, username, command.getAuthToken(), command.getGameID());
                case RESIGN -> resign(session, username, command.getAuthToken(), command.getGameID());
            }
        } catch(DataAccessException ex) {
            var updatedNotification = ServerMessage.errorMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage());
            session.getRemote().sendString(updatedNotification);
        }
        catch (Exception ex) {
            var updatedNotification = ServerMessage.errorMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage());
            connections.broadcast(null, gameID, updatedNotification);
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {
        System.out.println("Websocket closed");
    }

    private void saveSession(int gameID, Session session) {
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
        // this should probably be an error message from Notification Message instead
        throw new DataAccessException("Error: Invalid");
    }

    private void checkAuth(String authToken) throws DataAccessException {
        dataAccess.getAuthData(authToken);
    }

    // Just moved it
    public GameData getGameData (Integer gameID) throws DataAccessException {
        List<GameInfo> gameInfos = dataAccess.listGames();
        for (GameInfo info : gameInfos) {
            if (Objects.equals(info.gameID(), gameID)) {
                return dataAccess.getGame(info.gameName());
            }
        }
        return null;
    }

    public void connect(Session session, String username, String authToken, Integer gameID) throws Exception {
        //might have to deserialze twice
        checkAuth(authToken); // will throw an error if not there
        // System.out.println(getPlayerColor(username, gameID));
        // System.out.println(authToken);
        // System.out.println(username);
        // System.out.println(gameID);
        // System.out.println(getGameData(gameID).game());
        var sendGame = ServerMessage.callLoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, getPlayerColor(username, gameID), getGameData(gameID).game());
        var updatedNotification = ServerMessage.callNotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "connect", username, getPlayerColor(username, gameID), null);
        saveSession(gameID, session); // has nullptr
        session.getRemote().sendString(new Gson().toJson(sendGame));
        connections.broadcast(session, gameID, updatedNotification);
    }

    public void leaveGame(Session session, String username, String authToken, Integer gameID) throws Exception {
        checkAuth(authToken);
        var updatedNotification = ServerMessage.callNotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "leave", username, getPlayerColor(username, gameID), null);
        connections.broadcast(session, gameID, updatedNotification);
        connections.remove(gameID, session);
        var sendGame = ServerMessage.callLoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, getPlayerColor(username, gameID), getGameData(gameID).game());
        session.getRemote().sendString(new Gson().toJson(sendGame));
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
        var updatedNotification = ServerMessage.callNotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "resign", username, getPlayerColor(username, gameID), opposingUsername);
        connections.broadcast(session, gameID, updatedNotification);
        connections.remove(gameID, session);
    }

    public void makeMove(Session session, String username, String authToken, Integer gameID) {
        // still working on the details
    }
}
