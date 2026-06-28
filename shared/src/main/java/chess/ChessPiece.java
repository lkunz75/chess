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

    //KING
    public List<List<Integer>> KingMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        // A King can move one block in any direction
        List<List<Integer>> king_move = new ArrayList<>();
        int initial_row = myPosition.getRow() - 1;
        int initial_col = myPosition.getColumn() - 1;
        int row_downright = initial_row + 1;
        int col_downright = initial_col + 1;
        // go diag-right (+,+)
        if (((row_downright < 8) && (col_downright < 8)) && ((board.squares[row_downright][col_downright] == null) || (board.squares[row_downright][col_downright].pieceColor != board.squares[initial_row][initial_col].pieceColor))){
            king_move.add(Arrays.asList(row_downright,col_downright)); // convert to the right thing for later
        }
        //diag down left (+,-)
        int row_downleft = initial_row + 1;
        int col_downleft = initial_col - 1;
        if (((row_downleft < 8) && (col_downleft >= 0)) && ((board.squares[row_downleft][col_downleft] == null) || (board.squares[row_downleft][col_downleft].pieceColor != board.squares[initial_row][initial_col].pieceColor))){
            king_move.add(Arrays.asList(row_downleft,col_downleft)); // convert to the right thing for later
        }
        //diag up-left (-, -)
        int row_upleft = initial_row - 1;
        int col_upleft = initial_col - 1;
        if (((row_upleft >= 0) && (col_upleft >= 0)) && ((board.squares[row_upleft][col_upleft] == null) || (board.squares[row_upleft][col_upleft].pieceColor != board.squares[initial_row][initial_col].pieceColor))){
            king_move.add(Arrays.asList(row_upleft,col_upleft)); // convert to the right thing for later
        }
        //diag up-right (-,+)
        int row_upright = initial_row - 1;
        int col_upright = initial_col + 1;
        if (((row_upright >= 0) && (col_upright < 8)) && ((board.squares[row_upright][col_upright] == null) || (board.squares[row_upright][col_upright].pieceColor != board.squares[initial_row][initial_col].pieceColor))){
            king_move.add(Arrays.asList(row_upright,col_upright)); // convert to the right thing for later
        }
        // just up
        int col_up= initial_col + 1;
        if (((initial_row < 8 && col_up < 8)) && ((board.squares[initial_row][col_up] == null) || (board.squares[initial_row][col_up].pieceColor != board.squares[initial_row][initial_col].pieceColor))){
            king_move.add(Arrays.asList(initial_row,col_up)); // convert to the right thing for later
        }
        // just down
        int col_down= initial_col - 1;
        if (((initial_row < 8 && col_down >= 0)) && ((board.squares[initial_row][col_down] == null) || (board.squares[initial_row][col_down].pieceColor != board.squares[initial_row][initial_col].pieceColor))){
            king_move.add(Arrays.asList(initial_row,col_down)); // convert to the right thing for later
        }
        // just left
        int row_left = initial_row + 1;
        if (((row_left < 8 && initial_col < 8)) && ((board.squares[row_left][initial_col] == null) || (board.squares[row_left][initial_col].pieceColor != board.squares[initial_row][initial_col].pieceColor))){
            king_move.add(Arrays.asList(row_left,initial_col)); // convert to the right thing for later
        }
        // just right
        int row_right = initial_row - 1;
        if (((row_right >= 0 && initial_col < 8)) && ((board.squares[row_right][initial_col] == null) || (board.squares[row_right][initial_col].pieceColor != board.squares[initial_row][initial_col].pieceColor))){
            king_move.add(Arrays.asList(row_right,initial_col)); // convert to the right thing for later
        }
        return king_move;
    }

    public List<List<Integer>> KnightMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        // moves in an L shape
        List<List<Integer>> knight_move = new ArrayList<>();
        int initial_row = myPosition.getRow() - 1;
        int initial_col = myPosition.getColumn() - 1;
        // (-1, +3)
        if (((initial_row - 1 >= 0) && (initial_col + 2 < 8)) && ((board.squares[initial_row - 1][initial_col + 2] == null) || (board.squares[initial_row - 1][initial_col + 2].pieceColor != board.squares[initial_row][initial_col].pieceColor))){
            knight_move.add(Arrays.asList(initial_row - 1,initial_col + 2)); // convert to the right thing for later
        }
        // (+1, +3)
        if (((initial_row + 1 < 8) && (initial_col + 2 < 8)) && ((board.squares[initial_row + 1][initial_col + 2] == null) || (board.squares[initial_row + 1][initial_col + 2].pieceColor != board.squares[initial_row][initial_col].pieceColor))){
            knight_move.add(Arrays.asList(initial_row + 1,initial_col + 2)); // convert to the right thing for later
        }
        // (-1, -3)
        if (((initial_row - 1 >= 0) && (initial_col - 2 >= 0)) && ((board.squares[initial_row - 1][initial_col - 2] == null) || (board.squares[initial_row - 1][initial_col - 2].pieceColor != board.squares[initial_row][initial_col].pieceColor))){
            knight_move.add(Arrays.asList(initial_row - 1,initial_col - 2)); // convert to the right thing for later
        }
        // (+1, -3)
        if (((initial_row + 1 < 8) && (initial_col - 2 >= 0)) && ((board.squares[initial_row + 1][initial_col - 2] == null) || (board.squares[initial_row + 1][initial_col - 2].pieceColor != board.squares[initial_row][initial_col].pieceColor))){
            knight_move.add(Arrays.asList(initial_row + 1,initial_col - 2)); // convert to the right thing for later
        }
        // (+3, -1)
        if (((initial_row + 2 < 8) && (initial_col - 1 >= 0)) && ((board.squares[initial_row + 2][initial_col - 1] == null) || (board.squares[initial_row + 2][initial_col - 1].pieceColor != board.squares[initial_row][initial_col].pieceColor))){
            knight_move.add(Arrays.asList(initial_row + 2,initial_col - 1)); // convert to the right thing for later
        }
        // (+3, +1)
        if (((initial_row + 2 < 8) && (initial_col + 1 < 8)) && ((board.squares[initial_row + 2][initial_col + 1] == null) || (board.squares[initial_row + 2][initial_col + 1].pieceColor != board.squares[initial_row][initial_col].pieceColor))){
            knight_move.add(Arrays.asList(initial_row + 2,initial_col + 1)); // convert to the right thing for later
        }
        // (-3, +1)
        if (((initial_row - 2 >= 0) && (initial_col + 1 < 8)) && ((board.squares[initial_row - 2][initial_col + 1] == null) || (board.squares[initial_row - 2][initial_col + 1].pieceColor != board.squares[initial_row][initial_col].pieceColor))){
            knight_move.add(Arrays.asList(initial_row - 2,initial_col + 1)); // convert to the right thing for later
        }
        // (-3, -1)
        if (((initial_row - 2 >= 0) && (initial_col - 1 >= 0)) && ((board.squares[initial_row - 2][initial_col - 1] == null) || (board.squares[initial_row - 2][initial_col - 1].pieceColor != board.squares[initial_row][initial_col].pieceColor))){
            knight_move.add(Arrays.asList(initial_row - 2,initial_col - 1)); // convert to the right thing for later
        }
        return knight_move;
    }

    public List<List<Integer>> PawnMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        List<List<Integer>> pawn_move = new ArrayList<>();
        int initial_row = myPosition.getRow() - 1;
        int initial_col = myPosition.getColumn() - 1;
        // white add
        if (board.squares[initial_row][initial_col].pieceColor == ChessGame.TeamColor.WHITE){
            if (initial_row == 1 && board.squares[initial_row+1][initial_col] == null && board.squares[initial_row+2][initial_col] == null){
                pawn_move.add(Arrays.asList(initial_row+2, initial_col));
            }
            if ((initial_row + 1 < 8) && (board.squares[initial_row + 1][initial_col] == null)){
                pawn_move.add(Arrays.asList(initial_row + 1,initial_col)); // convert to the right thing for later
            }
            if ((initial_row + 1 < 8 && initial_col + 1 < 8) && board.squares[initial_row+1][initial_col+1]!= null && board.squares[initial_row][initial_col].pieceColor != board.squares[initial_row+1][initial_col+1].pieceColor){
                pawn_move.add(Arrays.asList(initial_row + 1,initial_col+1));
            }
            if ((initial_row + 1 < 8 && initial_col - 1 >= 0) && board.squares[initial_row+1][initial_col-1]!= null && board.squares[initial_row][initial_col].pieceColor != board.squares[initial_row+1][initial_col-1].pieceColor){
                pawn_move.add(Arrays.asList(initial_row + 1,initial_col-1));
            }
        }
        // black subtract
        else {
            if (initial_row == 6 && board.squares[initial_row-1][initial_col] == null && board.squares[initial_row-2][initial_col] == null){
                pawn_move.add(Arrays.asList(initial_row-2, initial_col));
            }
            if ((initial_row - 1 >= 0) && (board.squares[initial_row - 1][initial_col] == null)){
                pawn_move.add(Arrays.asList(initial_row - 1,initial_col)); // convert to the right thing for later
            }
            if ((initial_row - 1 >= 0 && initial_col + 1 < 8) && board.squares[initial_row-1][initial_col+1]!= null && board.squares[initial_row][initial_col].pieceColor != board.squares[initial_row-1][initial_col+1].pieceColor){
                pawn_move.add(Arrays.asList(initial_row - 1,initial_col+1));
            }
            if ((initial_row -1 >= 0 && initial_col - 1 >= 0) && board.squares[initial_row-1][initial_col-1]!= null && board.squares[initial_row][initial_col].pieceColor != board.squares[initial_row-1][initial_col-1].pieceColor){
                pawn_move.add(Arrays.asList(initial_row - 1,initial_col-1));
            }
        }
        return pawn_move;
    }

    public List<List<Integer>> QueenMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        // can move diagonal, forward, backward, side to side
        List<List<Integer>> valid_move = new ArrayList<>();
        //Bishops can move diagonally any number of squares
        int initial_row = myPosition.getRow()-1; // be in array mode
        int initial_col = myPosition.getColumn()-1;
        int row_downright = initial_row + 1;
        int col_downright = initial_col + 1;
        //Same diagonals as the Bishop
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
        // go up
        int row_up = initial_row - 1;
        while ((row_up >= 0) && (initial_col < 8)){
            if (board.squares[row_up][initial_col] == null) {
                valid_move.add(Arrays.asList(row_up,initial_col));
                row_up--;
            }
            else if (board.squares[row_up][initial_col].pieceColor != board.squares[initial_row][initial_col].pieceColor){
                valid_move.add(Arrays.asList(row_up,initial_col));
                break; // get out of the while loop since we hit a piece
            }
            else {
                // hit a friendly colored piece
                break;
            }
        }
        // go down
        int row_down = initial_row + 1;
        while ((row_down < 8) && (initial_col < 8)){
            if (board.squares[row_down][initial_col] == null) {
                valid_move.add(Arrays.asList(row_down,initial_col));
                row_down++;
            }
            else if (board.squares[row_down][initial_col].pieceColor != board.squares[initial_row][initial_col].pieceColor){
                valid_move.add(Arrays.asList(row_down,initial_col));
                break; // get out of the while loop since we hit a piece
            }
            else {
                // hit a friendly colored piece
                break;
            }
        }
        // go right
        int col_right = initial_col + 1;
        while ((initial_row < 8) && (col_right < 8)){
            if (board.squares[initial_row][col_right] == null) {
                valid_move.add(Arrays.asList(initial_row,col_right));
                col_right++;
            }
            else if (board.squares[initial_row][col_right].pieceColor != board.squares[initial_row][initial_col].pieceColor){
                valid_move.add(Arrays.asList(initial_row,col_right));
                break; // get out of the while loop since we hit a piece
            }
            else {
                // hit a friendly colored piece
                break;
            }
        }
        // go left
        int col_left = initial_col - 1;
        while ((initial_row < 8) && (col_left >= 0)){
            if (board.squares[initial_row][col_left] == null) {
                valid_move.add(Arrays.asList(initial_row,col_left));
                col_left--;
            }
            else if (board.squares[initial_row][col_left].pieceColor != board.squares[initial_row][initial_col].pieceColor){
                valid_move.add(Arrays.asList(initial_row,col_left));
                break; // get out of the while loop since we hit a piece
            }
            else {
                // hit a friendly colored piece
                break;
            }
        }
        return valid_move;
    }

    // just moved it for added clarity
    Collection<ChessMove> convert_moves(ChessBoard board, ChessPosition myPosition, List<List<Integer>> valid_moves, ChessPiece piece){
        List<ChessMove> converted_move = new ArrayList<>();
        for (List<Integer> move:valid_moves) {
            int row = move.get(0);
            int col = move.get(1);
            if (piece.getPieceType() == PieceType.PAWN && piece.pieceColor == ChessGame.TeamColor.WHITE && row == 7){
                //promote the pawn when it reaches the end it can become any of these four things!
                ChessPosition ValidMove = new ChessPosition(row+1, col+1);
                ChessMove new_queen = new ChessMove(myPosition, ValidMove, PieceType.QUEEN); //this should be null
                converted_move.add(new_queen);
                ChessMove new_rook = new ChessMove(myPosition, ValidMove, PieceType.ROOK); //this should be null
                converted_move.add(new_rook);
                ChessMove new_bishop = new ChessMove(myPosition, ValidMove, PieceType.BISHOP); //this should be null
                converted_move.add(new_bishop);
                ChessMove new_knight = new ChessMove(myPosition, ValidMove, PieceType.KNIGHT); //this should be null
                converted_move.add(new_knight);
            }
            else if (piece.getPieceType() == PieceType.PAWN && piece.pieceColor == ChessGame.TeamColor.BLACK && row == 0){
                //promote
                ChessPosition ValidMove = new ChessPosition(row+1, col+1);
                ChessMove new_queen = new ChessMove(myPosition, ValidMove, PieceType.QUEEN); //this should be null
                converted_move.add(new_queen);
                ChessMove new_rook = new ChessMove(myPosition, ValidMove, PieceType.ROOK); //this should be null
                converted_move.add(new_rook);
                ChessMove new_bishop = new ChessMove(myPosition, ValidMove, PieceType.BISHOP); //this should be null
                converted_move.add(new_bishop);
                ChessMove new_knight = new ChessMove(myPosition, ValidMove, PieceType.KNIGHT); //this should be null
                converted_move.add(new_knight);
            }
            else {
                ChessPosition validMove = new ChessPosition(row+1, col+1);
                ChessMove new_piece = new ChessMove(myPosition, validMove, null); //this should be null
                converted_move.add(new_piece);
            }
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
        if (piece.getPieceType() == PieceType.KING){
            valid_moves = KingMovesCalculator(board, myPosition);
        }
        if (piece.getPieceType() == PieceType.KNIGHT){
            valid_moves = KnightMovesCalculator(board, myPosition);
        }
        if (piece.getPieceType() == PieceType.PAWN){
            valid_moves = PawnMovesCalculator(board, myPosition);
        }
        if (piece.getPieceType() == PieceType.QUEEN){
            valid_moves = QueenMovesCalculator(board, myPosition);
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
