package websocket.messages;

import chess.ChessGame;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    // have another class that has extends ServerMessage because different messages will need different things
    // make one for load_game, one for error, and one for notification

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public static NotificationMessage callNotificationMessage(ServerMessageType type, String function, String username, String playerColor, String opposingPlayer) {
        // suggested change from function
        switch (function) {
            case "connect" -> {
                return new NotificationMessage(ServerMessageType.NOTIFICATION, String.format("%s has joined the game as %s", username, playerColor));
            }
            case "leave" -> {
                return new NotificationMessage(ServerMessageType.NOTIFICATION, String.format("%s has left the game.", username));
            }
            case "resign" -> {
                return new NotificationMessage(ServerMessageType.NOTIFICATION, String.format("%s resigns. %s wins the game!", username, opposingPlayer));
            }
        }
        return new NotificationMessage(ServerMessageType.NOTIFICATION, "Error: Invalid");
    }

    public ErrorMessages errorMessage(ServerMessageType type, String message) {
        if (type.equals(ServerMessageType.ERROR)) {
            return new ErrorMessages(ServerMessageType.ERROR, message);
        }
        return new ErrorMessages(ServerMessageType.ERROR, "Error: Invalid!");
    }

    public static LoadGameMessage callLoadGameMessage(ServerMessageType type, String playerColor, ChessGame gameData) throws Exception {
        if (type.equals(ServerMessageType.LOAD_GAME)) {
            return new LoadGameMessage(ServerMessageType.LOAD_GAME, playerColor, gameData);
        }
        throw new Exception("Error: Invalid");
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage that)) {
            return false;
        }
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}