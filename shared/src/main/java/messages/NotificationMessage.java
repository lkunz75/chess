package messages;

public class NotificationMessage {
    public static String notificationMessage(String function, String username, String playerColor) {
        if (function.equals("connect")) {
            return String.format("%s has joined the game as %s", username, playerColor);
        }
        if (function.equals("leave")) {
            return String.format("%s has left the game.", username);
        }
        return "Error: Invalid";
    }
}
