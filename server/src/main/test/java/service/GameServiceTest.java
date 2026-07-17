package service;

import dataaccess.DataAccessException;
import model.GameInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class GameServiceTest {

    final static UserService USER_SERVICE = new UserService();
    final static GameService GAME_SERVICE = new GameService();
    @BeforeEach
    void clear() throws DataAccessException {
        DeleteUserRequest deleteUserRequest = new DeleteUserRequest();
        DeleteUserResult result = USER_SERVICE.deleteUser(deleteUserRequest);
        DeleteRequest deleteRequest = new DeleteRequest();
        DeleteResult deleteResult = GAME_SERVICE.delete(deleteRequest);
    }

    // CREATE
    @Test
    @DisplayName("Positive CreateTest")
    public void createUserPositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Bob", "1234", "bob1234@gmail.com");
        RegisterResult result = USER_SERVICE.register(request);
        CreateRequest createRequest = new CreateRequest(result.authToken(), "GameTest123");
        CreateResult createResult = GAME_SERVICE.create(createRequest);
        assertEquals(new CreateResult(1), createResult);
    }

    @Test
    @DisplayName("Negative CreateTest")
    public void createUserNegative() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Bob", "1234", "bob1234@gmail.com");
        RegisterResult result = USER_SERVICE.register(request);
        CreateRequest createRequest = new CreateRequest("1234", "GameTest123");
        try {
            CreateResult createResult = GAME_SERVICE.create(createRequest);
            fail("Expected DataAccessException to be thrown. You are allowing someone without a proper authToken in!");
        }
        catch (DataAccessException e){
            assertEquals("401 Error: Unauthorized", e.getMessage());
        }
    }

    // JOIN
    @Test
    @DisplayName("Positive JoinTest")
    public void joinUserPositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Bob", "1234", "bob1234@gmail.com");
        RegisterResult result = USER_SERVICE.register(request);
        CreateRequest createRequest = new CreateRequest(result.authToken(), "GameTest123");
        CreateResult createResult = GAME_SERVICE.create(createRequest);
        JoinRequest joinRequest = new JoinRequest(result.authToken(), "WHITE", createResult.gameID());
        JoinResult joinResult = GAME_SERVICE.join(joinRequest);
        assertEquals(new JoinResult(), joinResult);
    }

    @Test
    @DisplayName("negative JoinTest")
    public void joinUserNegative() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Bob", "1234", "bob1234@gmail.com");
        RegisterResult result = USER_SERVICE.register(request);
        RegisterRequest request2 = new RegisterRequest("Jane", "6767", "jane67@gmail.com");
        RegisterResult result2 = USER_SERVICE.register(request2);
        CreateRequest createRequest = new CreateRequest(result.authToken(), "GameTest123");
        CreateResult createResult = GAME_SERVICE.create(createRequest);
        JoinRequest joinRequest = new JoinRequest(result.authToken(), "WHITE", createResult.gameID());
        JoinResult joinResult = GAME_SERVICE.join(joinRequest);
        JoinRequest joinRequest2 = new JoinRequest(result2.authToken(), "WHITE", createResult.gameID());
        try {
            JoinResult joinResult2 = GAME_SERVICE.join(joinRequest2);
            fail("Expected DataAccessException to be thrown. You are allowing someone with the same color to join!");
        }
        catch(DataAccessException e) {
            assertEquals("403 Error: already taken", e.getMessage());
        }
    }

    // LIST
    @Test
    @DisplayName("Positive ListTest")
    public void listUserPositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Bob", "1234", "bob1234@gmail.com");
        RegisterResult result = USER_SERVICE.register(request);
        CreateRequest createRequest = new CreateRequest(result.authToken(), "GameTest123");
        CreateResult createResult = GAME_SERVICE.create(createRequest);
        JoinRequest joinRequest = new JoinRequest(result.authToken(), "WHITE", createResult.gameID());
        JoinResult joinResult = GAME_SERVICE.join(joinRequest);
        ListRequest listRequest = new ListRequest(result.authToken());
        ListResult listResult = GAME_SERVICE.list(listRequest);
        GameInfo gameInfo = new GameInfo(1, "Bob", null, "GameTest123");
        List<GameInfo> gameList = new ArrayList<>();
        gameList.add(gameInfo);
        assertEquals(new ListResult(gameList),listResult);
    }

    @Test
    @DisplayName("Negative ListTest")
    public void listUserNegative() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Bob", "1234", "bob1234@gmail.com");
        RegisterResult result = USER_SERVICE.register(request);
        CreateRequest createRequest = new CreateRequest(result.authToken(), "GameTest123");
        CreateResult createResult = GAME_SERVICE.create(createRequest);
        JoinRequest joinRequest = new JoinRequest(result.authToken(), "WHITE", createResult.gameID());
        JoinResult joinResult = GAME_SERVICE.join(joinRequest);
        ListRequest listRequest = new ListRequest("badauth123");
        try {
            ListResult listResult = GAME_SERVICE.list(listRequest);
            fail("Expected DataAccessException to be thrown. You are allowing someone with a bad authToken to join.");
        } catch (DataAccessException e) {
            assertEquals("401 Error: Unauthorized", e.getMessage());
        }
    }

    @Test
    @DisplayName("Positive DeleteTest")
    public void deleteUserPositive() throws DataAccessException {
        UserService userService = new UserService();
        RegisterRequest request = new RegisterRequest("Bob", "1234", "bob1234@gmail.com");
        RegisterResult result = userService.register(request);
        GameService gameService = new GameService();
        CreateRequest createRequest = new CreateRequest(result.authToken(), "GameTest123");
        DeleteRequest deleteRequest = new DeleteRequest();
        DeleteResult deleteResult = gameService.delete(deleteRequest);
        assertEquals(deleteResult, new DeleteResult());
    }
}
