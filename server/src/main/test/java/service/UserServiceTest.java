package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    // looked up notation and classes to do this

    //REGISTER
    @Test
    @DisplayName("Positive RegisterTest")
    public void registerUserPositive() throws DataAccessException{
        UserService service = new UserService();
        RegisterRequest request = new RegisterRequest("Bob", "1234", "bob1234@gmail.com");
        RegisterResult result = service.register(request);
        assertNotNull(result.authToken());
        assertEquals("Bob", result.username());
    }

    @Test
    @DisplayName("Negative RegisterTest")
    public void registerUserNegative() throws DataAccessException{
        UserService service = new UserService();
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

    //LOGIN
    @Test
    @DisplayName("Positive LoginTest")
    public void loginUserPositive() throws DataAccessException{
        UserService service = new UserService();
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
        UserService service = new UserService();
        RegisterRequest bobRegister = new RegisterRequest("Bob", "1234", "bob1234@gmail.com");
        service.register(bobRegister);
        RegisterRequest janeRegister = new RegisterRequest("Jane", "6784", "jane6789@gmail.com");
        service.register(janeRegister);
        LoginRequest loginRequest = new LoginRequest("Bob", "6784");
        try {
            LoginResult loginResult = service.login(loginRequest);
            fail("Expected DataAccessException to be thrown. Should not have signed in");
        }
        catch (DataAccessException e){
            assertEquals("400 Error: Bad Request", e.getMessage());
        }
    }
}
