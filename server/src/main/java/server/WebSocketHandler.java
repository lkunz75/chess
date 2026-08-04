package server;

import com.google.gson.Gson;
import commands.UserGameCommand;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsMessageContext;
import messages.ServerMessage;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import service.ErrorMessage;

import java.io.IOException;
import java.net.http.WebSocket;

public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

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

    private void saveSession(int gameId, Session session) {
        // figure out how to saveSession
    }

    private String getUsername(String authToken) {
        // figure out how to get username
    }

    public void connect(Session session, String username, String authToken, Integer gameID) throws IOException {
        // make sure that the person is authorized (make a function for that)
        connections.add(gameID, session);
        var message = String.format("%s joined the game", username); // determine which side
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
    }

    public void leaveGame(Session session, String username, String authToken, Integer gameID) throws IOException {
        var message = String.format("%s has left the game", username);
        var notification = new UserGameCommand(session, UserGameCommand.CommandType.RESIGN, authToken, gameID);
        //notification here needs to be a ServerMessage and right now it's a UserGameCommand
        connections.broadcast(session, notification);
        connections.remove(session);
    }


}
