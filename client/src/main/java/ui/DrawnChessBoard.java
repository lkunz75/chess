package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.WHITE_KING;
import static ui.EscapeSequences.WHITE_PAWN;

public class DrawnChessBoard {
    private static final int HEIGHT = 8;
    private static final int WIDTH = 8;
    private static String startColor = null;
    private static ChessBoard game = null;


    // ChessPiece[][] squares = new ChessPiece[8][8]; is game
    public static void chessBoard(String color, ChessBoard chessGame, Collection<ChessMove> moves) {
        game = chessGame;
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        String[] headers = {};
        if (color.equals("BLACK")) {
            headers = new String[]{"h", "g", "f", "e", "d", "c", "b", "a"};
            startColor = color;
        }
        if (color.equals("WHITE")) {
            headers = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
            startColor = color;
        }
        drawHeaders(out, headers);
        drawBoard(out, moves);
        drawHeaders(out, headers);
    }

    private static void drawHeaders(PrintStream out, String[] headers) {
        setGray(out);
        out.print("  ");
        out.print(SET_TEXT_BOLD);
        for (int boardCol = 0; boardCol < WIDTH; ++boardCol) {
            if (boardCol == 3) {
                out.print(" ");
                drawHeader(out, headers[boardCol]);
            }
            else {
                out.print(" ");
                drawHeader(out, headers[boardCol]);
                out.print(" ");
            }
        }
        out.print("   ");
        out.print(RESET_BG_COLOR);
        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        printHeaderText(out, headerText);
    }

    private static void drawSideHeader(PrintStream out, int rowNumber) {
        setGray(out);
        out.print(SET_TEXT_COLOR_BLACK);
        if (startColor.equals("BLACK")) {
            out.print(" " + (9-rowNumber) + " ");
        }
        else{
            out.print(" " + rowNumber + " ");
        }
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(" ");
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(player);
        setGray(out);
    }

    private static void setHelp(PrintStream out, Collection<ChessMove> moves, Integer row, Integer col) {
        for (ChessMove move: moves) {
            ChessPosition endPosition = move.getEndPosition();
            if (startColor.equals("BLACK")) {
                if (col == 7 - endPosition.getColumn() && row == 7 - endPosition.getRow()) {
                    setBlue(out);
                }
            }
            else {
                if (col == endPosition.getColumn()-1 && row == endPosition.getRow()-1){
                    setBlue(out);
                }
            }
        }
    }
    private static void drawBoard(PrintStream out, Collection<ChessMove> moves) {
        for (int row = 0; row < HEIGHT; row++) {
            drawSideHeader(out, 8-row);
            for (int col = 0; col < WIDTH; col++) {
                if ((row + col) % 2 == 0) {
                    setWhite(out);

                } else {
                    setDarkGray(out);
                }
                setHelp(out, moves, row, col);
                if (startColor.equals("BLACK")) {
                    playersColor(out, row, col, SET_TEXT_COLOR_BLACK, SET_TEXT_COLOR_MAGENTA);
                }
                else {
                    playersColor(out, row, col, SET_TEXT_COLOR_MAGENTA, SET_TEXT_COLOR_BLACK);
                }
            }
            drawSideHeader(out, 8-row);
            out.print(RESET_TEXT_COLOR);
            out.print(RESET_BG_COLOR);
            out.println(); // gets it to the next line
        }
    }

    public static String pieceType(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        }
        else if (piece.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            return settingPieces(piece, BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_KING, BLACK_QUEEN, BLACK_PAWN);
        }
        else if (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            return settingPieces(piece, WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_KING, WHITE_QUEEN, WHITE_PAWN);
        }
        return EMPTY;
    }

    private static String settingPieces(ChessPiece piece, String rook, String knight, String bishop, String king, String queen, String pawn) {
        if (piece.getPieceType().equals(ChessPiece.PieceType.ROOK)) {
            return rook;
        }
        if (piece.getPieceType().equals(ChessPiece.PieceType.KNIGHT)) {
            return knight;
        }
        if (piece.getPieceType().equals(ChessPiece.PieceType.BISHOP)) {
            return bishop;
        }
        if (piece.getPieceType().equals(ChessPiece.PieceType.QUEEN)) {
            return queen;
        }
        if (piece.getPieceType().equals(ChessPiece.PieceType.KING)) {
            return king;
        }
        if (piece.getPieceType().equals(ChessPiece.PieceType.PAWN)) {
            return pawn;
        }
        return EMPTY;
    }

    private static void playersColor(PrintStream out, int row, int col, String opposingColor, String homeColor) {
        if (startColor.equals("BLACK")) {
            row = 7 - row; // zero indexed!
            col = 7 - col;
        }
        String piece = pieceType(game.squares[row][col]);
        if (piece.equals(EMPTY)) {
            out.print(EMPTY);
        } else if ((game.squares[row][col].getTeamColor().equals(ChessGame.TeamColor.WHITE) && startColor.equals("WHITE")) ||
                (game.squares[row][col].getTeamColor().equals(ChessGame.TeamColor.BLACK) && startColor.equals("BLACK"))) {
            out.print(homeColor);
            out.print(SET_TEXT_BOLD);
            out.print(piece);
        } else {
            out.print(opposingColor);
            out.print(SET_TEXT_BOLD);
            out.print(piece);
        }
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setDarkGray(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_DARK_GREY);
    }

    private static void setGray(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_LIGHT_GREY);
    }

    private static void setBlue(PrintStream out) {
        out.print(SET_BG_COLOR_BLUE);
        out.print(SET_TEXT_COLOR_BLUE);
    }

//    public static void main(String[] args) {
//        ChessBoard game = new ChessBoard();
//        game.resetBoard();
//        chessBoard("WHITE", game);
//    }
}
