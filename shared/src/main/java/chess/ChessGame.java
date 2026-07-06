package chess;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessBoard game = new ChessBoard();
    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        int start_row = start.getRow()-1;
        int start_col = start.getColumn()-1;
        ChessBoard board = getBoard();
        ChessPosition endposition = move.getEndPosition();
        if (board.squares[start_row][start_col] != null && board.squares[start_row][start_col].getTeamColor() == TeamColor.WHITE) {
            // check color so we know whose turn is next
            if (isInCheck(TeamColor.WHITE)){
                // changes what moves can be made
            }
            if (isInCheck(TeamColor.WHITE)){
                // changes what moves can be made
            }
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        int row = 0;
        int col = 0;
        ChessBoard board = getBoard();
        ChessPosition king_position = null; // initialize it
        // pieceMoves(ChessBoard board, ChessPosition myPosition)
        if (teamColor != TeamColor.BLACK){
            // to check if white is in check, you have to go through the valid Black Moves and see if its where the white king is
            // you need to find where the White King is.
            List<List<Integer>> white_king = board.return_white_king();
            for (List<Integer> king:white_king){
                int k_row = king.get(0);
                int k_col = king.get(1);
                king_position = new ChessPosition(k_row+1, k_col+1);
            }
            while (row < 8){
                while (col < 8) {
                    ChessPosition board_spot = new ChessPosition(row+1, col+1);
                    ChessPiece piece = board.squares[row][col];
                    if (piece != null && piece.getTeamColor() == TeamColor.BLACK) { // this should be checked because we want to check and see if opposing team puts us in check
                        Collection<ChessMove> list_of_moves = piece.pieceMoves(game, board_spot);
                        for (ChessMove move : list_of_moves) {
                            ChessPosition endPosition = move.getEndPosition();
                            if (endPosition.equals(king_position)) {
                                return true;
                            } // is in check
                        }
                    }
                    col++;
                }
                col = 0;
                row++;
            }
            return false; // went through it all without hitting the king
        }
        else {
            // goes through other team colors
            List<List<Integer>> black_king = board.return_black_king();
            for (List<Integer> king:black_king){
                int k_row = king.get(0);
                int k_col = king.get(1);
                king_position = new ChessPosition(k_row+1, k_col+1);
            }
            while (row < 8){
                while (col < 8) {
                    ChessPosition board_spot = new ChessPosition(row+1, col+1);
                    ChessPiece piece = board.squares[row][col]; // remember how your indexed!!
                    if (piece != null && piece.getTeamColor() == TeamColor.WHITE) { // this should be checked because we want to check and see if opposing team puts us in check
                        Collection<ChessMove> list_of_moves = piece.pieceMoves(game, board_spot);
                        for (ChessMove move : list_of_moves) {
                            ChessPosition endPosition = move.getEndPosition();
                            if (endPosition.equals(king_position)) {
                                return true;
                            } // is in check
                        }
                    }
                    col++;
                }
                col = 0;
                row++;
            }
            return false; // went through it all without hitting the king
        }
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        game = board; // set the game chessboard to the given board
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return game; // returns a chessboard object
    }


    @Override
    public String toString() {
        return "ChessGame{" +
                "game=" + game +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(game, chessGame.game);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(game);
    }
}
