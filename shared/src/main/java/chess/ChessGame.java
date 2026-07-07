package chess;

import java.util.*;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessBoard game = new ChessBoard();
    TeamColor current_turn = TeamColor.WHITE;

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return current_turn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        current_turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    // looked up how to make a clone + watched videos
    // One such method is to have ChessBoard implement Cloneable,
    // then in the override clone method, you loop through the 2d ChessPiece array,
    // and do Arrays.copyOf to copy the chess board row by row, then finally putting the 2d array into the cloned ChessBoard.
    public static class CloneCopy implements Cloneable {
        ChessPiece[][] data;

        public CloneCopy() {
            data = new ChessPiece[8][8];
        }

        @Override
        public CloneCopy clone() throws CloneNotSupportedException {
            CloneCopy clone = new CloneCopy();
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    if (data[i][j] != null) {
                        clone.data[i][j] = data[i][j].clone();
                    }
                }
            }
            return clone;
        }

        public static void main(String[] args) throws CloneNotSupportedException {
            CloneCopy copy = new CloneCopy();
        }
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    // makes sure the move doesn't put your team in check
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        List<ChessMove> valid_moves = new ArrayList<>();
        int col = startPosition.getColumn() - 1;
        int row = startPosition.getRow() - 1; // index it for an array
        if (game.squares[row][col] == null) {
            return null;
        }
        ChessPiece piece = game.squares[row][col];
        TeamColor color = piece.getTeamColor(); // know the color so you know if your king is at risk
        Collection<ChessMove> moves = piece.pieceMoves(game, startPosition); // gets all of the moves

        CloneCopy original = new CloneCopy();
        original.data = game.squares;
        ChessPiece[][] board_copy = original.data.clone(); // copy of my game
        // king can not move into check is failing why?

        game.squares = board_copy; // so we can use IsInCheck
        for (ChessMove move : moves) {
            ChessPosition start = move.getStartPosition();
            ChessPosition end = move.getEndPosition();
            game.squares[start.getRow() - 1][start.getColumn() - 1] = null;
            ChessPiece promoted = game.squares[end.getRow() - 1][end.getColumn() - 1];
            game.squares[end.getRow() - 1][end.getColumn() - 1] = piece;

            // if its the king, we have to update where we are storing the king for later!
            if (game.squares[end.getRow() - 1][end.getColumn() - 1].getPieceType() == ChessPiece.PieceType.KING){
                if (color == TeamColor.WHITE){
                    List<List<Integer>> updated_wk = new ArrayList<>();
                    updated_wk.add(Arrays.asList(end.getRow()-1, end.getColumn()-1));
                    game.white_king = updated_wk;
                }
                if (color == TeamColor.BLACK){
                    List<List<Integer>> updated_bk = new ArrayList<>();
                    updated_bk.add(Arrays.asList(end.getRow()-1, end.getColumn()-1));
                    game.black_king = updated_bk;
                }
            }
            if (!isInCheck(color)) {
                ChessPosition ValidMove = new ChessPosition(end.getRow(), end.getColumn());
                if (piece.getPieceType() == ChessPiece.PieceType.PAWN && piece.getTeamColor() == ChessGame.TeamColor.WHITE && row == 7) {
                    //promote the pawn when it reaches the end it can become any of these four things!
                    ChessMove new_queen = new ChessMove(start, ValidMove, ChessPiece.PieceType.QUEEN); //this should be null
                    valid_moves.add(new_queen);
                    ChessMove new_rook = new ChessMove(start, ValidMove, ChessPiece.PieceType.ROOK); //this should be null
                    valid_moves.add(new_rook);
                    ChessMove new_bishop = new ChessMove(start, ValidMove, ChessPiece.PieceType.BISHOP); //this should be null
                    valid_moves.add(new_bishop);
                    ChessMove new_knight = new ChessMove(start, ValidMove, ChessPiece.PieceType.KNIGHT); //this should be null
                    valid_moves.add(new_knight);
                } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN && piece.getTeamColor() == ChessGame.TeamColor.BLACK && row == 0) {
                    //promote
                    ChessMove new_queen = new ChessMove(start, ValidMove, ChessPiece.PieceType.QUEEN); //this should be null
                    valid_moves.add(new_queen);
                    ChessMove new_rook = new ChessMove(start, ValidMove, ChessPiece.PieceType.ROOK); //this should be null
                    valid_moves.add(new_rook);
                    ChessMove new_bishop = new ChessMove(start, ValidMove, ChessPiece.PieceType.BISHOP); //this should be null
                    valid_moves.add(new_bishop);
                    ChessMove new_knight = new ChessMove(start, ValidMove, ChessPiece.PieceType.KNIGHT); //this should be null
                    valid_moves.add(new_knight);
                } else {
                    ChessMove new_piece = new ChessMove(start, ValidMove, null); //this should be null
                    valid_moves.add(new_piece);
                }
            }
            game.squares[start.getRow() - 1][start.getColumn() - 1] = piece; // go back to OG
            game.squares[end.getRow() - 1][end.getColumn() - 1] = promoted;
            if (game.squares[start.getRow() - 1][start.getColumn() - 1].getPieceType() == ChessPiece.PieceType.KING){
                if (color == TeamColor.WHITE){
                    List<List<Integer>> updated_wk = new ArrayList<>();
                    updated_wk.add(Arrays.asList(start.getRow()-1, start.getColumn()-1));
                    game.white_king = updated_wk;
                }
                if (color == TeamColor.BLACK){
                    List<List<Integer>> updated_bk = new ArrayList<>();
                    updated_bk.add(Arrays.asList(start.getRow()-1, start.getColumn()-1));
                    game.black_king = updated_bk;
                }
            }

        }
        game.squares = original.data; // change it back
        return valid_moves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        int start_row = start.getRow() - 1;
        int start_col = start.getColumn() - 1;
        ChessBoard board = getBoard();
        ChessPosition endposition = move.getEndPosition();
        if (board.squares[start_row][start_col] != null) {
            // check color so we know whose turn is next
            Collection<ChessMove> valid_moves = validMoves(start);
            for (ChessMove moves : valid_moves) {
                ChessPosition move_end = moves.getEndPosition();
                if (endposition == move_end) {
                    ChessPiece piece = board.squares[start_row][start_col];
                    board.squares[endposition.getRow() - 1][endposition.getColumn() - 1] = piece;
                    board.squares[start_row][start_col] = null;
                    return;
                }
            }
        } else {
            throw new InvalidMoveException();
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
        ChessPosition king_position = null; // initialize it
        // pieceMoves(ChessBoard board, ChessPosition myPosition)
        if (teamColor != TeamColor.BLACK) {
            // to check if white is in check, you have to go through the valid Black Moves and see if its where the white king is
            // you need to find where the White King is.
            List<List<Integer>> white_king = game.return_white_king();
            for (List<Integer> king : white_king) {
                int k_row = king.get(0);
                int k_col = king.get(1);
                king_position = new ChessPosition(k_row + 1, k_col + 1);
            }
            while (row < 8) {
                while (col < 8) {
                    ChessPosition board_spot = new ChessPosition(row + 1, col + 1);
                    ChessPiece piece = game.squares[row][col];
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
        } else {
            // goes through other team colors
            List<List<Integer>> black_king = game.return_black_king();
            for (List<Integer> king : black_king) {
                int k_row = king.get(0);
                int k_col = king.get(1);
                king_position = new ChessPosition(k_row + 1, k_col + 1);
            }
            while (row < 8) {
                while (col < 8) {
                    ChessPosition board_spot = new ChessPosition(row + 1, col + 1);
                    ChessPiece piece = game.squares[row][col]; // remember how your indexed!!
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
        List<List<Integer>> king = new ArrayList<>();
        int row = 0;
        int col = 0;
        if (teamColor == TeamColor.BLACK) {
            king = game.return_black_king();
        }
        if (teamColor == TeamColor.WHITE) {
            king = game.return_white_king();
        }
        for (List<Integer> k : king) {
            row = k.get(0);
            col = k.get(1);
        }
        ChessPosition king_position = new ChessPosition(row + 1, col + 1);
        Collection<ChessMove> moves = validMoves(king_position);
        if (moves.size() == 1) {
            return true;
        }
        if (moves.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {return false;}
        int row = 0;
        int col = 0;

        while (row < 8) {
            while (col < 8) {
                ChessPosition board_spot = new ChessPosition(row + 1, col + 1);
                ChessPiece piece = game.squares[row][col]; // remember how your indexed!!
                if (piece != null && piece.getTeamColor() == teamColor) { // this should be checked because we want to check and see if opposing team puts us in check
                    Collection<ChessMove> list_of_moves = piece.pieceMoves(game, board_spot);
                    for (ChessMove move : list_of_moves) {
                        ChessPosition startPosition = move.getStartPosition();
                        Collection<ChessMove> valid_moves = validMoves(startPosition);
                        if (valid_moves.size() > 1){
                            return false;
                        }
                    }
                }
                col++;
            }
            col = 0;
            row++;
        }
        return true;
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
                ", current_turn=" + current_turn +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(game, chessGame.game) && current_turn == chessGame.current_turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(game, current_turn);
    }
}