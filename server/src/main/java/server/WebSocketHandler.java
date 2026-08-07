package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import io.javalin.websocket.*;
import websocket.commands.UserGameCommand;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import websocket.messages.ErrorMessages;
import websocket.messages.NotificationMessage;
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
            String authToken = command.getAuthToken();
            if (!checkAuth(authToken)) {
                var updatedNotification = new ErrorMessages(ServerMessage.ServerMessageType.ERROR, "user didn't register");
                session.getRemote().sendString(new Gson().toJson(updatedNotification));
                return;
            }
            String username = getUsername(command.getAuthToken());
            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, gameID);
                case MAKE_MOVE -> makeMove(session, username, gameID, command.getMove());
                case LEAVE -> leaveGame(session, username, gameID);
                case RESIGN -> resign(session, username, gameID);
            }
        } catch (Exception ex) {
            var updatedNotification = new ErrorMessages(ServerMessage.ServerMessageType.ERROR, ex.getMessage());
            session.getRemote().sendString(new Gson().toJson(updatedNotification));
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
        return null;
    }

    private boolean checkAuth(String authToken) {
        try {
            dataAccess.getAuthData(authToken);
            return true;
        } catch (DataAccessException e) {
            return false;
        }
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

    public boolean checkGameID(Integer gameID) throws DataAccessException {
        List<GameInfo> gameInfos = dataAccess.listGames();
        for (GameInfo info : gameInfos) {
            if (Objects.equals(info.gameID(), gameID)) {
                return true;
            }
        }
        return false;
    }

    public void updateGameData (GameData gameData, ChessGame game, Integer gameID,
                                String whiteUsername, String blackUsername) throws DataAccessException {
        dataAccess.deleteGame(gameID);
        dataAccess.createGame(new GameData(gameData.gameID(), whiteUsername, blackUsername, gameData.gameName(), game));
    }

    public void connect(Session session, String username, Integer gameID) throws Exception {
        if (!checkGameID(gameID)) {
            var updatedNotification = new ErrorMessages(ServerMessage.ServerMessageType.ERROR, "Error: Invalid");
            session.getRemote().sendString(new Gson().toJson(updatedNotification));
            return;
        }
        var sendGame = ServerMessage.callLoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                getPlayerColor(username, gameID), getGameData(gameID).game());
        var updatedNotification = ServerMessage.callNotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                "connect", username, getPlayerColor(username, gameID), null, null);
        saveSession(gameID, session);
        // System.out.println("Session saved");
        session.getRemote().sendString(new Gson().toJson(sendGame));
        connections.broadcast(session, gameID, new Gson().toJson(updatedNotification));
    }

    public void leaveGame(Session session, String username, Integer gameID) throws Exception {
        var updatedNotification = ServerMessage.callNotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                "leave", username, getPlayerColor(username, gameID), null, null);
        connections.broadcast(session, gameID, new Gson().toJson(updatedNotification));
        connections.remove(gameID, session);
        if (Objects.equals(getPlayerColor(username, gameID), "WHITE")) {
            updateGameData(getGameData(gameID), getGameData(gameID).game(), gameID,username, getGameData(gameID).blackUsername());
        }
        else if (Objects.equals(getPlayerColor(username, gameID), "BLACK")) {
            updateGameData(getGameData(gameID), getGameData(gameID).game(), gameID, getGameData(gameID).whiteUsername(), username);
        }
    }

    public void resign(Session session, String username, Integer gameID) throws Exception {
        GameData gameData = getGameData(gameID);
        String playerColor = getPlayerColor(username, gameID);
        String opposingUsername;
        if (gameData.game().checkGameOver()) {
            var updatedNotification = new ErrorMessages(ServerMessage.ServerMessageType.ERROR, "Game was already forfeited. You win!");
            session.getRemote().sendString(new Gson().toJson(updatedNotification));
            return;
        }
        if (playerColor != null && playerColor.equals("OBSERVER")) {
            var updatedNotification = new ErrorMessages(ServerMessage.ServerMessageType.ERROR, "Observers can't resign.");
            session.getRemote().sendString(new Gson().toJson(updatedNotification));
            return;
        }
        if (playerColor != null && playerColor.equals("WHITE")) {
            opposingUsername = gameData.blackUsername();
            updateGameData(gameData, getGameData(gameID).game(), gameID, null, opposingUsername);
        }
        else {
            opposingUsername = gameData.whiteUsername();
            updateGameData(gameData, getGameData(gameID).game(), gameID, opposingUsername, null);
        }

        var updatedNotification = ServerMessage.callNotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                "resign", username, getPlayerColor(username, gameID), opposingUsername, null);
        connections.broadcast(null, gameID, new Gson().toJson(updatedNotification));
        // this boolean makes it so once the game is over or they resign it's done!
        gameData.game().updateGameOver();
        updateGameData(gameData, gameData.game(), gameID, gameData.whiteUsername(), gameData.blackUsername());
    }

    public void updateGame(ChessMove move, ChessGame game) throws Exception {
        game.makeMove(move);
    }

    public void makeMove(Session session, String username, Integer gameID, ChessMove move) throws Exception {
        String color = getPlayerColor(username, gameID);
        GameData gameData = getGameData(gameID);
        ChessGame game = getGameData(gameID).game();
        ChessGame.TeamColor currentTurn = game.getTeamTurn();
        ChessGame.TeamColor currentColor = null;
        ChessGame.TeamColor opposingColor = null;
        String opposingUsername = null;
        // makes sure the game can be played, and checks for check, checkmate, and stalemate
        if (game.checkGameOver()) {
            var updatedNotification = new ErrorMessages(ServerMessage.ServerMessageType.ERROR, "Game is already over.");
            session.getRemote().sendString(new Gson().toJson(updatedNotification));
            return;
        }
        if (color != null && color.equals("BLACK")) {
            currentColor = ChessGame.TeamColor.BLACK;
            opposingColor = ChessGame.TeamColor.WHITE;
            opposingUsername = gameData.whiteUsername();
        }
        else if (color != null && color.equals("WHITE")) {
            currentColor = ChessGame.TeamColor.WHITE;
            opposingColor = ChessGame.TeamColor.BLACK;
            opposingUsername = gameData.blackUsername();
        }
        else {
            var updatedNotification = new ErrorMessages(ServerMessage.ServerMessageType.ERROR, "Observers can not make moves.");
            session.getRemote().sendString(new Gson().toJson(updatedNotification));
            return;
        }
        if (!currentColor.equals(currentTurn)) {
            var updatedNotification = new ErrorMessages(ServerMessage.ServerMessageType.ERROR, "Not your turn.");
            session.getRemote().sendString(new Gson().toJson(updatedNotification));
            return;
        }

        try {
            updateGame(move, game);
            updateGameData(gameData, game, gameID, gameData.whiteUsername(), gameData.blackUsername());
        } catch (Exception e) {
            var updatedNotification = new ErrorMessages(ServerMessage.ServerMessageType.ERROR, "Invalid move!");
            session.getRemote().sendString(new Gson().toJson(updatedNotification));
            return;
        }

        if (getGameData(gameID).game().isInCheckmate(opposingColor)) {
            var updatedNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    String.format("%s is in checkmate.", opposingUsername));
            gameData.game().updateGameOver();
            updateGameData(gameData, gameData.game(), gameID, gameData.whiteUsername(), gameData.blackUsername());
            connections.broadcast(null, gameID, new Gson().toJson(updatedNotification));
        }
        else if (getGameData(gameID).game().isInCheck(opposingColor)) {
            var updatedNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    String.format("%s is in check", opposingUsername));
            connections.broadcast(null, gameID, new Gson().toJson(updatedNotification));
        }
        else if (getGameData(gameID).game().isInStalemate(opposingColor)) {
            var updatedNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    "The game is in stalemate.");
            gameData.game().updateGameOver();
            updateGameData(gameData, gameData.game(), gameID, gameData.whiteUsername(), gameData.blackUsername());
            connections.broadcast(null, gameID, new Gson().toJson(updatedNotification));
        }
        // System.out.println(new Gson().toJson(game));
        var sendGame = ServerMessage.callLoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, color, getGameData(gameID).game());
        connections.broadcast(null, gameID, new Gson().toJson(sendGame));
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();

        String start = "" + (char)(startPosition.getColumn()-1 + 'a') + (startPosition.getRow());
        String end = "" + (char)(endPosition.getColumn()-1 + 'a') + (endPosition.getRow());
        var updatedNotification = ServerMessage.callNotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                "move", username, start, end, username);
        connections.broadcast(session, gameID, new Gson().toJson(updatedNotification));
    }
}
