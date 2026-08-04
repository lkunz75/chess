package messages;

import chess.ChessGame;
import model.GameData;

public class LoadGameMessage extends ServerMessage {

    public LoadGameMessage(ServerMessageType type) {
        super(type);
    }

    public static String loadGame(String playerColor, ChessGame chessGame) throws Exception {
        // this needs to send the right game board out to all!
        if (playerColor.equals("BLACK")) {
            return String.format("BLACK Team's Board %s", chessGame);

        } else {
            return String.format("WHITE Team's Board %s", chessGame);
        }
    }
}


