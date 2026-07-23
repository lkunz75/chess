package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.GameInfo;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataAccessTests {
    private DataAccess dataAccess;

    @BeforeEach
    public void setup() throws DataAccessException {
        dataAccess = new MySqlDataAccess();
        dataAccess.deleteAllAuthData();
        dataAccess.deleteAllGameData();
        dataAccess.deleteAllUserData();
    }

    //SQL USER TESTS
    @Test
    @DisplayName("Positive CreateUser")
    public void createUserPositive() throws DataAccessException{
        UserData user = new UserData("Bob", "1234", "bob1234@gmail.com");
        dataAccess.createUserData(user);
        UserData data = dataAccess.getUserData("Bob");
        assertEquals("bob1234@gmail.com", data.email());
    }

    @Test
    @DisplayName("Negative CreateUser")
    public void createUserNegative() throws DataAccessException{
        UserData user = new UserData("Bob", "1234", "bob1234@gmail.com");
        dataAccess.createUserData(user);
        UserData user2 = new UserData("Bob", "1234", "bob1234@gmail.com");
        try {
            dataAccess.createUserData(user2);
            fail("Expected DataAccessException to be thrown. You are allowing duplicates!");
        } catch (DataAccessException e) {
            // System.out.println(e.getMessage());
            // looked up notation to return true when error comes as it should!
            assertTrue(e.getMessage().contains("Duplicate"));
        }
    }

    @Test
    @DisplayName("Positive GetUserData")
    public void getUserDataPositive() throws DataAccessException{
        UserData user = new UserData("Jane", "1234", "jane1234@gmail.com");
        dataAccess.createUserData(user);
        UserData data = dataAccess.getUserData("Jane");
        assertEquals("jane1234@gmail.com", data.email());
    }

    @Test
    @DisplayName("Negative GetUserData")
    public void getUserDataNegative() throws DataAccessException{
        UserData data = dataAccess.getUserData("John");
        assertNull(data);
    }

    @Test
    @DisplayName("Positive GetUserPassword")
    public void getUserPasswordPositive() throws DataAccessException{
        UserData user = new UserData("Jane", "1234", "jane1234@gmail.com");
        dataAccess.createUserData(user);
        boolean result = dataAccess.getUserPassword("Jane", "1234");
        assertTrue(result);
    }

    @Test
    @DisplayName("Negative GetUserPassword")
    public void getUserPasswordNegative() throws DataAccessException{
        UserData user = new UserData("Jane", "1234", "jane1234@gmail.com");
        dataAccess.createUserData(user);
        boolean result = dataAccess.getUserPassword("Jane", "5865");
        assertFalse(result);
    }

    @Test
    @DisplayName("Positive DeleteUserData")
    public void deleteUserDataPositive() throws DataAccessException{
        UserData user = new UserData("Bob", "1234", "bob1234@gmail.com");
        dataAccess.createUserData(user);
        dataAccess.deleteAllUserData();
        UserData data = dataAccess.getUserData("Bob");
        assertNull(data);
    }

    // AUTH TESTS
    @Test
    @DisplayName("Positive AuthData")
    public void createAuthDataPositive() throws DataAccessException{
        UserData user = new UserData("Bob", "1234", "bob1234@gmail.com");
        dataAccess.createUserData(user);
        AuthData.AuthRecord data = dataAccess.createAuthData(user);
        assertNotNull(data.authToken());
    }

    @Test
    @DisplayName("Negative AuthData")
    public void createAuthDataNegative() throws DataAccessException{
        UserData user = new UserData("Bob", "1234", "bob1234@gmail.com");
        dataAccess.createUserData(user);
        dataAccess.createAuthData(user);
        try {
            dataAccess.createAuthData(user);
        } catch (DataAccessException e) {
            assertTrue(e.getMessage().contains("Duplicate"));
        }
    }

    @Test
    @DisplayName("Positive GetAuthData")
    public void getAuthDataPositive() throws DataAccessException{
        UserData user = new UserData("Bob", "1234", "bob1234@gmail.com");
        dataAccess.createUserData(user);
        AuthData.AuthRecord createdData = dataAccess.createAuthData(user);
        AuthData.AuthRecord data = dataAccess.getAuthData(createdData.authToken());
        assertEquals(createdData.authToken(), data.authToken());
    }

    @Test
    @DisplayName("Negative GetAuthData")
    public void getAuthDataNegative() throws DataAccessException{
        UserData user = new UserData("Bob", "1234", "bob1234@gmail.com");
        dataAccess.createUserData(user);
        AuthData.AuthRecord createdData = dataAccess.createAuthData(user);
        AuthData.AuthRecord data = dataAccess.getAuthData("123455");
        assertNull(data);
    }

    @Test
    @DisplayName("Positive DeleteAuthToken")
    public void deleteAuthTokenPositive() throws DataAccessException{
        UserData user = new UserData("Bob", "1234", "bob1234@gmail.com");
        dataAccess.createUserData(user);
        AuthData.AuthRecord createdData = dataAccess.createAuthData(user);
        dataAccess.deleteAuthToken(createdData.authToken());
        AuthData.AuthRecord data = dataAccess.getAuthData(createdData.authToken());
        assertNull(data);
    }

    @Test
    @DisplayName("Negative DeleteAuthToken")
    public void deleteAuthTokenNegative() throws DataAccessException{
        // SQL actually handles the error
        dataAccess.deleteAuthToken("123456");
    }

    @Test
    @DisplayName("Positive DeleteAuthData")
    public void deleteAuthDataPositive() throws DataAccessException{
        UserData user = new UserData("Bob", "1234", "bob1234@gmail.com");
        dataAccess.createUserData(user);
        AuthData.AuthRecord createdData = dataAccess.createAuthData(user);
        dataAccess.deleteAllAuthData();
        assertNull(dataAccess.getAuthData(createdData.authToken()));
    }

    //GAME TESTS
    @Test
    @DisplayName("Positive CreateGame")
    public void createGamePositive() throws DataAccessException{
        int gameID = dataAccess.newGameID();
        GameData game = new GameData(gameID, null, null, "Bob's Game", new ChessGame());
        dataAccess.createGame(game);
        assertNotNull(dataAccess.getGame("Bob's Game"));
    }

    @Test
    @DisplayName("Negative CreateGame")
    public void createGameNegative() throws DataAccessException{
        int gameID = dataAccess.newGameID();
        GameData game = new GameData(gameID, null, null, "Bob's Game", new ChessGame());
        dataAccess.createGame(game);
        try {
            dataAccess.createGame(game);
            fail("Expected DataAccessException to be thrown. You are allowing duplicates!");
        } catch (DataAccessException e) {
            assertTrue(e.getMessage().contains("Duplicate"));
        }
    }

    @Test
    @DisplayName("Positive ListGames")
    public void listGamePositive() throws DataAccessException{
        int gameID = dataAccess.newGameID();
        GameData game = new GameData(gameID, null, null, "Bob's Game", new ChessGame());
        dataAccess.createGame(game);
        int gameID2 = dataAccess.newGameID();
        GameData game2 = new GameData(gameID2, null, null, "Jane's Game", new ChessGame());
        dataAccess.createGame(game2);
        List<GameInfo> games = dataAccess.listGames();
        // System.out.println(games);
        assertEquals(2, games.size());
    }

    @Test
    @DisplayName("Negative ListGames")
    public void listGameNegative() throws DataAccessException{
        List<GameInfo> games = dataAccess.listGames();
        // System.out.println(games);
        assertEquals(new ArrayList<>(), games);
    }

    @Test
    @DisplayName("Positive GameID")
    public void gameIDPositive() throws DataAccessException{
        int gameID = dataAccess.newGameID();
        GameData game = new GameData(gameID, null, null, "Bob's Game", new ChessGame());
        dataAccess.createGame(game);
        int gameID2 = dataAccess.newGameID();
        GameData game2 = new GameData(gameID2, null, null, "Jane's Game", new ChessGame());
        dataAccess.createGame(game2);
        int nextID = dataAccess.newGameID();
        assertEquals(3, nextID);
    }

    @Test
    @DisplayName("Negative GameID")
    public void gameIDNegative() throws DataAccessException{
        int gameID = dataAccess.newGameID();
        assertEquals(1, gameID);
    }

    @Test
    @DisplayName("Positive GetColor")
    public void getColorPositive() throws DataAccessException{
        int gameID = dataAccess.newGameID();
        GameData game = new GameData(gameID, null, null, "Bob's Game", new ChessGame());
        dataAccess.createGame(game);
        assertTrue(dataAccess.getColor("WHITE", 1));
    }

    @Test
    @DisplayName("Negative GetColor")
    public void getColorNegative() throws DataAccessException{
        int gameID = dataAccess.newGameID();
        GameData game = new GameData(gameID, null, null, "Bob's Game", new ChessGame());
        dataAccess.createGame(game);
        dataAccess.joinGame("Bob", "WHITE", 1);
        assertFalse(dataAccess.getColor("WHITE", 1));
    }

    @Test
    @DisplayName("Positive JoinGame")
    public void joinGamePositive() throws DataAccessException{
        int gameID = dataAccess.newGameID();
        GameData game = new GameData(gameID, null, null, "Bob's Game", new ChessGame());
        dataAccess.createGame(game);
        dataAccess.joinGame("Bob", "WHITE", 1);
        GameData gameData = dataAccess.getGame("Bob's Game");
        assertEquals("Bob", gameData.whiteUsername());
    }

    @Test
    @DisplayName("Negative JoinGame")
    public void joinGameNegative() throws DataAccessException{
        int gameID = dataAccess.newGameID();
        GameData game = new GameData(gameID, null, null, "Bob's Game", new ChessGame());
        dataAccess.createGame(game);
        dataAccess.joinGame("Bob", "WHITE", 1);
        try {
            dataAccess.joinGame("June", "WHITE", 1);
        } catch (DataAccessException e) {
            assertTrue(e.getMessage().contains("403"));
        }
    }

    @Test
    @DisplayName("Positive DeleteGameData")
    public void deleteGameDataPositive() throws DataAccessException{
        int gameID = dataAccess.newGameID();
        GameData game = new GameData(gameID, null, null, "Bob's Game", new ChessGame());
        dataAccess.createGame(game);
        dataAccess.deleteAllGameData();
        assertNull(dataAccess.getGame("Bob's Game"));
    }
}