package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.userrequests.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {

    final DataAccess dataAccess;
    final UserService service;

    public UserServiceTests() {
        this.dataAccess = new MemoryDataAccess();
        this.service = new UserService(dataAccess);
    }
    // looked up notation and classes to do this


    // following the pet class outline and lets us make it static so info can be shared
    @BeforeEach
    void clear() throws DataAccessException {
        DeleteUserRequest deleteUserRequest = new DeleteUserRequest();
        DeleteUserResult result = service.deleteUser(deleteUserRequest);
    }


    // REGISTER
    @Test
    @DisplayName("Positive RegisterTest")
    public void registerUserPositive() throws DataAccessException{
        RegisterRequest request = new RegisterRequest("Bob", "1234", "bob1234@gmail.com");
        RegisterResult result = service.register(request);
        assertNotNull(result.authToken());
        assertEquals("Bob", result.username());
    }

    @Test
    @DisplayName("Negative RegisterTest")
    public void registerUserNegative() throws DataAccessException{
        RegisterRequest request1 = new RegisterRequest("Bob", "1234", "bob1234@gmail.com");
        RegisterResult result1 = service.register(request1);
        RegisterRequest request2 = new RegisterRequest("Bob", "1234", "bob1234@gmail.com");
        try {
            RegisterResult result2 = service.register(request2);
            fail("Expected DataAccessException to be thrown. You are allowing duplicates!");
        }
        catch (DataAccessException e){
            assertEquals("403 Error: Username already taken", e.getMessage());
        }
    }

    // LOGIN
    @Test
    @DisplayName("Positive LoginTest")
    public void loginUserPositive() throws DataAccessException{
        RegisterRequest registerRequest = new RegisterRequest("Bob", "1234", "bob1234@gmail.com");
        RegisterResult registerResult= service.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("Bob", "1234");
        LoginResult loginResult = service.login(loginRequest);
        // checked to see new auth token assertEquals(registerResult.authToken(), loginResult.authToken());
        assertNotNull(loginResult.authToken()); // if it creates an authToken then all was good
    }

    @Test
    @DisplayName("Negative LoginTest")
    public void loginUserNegative() throws DataAccessException{
        RegisterRequest bobRegister = new RegisterRequest("Bob", "1234", "bob1234@gmail.com");
        service.register(bobRegister);
        RegisterRequest janeRegister = new RegisterRequest("Jane", "6784", "jane6789@gmail.com");
        service.register(janeRegister);
        LoginRequest loginRequest = new LoginRequest("Bob", "6784");
        try {
            LoginResult loginResult = service.login(loginRequest);
            fail("Expected DataAccessException to be thrown. Should not have signed in!");
        }
        catch (DataAccessException e){
            assertEquals("401 Error: Unauthorized", e.getMessage());
        }
    }

    // LOGOUT
    @Test
    @DisplayName("Positive Logout")
    public void logoutUserPositive() throws DataAccessException{
        RegisterRequest registerRequest = new RegisterRequest("Jessica", "6767", "jessica67@gmail.com");
        RegisterResult registerResult= service.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("Jessica", "6767");
        LoginResult loginResult = service.login(loginRequest);
        LogoutRequest logoutRequest = new LogoutRequest(loginResult.authToken());
        LogoutResult logoutResult = service.logout(logoutRequest);
        assertEquals(new LogoutResult(), logoutResult);
    }

    @Test
    @DisplayName("Negative LogoutTest")
    public void logoutUserNegative() throws DataAccessException{
        LogoutRequest loginRequest = new LogoutRequest("6784");
        try {
            LogoutResult logoutResult = service.logout(loginRequest);
            fail("Expected DataAccessException to be thrown. Should not have logged out!");
        }
        catch (DataAccessException e){
            assertEquals("401 Error: Unauthorized", e.getMessage());
        }
    }

    // DELETE
    @Test
    @DisplayName("Positive DeleteTest")
    public void deleteUserPositive() throws DataAccessException{
        RegisterRequest registerRequest = new RegisterRequest("Bob", "1234", "bob1234@gmail.com");
        RegisterResult registerResult= service.register(registerRequest);
        DeleteUserRequest deleteUserRequest = new DeleteUserRequest();
        DeleteUserResult result = service.deleteUser(deleteUserRequest);
        assertEquals(new DeleteUserResult(), result);
    }
}
