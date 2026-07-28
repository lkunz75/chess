package client;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameInfo;
import server.ServerFacade;
import service.gamerequests.CreateRequest;
import service.gamerequests.JoinRequest;
import service.gamerequests.ListRequest;
import service.gamerequests.ListResult;
import service.userrequests.LoginRequest;
import service.userrequests.LoginResult;
import service.userrequests.LogoutRequest;
import service.userrequests.RegisterRequest;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

// DO I NEED TO HAVE A DELETE DATA? They shouldn't have access to that right?

public class ChessClient {
    private State state = State.SIGNEDOUT;
    private final ServerFacade server;
    private String authToken;
    private String userName;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println("♕ Welcome to 240 Chess. Type Help to get started. ♕");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_MAGENTA  + result);
            } catch (Exception e) {
                var message = e.toString();
                System.out.print(message);
            }
        }
        System.out.println();
    }

    public String eval(String input) {
        try {
            String [] tokens = input.toLowerCase().split(" "); // helps avoid random crashes
            String command = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length); // ordered without the command
            return switch (command) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout(params);
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (DataAccessException e) {
            return e.getMessage();
        }
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    public String register(String...params) throws DataAccessException {
        if (params.length >= 3) {
            state = State.SIGNEDIN;
            userName = String.join("-", params[1]);
            RegisterRequest registerRequest = new RegisterRequest(params[0], params[1], params[2]);
            server.register(registerRequest);
            return String.format("You signed in as %s", userName);
        }
        throw new DataAccessException("Expected: <username>, <password>, <email>");
    }

    public String login(String...params) throws DataAccessException {
        if (params.length >= 3) {
            state = State.SIGNEDIN;
            userName = String.join("-", params[0]);
            LoginResult loginResult = server.login(new LoginRequest(params[0], params[1]));
            authToken = loginResult.authToken();
            // Do I need ws here?
            return String.format("You signed in as %s", userName);
        }
        throw new DataAccessException("Expected: <username>, <password>");
    }

    public String logout(String...params) throws DataAccessException {
        if (params.length >= 1) {
            state = State.SIGNEDOUT;
            userName = null;
            server.logout(new LogoutRequest(authToken));
            return "You are now signed out.";
        }
        throw new DataAccessException("Unable to sign out. Were you signed in?");
    }

    public String create(String...params) throws DataAccessException {
        assertSignedIn();
        if (params.length >= 1) {
            server.create(new CreateRequest(authToken, params[0]));
            return String.format("Game created %s", params[0]);
        }
        throw new DataAccessException("Expected <gameName>");
    }

    public String list() throws DataAccessException {
        assertSignedIn();
        ListResult games = server.list(new ListRequest(authToken));
        var result = new StringBuilder();
        var gson = new Gson();
        for (GameInfo game : games.games()) {
            result.append(gson.toJson(game)).append('\n');
        }
        return result.toString();
    }

    public String join(String...params) throws DataAccessException {
        assertSignedIn();
        if (params.length >= 2) {
            server.join(new JoinRequest(authToken, params[0], Integer.parseInt(params[1])));
            return String.format("Joined game %s, as %s", params[0], params[1]);
        }
        throw new DataAccessException("Expected: <gameName>, <color>");
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

    private void assertSignedIn() throws DataAccessException {
        if (state == State.SIGNEDOUT) {
            throw new DataAccessException("You must sign in.");
        }
    }
}
