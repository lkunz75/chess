package chess;

import java.util.*;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
// A lot of the syntax took time to learn
public class ChessGame {
    ChessBoard game = new ChessBoard();
    TeamColor currentTurn = TeamColor.WHITE;

    public ChessGame() {
        game.resetBoard(); // here is where you call it!! Calling it above makes it fail, and we only want to reset the board once each time
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTurn = team;
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
    // and do Arrays.copyOf to copy the chess board row by row
    // then finally putting the 2d array into the cloned ChessBoard.
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
        List<List<Integer>> valid_moves = new ArrayList<>();
        Collection<ChessMove> converted_moves = new ArrayList<>();
        int col = startPosition.getColumn() - 1;
        int row = startPosition.getRow() - 1;
        if (game.squares[row][col] == null) {
            return null;
        }
        // know king placement and moves
        ChessPiece piece = game.squares[row][col];
        TeamColor color = piece.getTeamColor();
        Collection<ChessMove> moves = piece.pieceMoves(game, startPosition);

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
                    game.whiteKing = updated_wk;
                }
                if (color == TeamColor.BLACK){
                    List<List<Integer>> updated_bk = new ArrayList<>();
                    updated_bk.add(Arrays.asList(end.getRow()-1, end.getColumn()-1));
                    game.blackKing = updated_bk;
                }
            }
            if (!isInCheck(color)) {
                valid_moves.add(Arrays.asList(end.getRow()-1, end.getColumn()-1));
                converted_moves = piece.convertMoves(startPosition, valid_moves, piece);
            }
            // resetting the changes
            game.squares[start.getRow() - 1][start.getColumn() - 1] = piece;
            game.squares[end.getRow() - 1][end.getColumn() - 1] = promoted;
            if (game.squares[start.getRow() - 1][start.getColumn() - 1].getPieceType() == ChessPiece.PieceType.KING){
                if (color == TeamColor.WHITE){
                    List<List<Integer>> updated_wk = new ArrayList<>();
                    updated_wk.add(Arrays.asList(start.getRow()-1, start.getColumn()-1));
                    game.whiteKing = updated_wk;
                }
                if (color == TeamColor.BLACK){
                    List<List<Integer>> updated_bk = new ArrayList<>();
                    updated_bk.add(Arrays.asList(start.getRow()-1, start.getColumn()-1));
                    game.blackKing = updated_bk;
                }
            }
        }
        game.squares = original.data; // change it back
        return converted_moves;
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
        ChessPiece piece = null;
        if (board.squares[start_row][start_col] == null) {throw new InvalidMoveException();}
            // check color so we know whose turn is next
        TeamColor color = board.squares[start_row][start_col].getTeamColor();
        if (color != currentTurn){throw new InvalidMoveException();}
        Collection<ChessMove> valid_moves = validMoves(start);
        for (ChessMove moves : valid_moves) {
            ChessPosition move_end = moves.getEndPosition();
            //using .equals allows java to compare
            if (endposition.equals(move_end)) {
                piece = board.squares[start_row][start_col];
                if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                    ChessPiece.PieceType promote = move.getPromotionPiece();
                    if (promote != null) {
                        piece = new ChessPiece(color, promote);
                    }
                }
                board.squares[endposition.getRow() - 1][endposition.getColumn() - 1] = piece;
                board.squares[start_row][start_col] = null;
                if (color == TeamColor.WHITE) {
                    currentTurn = TeamColor.BLACK;
                }
                else {
                    currentTurn = TeamColor.WHITE;
                }
                return;
            }
        }
        throw new InvalidMoveException();
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
        ChessPosition king_position = getKingPosition(teamColor);
        // pieceMoves(ChessBoard board, ChessPosition myPosition)
        while (row < 8) {
            while (col < 8) {
                ChessPosition board_spot = new ChessPosition(row + 1, col + 1);
                ChessPiece piece = game.squares[row][col];
                // check opposing team
                if (piece == null || piece.getTeamColor() == teamColor) {
                    col++;
                    continue;
                }
                Collection<ChessMove> list_of_moves = piece.pieceMoves(game, board_spot);
                for (ChessMove move : list_of_moves) {
                    ChessPosition endPosition = move.getEndPosition();
                    if (endPosition.equals(king_position)) {
                        return true;
                    } // is in check
                }
                col++;
            }
            col = 0;
            row++;
        }
        return false; // went through it all without hitting the king
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition king_position = getKingPosition(teamColor);
        Collection<ChessMove> moves = validMoves(king_position);
        if (moves.isEmpty()) { // checks if the other pieces can protect it
            return chekingHelper(teamColor);
        }
        else {return false;}
    }

    public boolean chekingHelper(TeamColor teamColor){
        int row = 0;
        int col = 0;
        while (row < 8) {
            while (col < 8) {
                ChessPosition board_spot = new ChessPosition(row + 1, col + 1);
                // indexed properly
                ChessPiece piece = game.squares[row][col];
                if (piece == null || piece.getTeamColor() != teamColor) {
                    col++;
                    continue;
                }
                Collection<ChessMove> list_of_moves = piece.pieceMoves(game, board_spot);
                for (ChessMove move : list_of_moves) {
                    ChessPosition startPosition = move.getStartPosition();
                    Collection<ChessMove> valid_moves = validMoves(startPosition);
                    if (!valid_moves.isEmpty()){
                        return false;
                    }
                }
                col++;
            }
            col = 0;
            row++;
        }
        return true;
    }

    public ChessPosition getKingPosition(TeamColor teamColor){
        List<List<Integer>> king = new ArrayList<>();
        int k_row = 0;
        int k_col = 0;
        if (teamColor == TeamColor.BLACK) {
            king = game.returnBlackKing();
        }
        if (teamColor == TeamColor.WHITE) {
            king = game.returnWhiteKing();
        }
        for (List<Integer> k : king) {
            k_row = k.get(0);
            k_col = k.get(1);
        }
        return new ChessPosition(k_row + 1, k_col + 1);
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
        return chekingHelper(teamColor);
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
                ", current_turn=" + currentTurn +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(game, chessGame.game) && currentTurn == chessGame.currentTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(game, currentTurn);
    }
}