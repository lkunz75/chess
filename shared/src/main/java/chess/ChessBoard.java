package chess;

import java.util.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] squares = new ChessPiece[8][8]; //remember that its zero indexed! These are just dimensions!
    List<List<Integer>> white_king = new ArrayList<>();
    List<List<Integer>> black_king = new ArrayList<>();
    public ChessBoard() {
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
        if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && squares[position.getRow()-1][position.getColumn()-1].getTeamColor() == ChessGame.TeamColor.WHITE){
            white_king = new ArrayList<>(); // clear the old spot
            white_king.add(Arrays.asList(position.getRow()-1, position.getColumn()-1));
        }

        if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && squares[position.getRow()-1][position.getColumn()-1].getTeamColor() == ChessGame.TeamColor.BLACK){
            black_king = new ArrayList<>();
            black_king.add(Arrays.asList(position.getRow()-1, position.getColumn()-1));
        }
    }

    public List<List<Integer>> return_white_king(){
        return white_king;
    }

    public List<List<Integer>> return_black_king(){
        return black_king;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1]; // just return the piece, remember that its zero indexed!
    }


    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
    //Rook, Knight, Bishop, Queen, King, Bishop, Knight, Rook
        // 8 Pawns
        ChessPiece[][] new_squares = new ChessPiece[8][8];
        squares = new_squares;
        ChessPosition rook_left_posw = new ChessPosition(1, 1); // this is myPosition
        ChessPiece rook_leftw = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        addPiece(rook_left_posw, rook_leftw);
        ChessPosition knight_left_posw = new ChessPosition(1, 2);
        ChessPiece knight_leftw = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        addPiece(knight_left_posw, knight_leftw);
        ChessPosition bishop_left_posw = new ChessPosition(1, 3);
        ChessPiece bishop_leftw = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        addPiece(bishop_left_posw, bishop_leftw);
        ChessPosition queen_left_posw = new ChessPosition(1, 4);
        ChessPiece queen_leftw = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        addPiece(queen_left_posw, queen_leftw);
        ChessPosition king_left_posw = new ChessPosition(1, 5);
        ChessPiece king_leftw = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        addPiece(king_left_posw, king_leftw);
        ChessPosition bishop_right_posw = new ChessPosition(1, 6);
        ChessPiece bishop_rightw = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        addPiece(bishop_right_posw, bishop_rightw);
        ChessPosition knight_right_posw = new ChessPosition(1, 7);
        ChessPiece knight_rightw = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        addPiece(knight_right_posw, knight_rightw);
        ChessPosition rook_right_posw = new ChessPosition(1, 8);
        ChessPiece rook_rightw = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        addPiece(rook_right_posw, rook_rightw);
        ChessPosition pawn_posw1 = new ChessPosition(2, 1);
        ChessPiece pawn_w1 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        addPiece(pawn_posw1, pawn_w1);
        ChessPosition pawn_posw2 = new ChessPosition(2, 2);
        ChessPiece pawn_w2 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        addPiece(pawn_posw2, pawn_w2);
        ChessPosition pawn_posw3 = new ChessPosition(2, 3);
        ChessPiece pawn_w3 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        addPiece(pawn_posw3, pawn_w3);
        ChessPosition pawn_posw4 = new ChessPosition(2, 4);
        ChessPiece pawn_w4 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        addPiece(pawn_posw4, pawn_w4);
        ChessPosition pawn_posw5 = new ChessPosition(2, 5);
        ChessPiece pawn_w5 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        addPiece(pawn_posw5, pawn_w5);
        ChessPosition pawn_posw6 = new ChessPosition(2, 6);
        ChessPiece pawn_w6 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        addPiece(pawn_posw6, pawn_w6);
        ChessPosition pawn_posw7 = new ChessPosition(2, 7);
        ChessPiece pawn_w7 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        addPiece(pawn_posw7, pawn_w7);
        ChessPosition pawn_posw8 = new ChessPosition(2, 8);
        ChessPiece pawn_w8 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        addPiece(pawn_posw8, pawn_w8);

        // keep the same order for black
        ChessPosition rook_left_posb = new ChessPosition(8, 8); // this is myPosition
        ChessPiece rook_leftb = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        addPiece(rook_left_posb, rook_leftb);
        ChessPosition knight_left_posb = new ChessPosition(8, 7);
        ChessPiece knight_leftb = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        addPiece(knight_left_posb, knight_leftb);
        ChessPosition bishop_left_posb = new ChessPosition(8, 6);
        ChessPiece bishop_leftb = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        addPiece(bishop_left_posb, bishop_leftb);
        ChessPosition queen_left_posb = new ChessPosition(8, 4);
        ChessPiece queen_leftb = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        addPiece(queen_left_posb, queen_leftb);
        ChessPosition king_left_posb = new ChessPosition(8, 5);
        ChessPiece king_leftb = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        addPiece(king_left_posb, king_leftb);
        ChessPosition bishop_right_posb = new ChessPosition(8, 3);
        ChessPiece bishop_rightb = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        addPiece(bishop_right_posb, bishop_rightb);
        ChessPosition knight_right_posb = new ChessPosition(8, 2);
        ChessPiece knight_rightb = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        addPiece(knight_right_posb, knight_rightb);
        ChessPosition rook_right_posb = new ChessPosition(8, 1);
        ChessPiece rook_rightb = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        addPiece(rook_right_posb, rook_rightb);
        ChessPosition pawn_posb1 = new ChessPosition(7, 1);
        ChessPiece pawn_b1 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        addPiece(pawn_posb1, pawn_b1);
        ChessPosition pawn_posb2 = new ChessPosition(7, 2);
        ChessPiece pawn_b2 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        addPiece(pawn_posb2, pawn_b2);
        ChessPosition pawn_posb3 = new ChessPosition(7, 3);
        ChessPiece pawn_b3 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        addPiece(pawn_posb3, pawn_b3);
        ChessPosition pawn_posb4 = new ChessPosition(7, 4);
        ChessPiece pawn_b4 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        addPiece(pawn_posb4, pawn_b4);
        ChessPosition pawn_posb5 = new ChessPosition(7, 5);
        ChessPiece pawn_b5 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        addPiece(pawn_posb5, pawn_b5);
        ChessPosition pawn_posb6 = new ChessPosition(7, 6);
        ChessPiece pawn_b6 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        addPiece(pawn_posb6, pawn_b6);
        ChessPosition pawn_posb7 = new ChessPosition(7, 7);
        ChessPiece pawn_b7 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        addPiece(pawn_posb7, pawn_b7);
        ChessPosition pawn_posb8 = new ChessPosition(7, 8);
        ChessPiece pawn_b8 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        addPiece(pawn_posb8, pawn_b8);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "squares=" + Arrays.toString(squares) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }
}

