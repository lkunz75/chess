package messages;

public class LoadGameMessage extends ServerMessage {
    public LoadGameMessage(ServerMessageType type) {
        super(type);
    }

    public static ServerMessage loadGameMessage(String function, String username) {
        if (function.equals("connect")) {
            return new ServerMessage("%s has joined the game.", username);
        }
        if (function.equals("leave")) {
            return new ServerMessage("%s has left the game.", username);
        }
        return "Error: Invalid";
    }
}


