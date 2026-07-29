package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class DrawnChessBoard {
    private static final int HEIGHT = 8;
    private static final int WIDTH = 8;
    private static final String[] BLACK_PLAYERS = {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP,
            BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK};
    private static final String[] WHITE_PLAYERS = {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP,
            WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK};
    private static String[] startPlayers;
    private static String[] opposingPlayers;
    private static String startPawn;
    private static String opposingPawn;
    private static String startColor = null;

    public static void chessBoard(String color) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        String[] headers = {};
        if (color.equals("BLACK")) {
            headers = new String[]{"h", "g", "f", "e", "d", "c", "b", "a"};
            startPlayers = BLACK_PLAYERS;
            startPawn = BLACK_PAWN;
            opposingPawn = WHITE_PAWN;
            opposingPlayers = WHITE_PLAYERS;
            startColor = color;
        }
        if (color.equals("WHITE")) {
            headers = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
            startPlayers = WHITE_PLAYERS;
            opposingPlayers = BLACK_PLAYERS;
            startPawn = WHITE_PAWN;
            opposingPawn = BLACK_PAWN;
            startColor = null;
        }
        drawHeaders(out, headers);
        drawBoard(out);
        drawHeaders(out, headers);
        startPlayers = null;
        opposingPlayers = null;
        startPawn = null;
        opposingPawn = null;
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
        if (startColor != null) {
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

    private static void drawBoard(PrintStream out) {
        for (int row = 0; row < HEIGHT; row++) {
            drawSideHeader(out, 8-row);
            for (int col = 0; col < WIDTH; col++) {
                if ((row + col) % 2 == 0) {
                    setWhite(out);

                } else {
                    setDarkGray(out);
                }
                if (startColor != null) {
                    playersColor(out, row, col, SET_TEXT_COLOR_MAGENTA, SET_TEXT_COLOR_BLACK);
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

    private static void playersColor(PrintStream out, int row, int col, String opposingColor, String homeColor) {
        if (row == 0) {
            out.print(opposingColor);
            out.print(SET_TEXT_BOLD);
            out.print(opposingPlayers[col]);
        } else if (row == 1) {
            out.print(opposingColor);
            out.print(SET_TEXT_BOLD);
            out.print(opposingPawn);
        } else if (row == 6) {
            out.print(homeColor);
            out.print(SET_TEXT_BOLD);
            out.print(startPawn);
        } else if (row == 7) {
            out.print(homeColor);
            out.print(SET_TEXT_BOLD);
            out.print(startPlayers[col]);
        } else {
            out.print(EMPTY);
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
}
