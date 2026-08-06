package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {
    private final String playerColor;
    private final ChessGame game;


    public LoadGameMessage(ServerMessageType type, String playerColor, ChessGame game) {
        super(type);
        this.playerColor = playerColor;
        this.game = game;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public ChessGame getChessGame() {
        return game;
    }
}


