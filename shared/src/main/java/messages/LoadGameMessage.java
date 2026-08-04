package messages;

public class LoadGameMessage extends ServerMessage {
    public LoadGameMessage(ServerMessageType type) {
        super(type);
    }

    public static String loadGameMessage(String function, String username) {
        if (function.equals("connect")) {
            return String.format("%s has joined the game.", username);
        }
        if (function.equals("leave")) {
            return String.format("%s has left the game.", username);
        }
        return "Error: Invalid";
    }
}


