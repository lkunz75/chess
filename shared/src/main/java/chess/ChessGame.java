package chess;

import java.util.Collection;

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
        int start_row = start.getRow();
        int start_col = start.getColumn();
        ChessBoard board = getBoard();
        ChessPosition endposition = move.getEndPosition();
        if (board.squares[start_row][start_col].getTeamColor() == TeamColor.WHITE) {
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
        // pieceMoves(ChessBoard board, ChessPosition myPosition)
        if (teamColor == TeamColor.WHITE){
            // go through colors
            while (row < 8){
                while (col < 8) {
                    ChessPosition board_spot = new ChessPosition(row, col);
                    col++;
                    Collection<ChessMove> list_of_moves = pieceMoves(game, board_spot);
                }
                col = 0;
                row++;
            }
            if (endPosition.equals(kingPosition)){
                return true;
            }
        }
        if (teamColor == TeamColor.BLACK){
            // goes through other team colors
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
}
