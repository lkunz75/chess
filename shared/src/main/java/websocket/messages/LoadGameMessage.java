package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {
    private final String playerColor;
    private final ChessGame chessGame;


    public LoadGameMessage(ServerMessageType type, String playerColor, ChessGame chessGame) {
        super(type);
        this.playerColor = playerColor;
        this.chessGame = chessGame;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public ChessGame getChessGame() {
        return chessGame;
    }
}


