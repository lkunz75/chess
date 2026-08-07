package client;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessages;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.net.URI;

public class WebSocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage mes = new Gson().fromJson(message, ServerMessage.class);
                    if (mes.getServerMessageType().equals(ServerMessage.ServerMessageType.LOAD_GAME)) {
                        LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
                        notificationHandler.notify(loadGameMessage); // will notify chess client / send info
                    }
                    else if (mes.getServerMessageType().equals(ServerMessage.ServerMessageType.NOTIFICATION)) {
                        NotificationMessage notificationMessage = new Gson().fromJson(message, NotificationMessage.class);
                        notificationHandler.notify(notificationMessage);
                    }
                    else {
                        ErrorMessages errorMessages = new Gson().fromJson(message, ErrorMessages.class);
                        notificationHandler.notify(errorMessages);
                    }

                }
            });
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, Integer gameID, ChessMove move, ChessGame game) throws DataAccessException {
        try {
           // UserGameCommand.CommandType commandType, String authToken, Integer gameID, ChessMove move, ChessGame game
            UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID, move, game);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void makeMove(String authToken, Integer gameID, ChessMove move, ChessGame game) throws DataAccessException {
        try {
            // UserGameCommand.CommandType commandType, String authToken, Integer gameID, ChessMove move, ChessGame game
            UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move, game);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void leave(String authToken, Integer gameID, ChessMove move, ChessGame game) throws DataAccessException {
        try {
            // UserGameCommand.CommandType commandType, String authToken, Integer gameID, ChessMove move, ChessGame game
            UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID, move, game);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void resign(String authToken, Integer gameID, ChessMove move, ChessGame game) throws DataAccessException {
        try {
            // UserGameCommand.CommandType commandType, String authToken, Integer gameID, ChessMove move, ChessGame game
            UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID, move, game);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}