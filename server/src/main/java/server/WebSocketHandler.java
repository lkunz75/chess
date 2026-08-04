package server;

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
import service.ErrorMessage;

import java.io.IOException;
import java.net.http.WebSocket;
import java.util.List;

public class WebSocketHandler {
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
        int gameId = -1;
        Session session = wsMessageContext.session;
        try {
            UserGameCommand command = new Gson().fromJson(
                    wsMessageContext.message(), UserGameCommand.class);
            gameId = command.getGameID();
            String username = getUsername(command.getAuthToken());
            saveSession(gameId, session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, command.getAuthToken(), command.getGameID());
                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
                case LEAVE -> leaveGame(session, username, command.getAuthToken(), command.getGameID());
                case RESIGN -> resign(session, username, (ResignCommand) command);
            }
        } catch (UnauthorizedException ex) {
            sendMessage(session, gameId, new ErrorMessage("Error: unauthorized"));
        } catch (Exception ex) {
            ex.printStackTrace();
            sendMessage(session, gameId, new ErrorMessage("Error: " + ex.getMessage()));
        }
    }

    private void saveSession(int gameID, Session session) {
        // figure out how to saveSession
        connections.add(gameID, session);
    }

    private String getUsername(String authToken) throws DataAccessException {
        try {
            AuthData.AuthRecord authData = dataAccess.getAuthData(authToken);
            return authData.username();
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private String getPlayerColor(String username, Integer gameID) throws DataAccessException {
        List<GameInfo> gameData = dataAccess.listGames();
        for (GameInfo info : gameData) {
            if (info.gameID() == gameID) {
                if (info.whiteUsername().equals(username)) {
                    return "WHITE";
                }
                if (info.blackUsername().equals(username)) {
                    return "BLACK";
                }
                throw new DataAccessException("Error: Player not a part of game");
            }
        }
        throw new DataAccessException("Error: Invalid");
    }

    public void connect(Session session, String username, String authToken, Integer gameID) throws IOException, DataAccessException {
        // make sure that the person is authorized (make a function for that)
        connections.add(gameID, session);
        var message = String.format("%s joined the game as %s", username, getPlayerColor(username, gameID)); // determine which side
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        var updatedNotification = notification.message(ServerMessage.ServerMessageType.LOAD_GAME, "connect", username);
        connections.broadcast(null, gameID, updatedNotification);
    }

    public void leaveGame(Session session, String username, String authToken, Integer gameID) throws IOException {
        var message = String.format("%s has left the game", username);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        var updatedNotification = notification.message(ServerMessage.ServerMessageType.LOAD_GAME, "leave", username);
        connections.broadcast(null, gameID, updatedNotification);
        connections.remove(gameID, session);
    }
}
