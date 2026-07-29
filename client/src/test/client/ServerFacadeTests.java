package client;

import org.junit.jupiter.api.*;

import server.Server;
import service.userrequests.*;
import service.gamerequests.*;
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
    void clear() throws Exception {
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
    void registerPositive() throws Exception {
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        var authData = facade.register(request);
        assertTrue(authData.authToken().length() > 10);
    }

    @DisplayName("Negative RegisterTest")
    @Test
    void registerNegative() throws Exception {
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        var authData = facade.register(request);
        RegisterRequest requestNegative = new RegisterRequest("player1", "password", "p1@email.com");
        try {
            var authDataNegative = facade.register(requestNegative);
            fail("Allowing duplicate users");
        } catch (Exception e) {
            // System.out.println(e.getMessage());
            assertTrue(e.getMessage().contains("taken"));
        }
    }

    //LOGIN
    @DisplayName("Positive LoginTest")
    @Test
    void loginPositive() throws Exception {
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        var authData = facade.register(request);
        LoginRequest loginRequest = new LoginRequest(authData.username(), "password");
        var loginData = facade.login(loginRequest);
        assertTrue(loginData.authToken().length() > 10);
    }

    @DisplayName("Negative LoginTest")
    @Test
    void loginNegative() throws Exception {
        LoginRequest loginRequest = new LoginRequest("george", "password");
        try {
            var loginData = facade.login(loginRequest);
            fail("You are letting someone with invalid credentials login");
        } catch (Exception e) {
            // System.out.println(e.getMessage());
            assertTrue(e.getMessage().contains("Unauthorized"));
        }
    }

    // LOGOUT
    @DisplayName("Positive LogoutTest")
    @Test
    void logoutPositive() throws Exception {
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
    void logoutNegative() throws Exception {
        LogoutRequest logoutRequest = new LogoutRequest("12345gn");
        try {
            var logoutData = facade.logout(logoutRequest);
            fail("Letting someone with an invalid authToken logout");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Unauthorized"));
        }
    }

    // DELETE
    @DisplayName("Positive Delete")
    @Test
    void deletePositive() throws Exception {
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        var authData = facade.register(request);
        DeleteRequest delete = new DeleteRequest();
        DeleteUserRequest deleteUser = new DeleteUserRequest();
        facade.delete(delete, deleteUser);
        LoginRequest loginRequest = new LoginRequest(authData.username(), "password");
        try {
            var loginData = facade.login(loginRequest);
            fail("Not deleting data as it should");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Unauthorized"));
        }
    }

    //CREATE
    @DisplayName("Positive CreateTest")
    @Test
    void createPositive() throws Exception {
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
    void createNegative() throws Exception {
        RegisterRequest request = new RegisterRequest("Jane", "janey", "jane@email.com");
        var authData = facade.register(request);
        LoginRequest loginRequest = new LoginRequest(authData.username(), "janey");
        var loginData = facade.login(loginRequest);
        CreateRequest createRequest = new CreateRequest(loginData.authToken(), "Jane's Game");
        facade.create(createRequest);
        try {
            facade.create(createRequest);
            fail("Created an already created game");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Bad"));
        }
    }

    //LIST
    @DisplayName("Positive ListTest")
    @Test
    void listPositive() throws Exception {
        RegisterRequest request = new RegisterRequest("Jane", "janey", "jane@email.com");
        var authData = facade.register(request);
        LoginRequest loginRequest = new LoginRequest(authData.username(), "janey");
        var loginData = facade.login(loginRequest);
        CreateRequest createRequest = new CreateRequest(loginData.authToken(), "Jane's Game");
        var createData = facade.create(createRequest);
        ListRequest listRequest = new ListRequest(authData.authToken());
        var listGames = facade.list(listRequest);
        assertEquals(1, listGames.games().size());
    }

    @DisplayName("Negative ListTest")
    @Test
    void listNegative() throws Exception {
        RegisterRequest request = new RegisterRequest("Jane", "janey", "jane@email.com");
        var authData = facade.register(request);
        LoginRequest loginRequest = new LoginRequest(authData.username(), "janey");
        var loginData = facade.login(loginRequest);
        CreateRequest createRequest = new CreateRequest(loginData.authToken(), "Jane's Game");
        facade.create(createRequest);
        ListRequest listRequest = new ListRequest("1234433");
        try {
            facade.list(listRequest);
            fail("Invalid authToken, but still listed games!");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Unauthorized"));
        }
    }

    // Join
    @DisplayName("Positive JoinTest")
    @Test
    void joinPositive() throws Exception {
        RegisterRequest request = new RegisterRequest("Jane", "janey", "jane@email.com");
        var authData = facade.register(request);
        LoginRequest loginRequest = new LoginRequest(authData.username(), "janey");
        var loginData = facade.login(loginRequest);
        CreateRequest createRequest = new CreateRequest(loginData.authToken(), "Jane's Game");
        var createData = facade.create(createRequest);
        // the small names like playerColor vs Color seem insignificant, but WILL cause crashes if not fixed
        JoinRequest joinRequest = new JoinRequest(loginData.authToken(), "BLACK", createData.gameID());
        var joinData = facade.join(joinRequest);
        assertEquals(new JoinResult(), joinData);
    }

    @DisplayName("Negative JoinTest")
    @Test
    void joinNegative() throws Exception {
        RegisterRequest request = new RegisterRequest("Jane", "janey", "jane@email.com");
        var authData = facade.register(request);
        LoginRequest loginRequest = new LoginRequest(authData.username(), "janey");
        var loginData = facade.login(loginRequest);
        RegisterRequest request2 = new RegisterRequest("Bob", "bobby", "bob@email.com");
        var authData2 = facade.register(request2);
        LoginRequest loginRequest2 = new LoginRequest(authData2.username(), "bobby");
        var loginData2 = facade.login(loginRequest2);
        CreateRequest createRequest = new CreateRequest(loginData.authToken(), "The Best Game");
        var createData = facade.create(createRequest);
        JoinRequest joinRequest = new JoinRequest(loginData.authToken(), "BLACK", createData.gameID());
        var joinData = facade.join(joinRequest);
        JoinRequest joinRequest2 = new JoinRequest(loginData2.authToken(), "BLACK", createData.gameID());
        try {
            facade.join(joinRequest2);
            fail("This color was already taken, but you are letting them join anyways!");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("taken"));
        }
    }
}
