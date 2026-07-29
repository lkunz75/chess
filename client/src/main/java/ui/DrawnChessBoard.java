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
    private static String[] START_PLAYERS;
    private static String[] OPPOSING_PLAYERS;
    private static String START_PAWN;
    private static String OPPOSING_PAWN;
    private static String START_COLOR = null;

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        String[] headers = {};
        if (args.length >= 1 && "BLACK".equals(args[0])) {
            headers = new String[]{"h", "g", "f", "e", "d", "c", "b", "a"};
            START_PLAYERS = BLACK_PLAYERS;
            START_PAWN = BLACK_PAWN;
            OPPOSING_PAWN = WHITE_PAWN;
            OPPOSING_PLAYERS = WHITE_PLAYERS;
            START_COLOR = "BLACK";
        }
        else {
        headers = new String[]{"a", "b", "c", "b", "e", "f", "g", "h"};
        START_PLAYERS = WHITE_PLAYERS;
        OPPOSING_PLAYERS = BLACK_PLAYERS;
        START_PAWN = WHITE_PAWN;
        OPPOSING_PAWN = BLACK_PAWN;
        }
        drawHeaders(out, headers);
        drawBoard(out);
        drawHeaders(out, headers);
        out.print(SET_BG_COLOR_BLUE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeaders(PrintStream out, String[] headers) {
        setGray(out);
        out.print("  ");
        out.print(SET_TEXT_BOLD);
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
        out.print(SET_TEXT_COLOR_BLACK);
        if (START_COLOR != null) {
            out.print(" " + (9-rowNumber) + " ");
        }
        else{
            out.print(" " + rowNumber + " ");
        }
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(player);
        setGray(out);
    }

    private static void drawBoard(PrintStream out) {
        for (int row = 0; row < HEIGHT; row++) {
            drawSideHeader(out, 8-row);
            for (int col = 0; col < WIDTH; col++) {
                if ((row + col) % 2 == 0) {
                    setWhite(out);
                } else {
                    setBlue(out);
                }
                if (START_COLOR != null) {
                    playersColor(out, row, col, EscapeSequences.SET_TEXT_COLOR_MAGENTA, EscapeSequences.SET_TEXT_COLOR_BLACK);
                }
                else {
                    playersColor(out, row, col, SET_TEXT_COLOR_BLACK, SET_TEXT_COLOR_MAGENTA);
                }
            }
            drawSideHeader(out, 8-row);
            out.print(RESET_TEXT_COLOR);
            out.print(RESET_BG_COLOR);
            out.println(); // gets it to the next line
        }
    }

    private static void playersColor(PrintStream out, int row, int col, String setTextColorBlack, String setTextColorMagenta) {
        if (row == 0) {
            out.print(setTextColorBlack);
            out.print(SET_TEXT_BOLD);
            out.print(OPPOSING_PLAYERS[col]);
        } else if (row == 1) {
            out.print(setTextColorBlack);
            out.print(SET_TEXT_BOLD);
            out.print(OPPOSING_PAWN);
        } else if (row == 6) {
            out.print(setTextColorMagenta);
            out.print(SET_TEXT_BOLD);
            out.print(START_PAWN);
        } else if (row == 7) {
            out.print(setTextColorMagenta);
            out.print(SET_TEXT_BOLD);
            out.print(START_PLAYERS[col]);
        } else {
            out.print(EMPTY);
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
