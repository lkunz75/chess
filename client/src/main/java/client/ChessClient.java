package client;

import chess.*;
import com.google.gson.Gson;
import model.GameInfo;
import service.gamerequests.*;
import service.userrequests.*;
import ui.DrawnChessBoard;
import websocket.messages.ErrorMessages;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.*;
import static java.lang.System.out;
import static ui.EscapeSequences.*;

// DO I NEED TO HAVE A DELETE DATA? They shouldn't have access to that right?

public class ChessClient implements NotificationHandler {
    private State state = State.SIGNEDOUT;
    private final ServerFacade server;
    private String authToken;
    private String username;
    private String currentColor = "WHITE";
    private final WebSocketFacade ws;
    private Integer currentID = 0;
    private ChessGame game = new ChessGame();

    public ChessClient(String serverUrl) throws Exception {
        server = new ServerFacade(serverUrl);
        ws = new WebSocketFacade(serverUrl, this);
    }

    public void run() {
        out.println("♕ Welcome to 240 Chess. Type help to get started. ♕");
        Scanner scanner = new Scanner(System.in);
        var result = " ";
        while (!"quit".equals(result)) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = eval(line);
                out.print(SET_TEXT_COLOR_MAGENTA  + result);
            } catch (Exception e) {
                var message = e.toString();
                out.print(message);
            }
        }
        username = null;
        state = State.SIGNEDOUT;
        authToken = null;
        out.println();
    }

    public String eval(String input) {
        try {
            System.out.println(RESET_BG_COLOR);
            String [] tokens = input.toLowerCase().split(" "); // helps avoid random crashes
            String command = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length); // ordered without the command
            // System.out.println(Arrays.toString(params));
            return switch (command) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "redraw" -> redraw();
                case "highlight" -> highlight(params);
                case "move" -> move(params);
                case "leave" -> leave(params);
                case "resign" -> resign();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private void printPrompt() {
        out.print("\n" + RESET_BG_COLOR + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    @Override
    public void notify(ServerMessage notification) {
        // System.out.println(SET_TEXT_COLOR_RED + new Gson().toJson(notification));
        if (Objects.equals(notification.getServerMessageType(), ServerMessage.ServerMessageType.LOAD_GAME)) {
            LoadGameMessage loadGameMessage = (LoadGameMessage) notification;
            game = loadGameMessage.getChessGame();
        }
        if (Objects.equals(notification.getServerMessageType(), ServerMessage.ServerMessageType.ERROR)) {
            System.out.println(SET_BG_COLOR_RED + new Gson().toJson(((ErrorMessages) notification).getMessage()));
        }
        if (Objects.equals(notification.getServerMessageType(), ServerMessage.ServerMessageType.NOTIFICATION)) {
            System.out.println(SET_BG_COLOR_RED + new Gson().toJson(((NotificationMessage) notification).getMessage()));
        }
        printPrompt();
    }

    public String register(String...params) throws Exception {
        if (params.length >= 3) {
            RegisterRequest registerRequest = new RegisterRequest(params[0], params[1], params[2]);
            RegisterResult registerResult = server.register(registerRequest);
            state = State.SIGNEDIN;
            username = String.join("-", registerResult.username());
            authToken = registerResult.authToken();
            return String.format("You signed in as %s", username);
        }
        throw new Exception("Expected: <username>, <password>, <email>");
    }

    public String login(String...params) throws Exception {
        if (params.length >= 2) {
            LoginResult loginResult = server.login(new LoginRequest(params[0], params[1]));
            authToken = loginResult.authToken();
            state = State.SIGNEDIN;
            username = String.join("-", params[0]);
            // Do I need ws here?
            return String.format("You signed in as %s", username);
        }
        throw new Exception("Expected: <username>, <password>");
    }

    public String logout() throws Exception {
        state = State.SIGNEDOUT;
        username = null;
        server.logout(new LogoutRequest(authToken));
        authToken = null;
        return "You are now signed out.";
    }

    public String create(String...params) throws Exception {
        assertSignedIn();
        if (params.length >= 1) {
            server.create(new CreateRequest(authToken, params[0]));
            return String.format("Game created %s", params[0]);
        }
        throw new Exception("Expected <gameName>");
    }

    public String list() throws Exception {
        assertSignedIn();
        ListResult games = server.list(new ListRequest(authToken));
        var result = new StringBuilder();
        var gson = new Gson();
        for (GameInfo game : games.games()) {
            String blackUsername = "blackUsername: " + game.blackUsername();
            String whiteUsername = "whiteUsername: " + game.whiteUsername();
            if (blackUsername.contains("null")) {
                blackUsername = "blackUsername: available";
            }
            if (whiteUsername.contains("null")) {
                whiteUsername = "whiteUsername: available";
            }
            String gameID = "gameID: " + game.gameID();
            String gameName = "gameName: " + game.gameName();
            String prettyGames = "[" + gameID + ", " + blackUsername + ", " + whiteUsername + ", " + gameName + "]\n";
            result.append(prettyGames);
        }
        return result.toString();
    }

    public String observe(String...params) throws Exception {
        int gameID = 0;
        if (params.length > 0) {
            try {
                gameID = Integer.parseInt(params[0]);
            } catch (NumberFormatException e) {
                throw new Exception("Error: gameID must be an integer!");
            }
            ws.connect(authToken, gameID, null, game);
            out.print(ERASE_SCREEN);
            DrawnChessBoard.chessBoard("WHITE", game.getBoard(), new ArrayList<>());
            state = State.OBSERVING;
            return String.format("Observing Game %s", params[0]);
        }
        throw new Exception("Expected: <gameID>");
    }

    public String join(String...params) throws Exception {
        assertSignedIn();
        int gameID = 0;
        if (params.length >= 2) {
            try {
                gameID = Integer.parseInt(params[0]);
            } catch (NumberFormatException e) {
                throw new Exception("Error: gameID must be an integer!");
            }
            String color = params[1].toUpperCase();
            server.join(new JoinRequest(authToken, color, gameID));
            state = State.JOINEDGAME;
            ws.connect(authToken, gameID, null, game);
            currentColor = color;
            redraw();
            out.print(ERASE_SCREEN);
            currentID = gameID;
            return String.format("Joined game %s, as %s", gameID, color);
        }
        throw new Exception("Expected: <gameID>, <WHITE|BLACK>");
    }

    public String redraw() throws Exception {
        if (currentColor.equals("BLACK")) {
            DrawnChessBoard.chessBoard(currentColor, game.getBoard(), new ArrayList<>());
        }
        else {
            DrawnChessBoard.chessBoard("WHITE", game.getBoard(), new ArrayList<>());
        }
        return "";
    }

    public String highlight(String...params) {
        if (params.length > 0) {
            int rowStart = convertPosition(params[0]);
            int colStart = params[0].charAt(1) - '0';
            ChessBoard board = game.getBoard();
//            System.out.println(rowStart);
//            System.out.print(colStart);
//            System.out.println(board.squares[colStart-1][rowStart-1].getPieceType());
            Collection<ChessMove> moves = game.validMoves(new ChessPosition(colStart, rowStart));
            DrawnChessBoard.chessBoard(currentColor, game.getBoard(), moves);
            return "Valid moves have been highlighted";
        }
        return "Error: Expected <CHESS POSITION> like <e7>";
    }

    public String leave(String...params) throws Exception {
        ws.leave(authToken, currentID, null, game);
        currentID = 0;
        currentColor = "WHITE";
        state = State.SIGNEDIN;
        return "You left the game.";
    }

    public String move(String...params) throws Exception {
        if (params.length >= 3) {
            int rowStart = convertPosition(params[0]);
            int rowEnd = convertPosition(params[1]);
            // quickly converts into an integer
            int colStart = params[0].charAt(1) - '0';
            int colEnd = params[1].charAt(1) - '0';
            ChessPosition chessStartPosition;
            ChessPosition chessEndPosition;
            if (colStart > 8 || colEnd > 8 || rowStart > 8 || rowEnd > 8) {
                return "Error! Must be a valid move.";
            }
            chessStartPosition = new ChessPosition(colStart, rowStart);
            chessEndPosition = new ChessPosition(colEnd, rowEnd);
            ChessPiece.PieceType promotion = convertPromotion(params[2].toUpperCase());
            ws.makeMove(authToken, currentID, new ChessMove(chessStartPosition, chessEndPosition, promotion), game); // sends updated move
            redraw();
            return " ";
        }
        return "Error: Must provide <CURRENT POSITION> <MOVE POSITION> <PROMOTION PIECE>. (Promotion is for pawns when they reach the end).";
    }

    public String resign() throws Exception {
        // notify();
        ws.resign(authToken, currentID, null, game);
        return String.format("%s has resigned.", username);
    }

    private Integer convertPosition(String move) {
        char row = move.charAt(0);
        // looked up an easy way to convert letters to numbers
        return row - 'a' + 1;
    }

    private ChessPiece.PieceType convertPromotion(String piece) {
        return switch (piece) {
            case "QUEEN" -> ChessPiece.PieceType.QUEEN;
            case "ROOK" -> ChessPiece.PieceType.ROOK;
            case "KNIGHT" -> ChessPiece.PieceType.KNIGHT;
            case "BISHOP" -> ChessPiece.PieceType.BISHOP;
            default -> null;
        };
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to play chess
                    quit - playing chess
                    help - with possible commands
                    """;
        }
        else if (state == State.JOINEDGAME) {
            return """
                    redraw - redraws chess board
                    leave - leaves the game
                    move <CURRENT POSITION> <MOVE POSITION> - make the move user wants
                    resign - forfeits the game
                    highlight <PIECE POSITION> - highlights egal moves
                    """;
        }
        else if (state == State.OBSERVING) {
            return """
                    redraw - redraws chess board
                    leave - leaves the game
                    """;
        }
        else {
            return """
                    create <NAME> - a game
                    list - games
                    join <ID> [WHITE|BLACK] - a game
                    observe <ID> - a game
                    logout - when you are done
                    quit - playing chess
                    help - with possible commands
                    """;
        }
    }

    private void assertSignedIn() throws Exception {
        if (state == State.SIGNEDOUT) {
            throw new Exception("You must sign in.");
        }
    }
}
