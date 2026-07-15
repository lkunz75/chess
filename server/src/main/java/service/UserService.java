package service;
import dataaccess.MemoryDataAccess;

public class UserService {
    public RegisterResult register(RegisterRequest registerRequest) {
        MemoryDataAccess.createUser(registerRequest.username());
    }
    public LoginResult login(LoginRequest loginRequest) {}
    public void logout(LogoutRequest logoutRequest) {}
}

record LoginRequest(String username, String password) {}
record LoginResult(String username, String authToken) {}

record RegisterRequest(String username, String password, String email) {}
record RegisterResult(String username, String authToken) {}
