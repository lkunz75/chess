package websocket.messages;

public class ErrorMessages extends ServerMessage{
    private final String errorMessage;

    public ErrorMessages(ServerMessageType type, String errorMessage) {
        super(type);
        this.errorMessage = errorMessage;
    }

    public String getMessage() {
        return errorMessage;
    }
}
