package chess;

import java.util.*;


/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    // listing out the different kinds of moves for each piece type
    public List<List<Integer>> BishopMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        List<List<Integer>> valid_move = new ArrayList<>();
        //Bishops can move diagonally any number of squares
        int initial_row = myPosition.getRow()-1; // be in array mode
        int initial_col = myPosition.getColumn()-1;
        int row_downright = initial_row + 1;
        int col_downright = initial_col + 1;
        // go down-right (+,+)
        while ((row_downright < 8) && (col_downright < 8)){
            if (board.squares[row_downright][col_downright] == null) {
                valid_move.add(Arrays.asList(row_downright,col_downright)); // convert to the right thing for later
                row_downright++;
                col_downright++;
            }
            else if (board.squares[row_downright][col_downright].pieceColor != board.squares[initial_row][initial_col].pieceColor){
                valid_move.add(Arrays.asList(row_downright,col_downright));
                break; // get out of the while loop since we hit a piece
            }
            else {
                // hit a friendly colored piece
                break;
            }
        }
        // go down-left (+, -)
        int row_downleft = initial_row + 1;
        int col_downleft = initial_col - 1;
        while ((row_downleft < 8) && (col_downleft >= 0)){
            if (board.squares[row_downleft][col_downleft] == null) {
                valid_move.add(Arrays.asList(row_downleft,col_downleft));
                row_downleft++;
                col_downleft--;
            }
            else if (board.squares[row_downleft][col_downleft].pieceColor != board.squares[initial_row][initial_col].pieceColor){
                valid_move.add(Arrays.asList(row_downleft,col_downleft));
                break; // get out of the while loop since we hit a piece
            }
            else {
                // hit a friendly colored piece
                break;
            }
        }

        //go up-left (-, -)
        int row_upleft = initial_row - 1;
        int col_upleft = initial_col - 1;
        while ((row_upleft >= 0) && (col_upleft >= 0)){
            if (board.squares[row_upleft][col_upleft] == null) {
                valid_move.add(Arrays.asList(row_upleft,col_upleft));
                row_upleft--;
                col_upleft--;
            }
            else if (board.squares[row_upleft][col_upleft].pieceColor != board.squares[initial_row][initial_col].pieceColor){
                valid_move.add(Arrays.asList(row_upleft,col_upleft));
                break; // get out of the while loop since we hit a piece
            }
            else {
                // hit a friendly colored piece
                break;
            }
        }

        //go up-right (-, +)
        int row_upright = initial_row - 1;
        int col_upright = initial_col + 1;
        while ((row_upright >= 0) && (col_upright < 8)){
            if (board.squares[row_upright][col_upright] == null) {
                valid_move.add(Arrays.asList(row_upright,col_upright));
                row_upright--;
                col_upright++;
            }
            else if (board.squares[row_upright][col_upright].pieceColor != board.squares[initial_row][initial_col].pieceColor){
                valid_move.add(Arrays.asList(row_upright,col_upright));
                break; // get out of the while loop since we hit a piece
            }
            else {
                // hit a friendly colored piece
                break;
            }
        }
        return valid_move;
    }


    Collection<ChessMove> convert_moves(ChessBoard board, ChessPosition myPosition, List<List<Integer>> valid_moves, ChessPiece piece){
        List<ChessMove> converted_move = new ArrayList<>();
        for (List<Integer> move:valid_moves) {
            int row = move.get(0);
            int col = move.get(1);
            ChessPosition validMove = new ChessPosition(row+1, col+1);
            ChessMove new_piece = new ChessMove(myPosition, validMove, null); //this should be null
            converted_move.add(new_piece);
        }
        return converted_move;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        List<List<Integer>> valid_moves = new ArrayList<>();
        if (piece.getPieceType() == PieceType.BISHOP){
             valid_moves = BishopMovesCalculator(board, myPosition);
        }
        return convert_moves(board, myPosition, valid_moves, piece);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
