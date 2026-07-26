package client;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import service.gamerequests.CreateRequest;
import service.gamerequests.DeleteRequest;
import service.userrequests.*;

import static java.util.Objects.isNull;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        String path = "http://localhost:" + port;
        facade = new ServerFacade(path);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clear() throws DataAccessException {
        DeleteRequest delete = new DeleteRequest();
        DeleteUserRequest deleteUser = new DeleteUserRequest();
        facade.delete(delete, deleteUser);
    }


    @Test
    public void sampleTest() {
        assertTrue(true);
    }

    // REGISTER
    @DisplayName("Positive RegisterTest")
    @Test
    void registerPositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        var authData = facade.register(request);
        assertTrue(authData.authToken().length() > 10);
    }

    @DisplayName("Negative RegisterTest")
    @Test
    void registerNegative() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        var authData = facade.register(request);
        RegisterRequest requestNegative = new RegisterRequest("player1", "password", "p1@email.com");
        try {
            var authDataNegative = facade.register(requestNegative);
            fail("Allowing duplicate users");
        } catch (DataAccessException e) {
            // System.out.println(e.getMessage());
            assertTrue(e.getMessage().contains("taken"));
        }
    }

    //LOGIN
    @DisplayName("Positive LoginTest")
    @Test
    void loginPositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        var authData = facade.register(request);
        LoginRequest loginRequest = new LoginRequest(authData.username(), "password");
        var loginData = facade.login(loginRequest);
        assertTrue(loginData.authToken().length() > 10);
    }

    @DisplayName("Negative LoginTest")
    @Test
    void loginNegative() throws DataAccessException {
        LoginRequest loginRequest = new LoginRequest("george", "password");
        try {
            var loginData = facade.login(loginRequest);
            fail("You are letting someone with invalid credentials login");
        } catch (DataAccessException e) {
            // System.out.println(e.getMessage());
            assertTrue(e.getMessage().contains("Unauthorized"));
        }
    }

    // LOGOUT
    @DisplayName("Positive LogoutTest")
    @Test
    void logoutPositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        var authData = facade.register(request);
        LoginRequest loginRequest = new LoginRequest(authData.username(), "password");
        var loginData = facade.login(loginRequest);
        LogoutRequest logoutRequest = new LogoutRequest(loginData.authToken());
        var logoutData = facade.logout(logoutRequest);
        assertEquals(new LogoutResult() , logoutData);
    }

    @DisplayName("Negative LogoutTest")
    @Test
    void logoutNegative() throws DataAccessException {
        LogoutRequest logoutRequest = new LogoutRequest("12345gn");
        try {
            var logoutData = facade.logout(logoutRequest);
            fail("Letting someone with an invalid authToken logout");
        } catch (DataAccessException e) {
            assertTrue(e.getMessage().contains("Unauthorized"));
        }
    }

    // DELETE
    @DisplayName("Positive Delete")
    @Test
    void deletePositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        var authData = facade.register(request);
        DeleteRequest delete = new DeleteRequest();
        DeleteUserRequest deleteUser = new DeleteUserRequest();
        facade.delete(delete, deleteUser);
        LoginRequest loginRequest = new LoginRequest(authData.username(), "password");
        try {
            var loginData = facade.login(loginRequest);
            fail("Not deleting data as it should");
        } catch (DataAccessException e) {
            assertTrue(e.getMessage().contains("Unauthorized"));
        }
    }

    //CREATE
    @DisplayName("Positive CreateTest")
    @Test
    void createPositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Jane", "janey", "jane@email.com");
        var authData = facade.register(request);
        LoginRequest loginRequest = new LoginRequest(authData.username(), "janey");
        var loginData = facade.login(loginRequest);
        CreateRequest createRequest = new CreateRequest(loginData.authToken(), "Jane's Game");
        var createData = facade.create(createRequest);
        assertTrue(createData.gameID() > 0);
    }

    //CREATE
    @DisplayName("Negative CreateTest")
    @Test
    void createNegative() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Jane", "janey", "jane@email.com");
        var authData = facade.register(request);
        LoginRequest loginRequest = new LoginRequest(authData.username(), "janey");
        var loginData = facade.login(loginRequest);
        CreateRequest createRequest = new CreateRequest(loginData.authToken(), "Jane's Game");
        facade.create(createRequest);
        try {
            facade.create(createRequest);
            fail("Created an already created game");
        } catch (DataAccessException e) {
            assertTrue(e.getMessage().contains("Bad"));
        }
    }



}
