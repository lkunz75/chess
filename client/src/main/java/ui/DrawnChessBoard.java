package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Random;

import static ui.EscapeSequences.*;

public class DrawnChessBoard {
    // Board dimensions.
    private static final int HEIGHT = 8;
    private static final int WIDTH = 8;
    private static final String[] BLACK_PLAYERS = {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN,
            BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK};
    private static final String[] WHITE_PLAYERS = {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN,
            WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK};


    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        String[] headers = {};
//        if ("WHITE".equals(args")) {
//            headers = new String[]{"a", "b", "c", "b", "e", "f", "g", "h"};
//            drawHeaders(out, headers);
//            drawInitialChessBoard(out);
//            drawHeaders(out, headers);
//        }
        headers = new String[]{"a", "b", "c", "b", "e", "f", "g", "h"};
        drawHeaders(out, headers);
        drawBoard(out);
        drawHeaders(out, headers);
        out.print(SET_BG_COLOR_BLUE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeaders(PrintStream out, String[] headers) {
        setGray(out);
        out.print("  ");
        for (int boardCol = 0; boardCol < WIDTH; ++boardCol) {
            drawHeader(out, headers[boardCol]);
        }
        out.print("  ");
        out.print(RESET_BG_COLOR);
        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        out.print(" ");
        printHeaderText(out, headerText);
        out.print("  ");
    }

    private static void drawSideHeader(PrintStream out, int rowNumber) {
        setGray(out);
        out.print(SET_TEXT_COLOR_GREEN);
        out.print(" " + rowNumber + " ");
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_GREEN);
        out.print(player);
        setGray(out);
    }

    private static void drawBoard(PrintStream out) {
        for (int row = 0; row < HEIGHT; row++) {
            drawSideHeader(out, 8-row);
            for (int col = 0; col < WIDTH; col++) {
                if ((row + col) % 2 == 0) {
                    setBlue(out);
                } else {
                    setWhite(out);
                }
                if (row == 0) {
                    out.print(SET_TEXT_COLOR_BLACK);
                    out.print(SET_TEXT_BOLD);
                    out.print(WHITE_PLAYERS[col]);
                }
                else if (row == 1) {
                    out.print(SET_TEXT_COLOR_BLACK);
                    out.print(SET_TEXT_BOLD);
                    out.print(WHITE_PAWN);
                }
                else if (row == 6) {
                    out.print(SET_TEXT_COLOR_MAGENTA);
                    out.print(SET_TEXT_BOLD);
                    out.print(BLACK_PAWN);
                }
                else if (row == 7) {
                    out.print(SET_TEXT_COLOR_MAGENTA);
                    out.print(SET_TEXT_BOLD);
                    out.print(BLACK_PLAYERS[col]);
                }
                else {
                    out.print(EMPTY);
                }
            }
            drawSideHeader(out, 8-row);
            out.print(RESET_TEXT_COLOR);
            out.print(RESET_BG_COLOR);
            out.println(); // gets it to the next line
        }
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setBlue(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_DARK_GREY);
    }

    private static void setGray(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_LIGHT_GREY);
    }
}
