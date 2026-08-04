package messages;

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

    public ServerMessage message(ServerMessageType type, String function, String username) {
        if (type.equals(ServerMessageType.LOAD_GAME)) {
            return LoadGameMessage.loadGameMessage(function, username);

        }
        else if (type.equals(ServerMessageType.ERROR)) {

        }
        else if (serverMessageType == ServerMessageType.NOTIFICATION) {

        }
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