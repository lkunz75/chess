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

    public static String callNotificationMessage(ServerMessageType type, String function, String username, String playerColor, String opposingPlayer) throws Exception {
        if (type.equals(ServerMessageType.NOTIFICATION)) {
            return NotificationMessage.notificationMessage(function, username, playerColor, opposingPlayer);
        }
        throw new Exception("Error: Invalid");
    }

    public static String errorMessage(ServerMessageType type, String message) throws Exception {
        if (type.equals(ServerMessageType.ERROR)) {
            return String.format(message);
        }
        throw new Exception("Error: Invalid");
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