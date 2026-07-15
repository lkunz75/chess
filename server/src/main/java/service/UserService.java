package service;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.UserData;

import java.util.List;

public class UserService {
    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        UserData user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        MemoryDataAccess registeredData = new MemoryDataAccess();
        List<String> registeredUser = null;
        try {
            registeredUser = registeredData.createUser(user); // error suggested this format
        } catch (DataAccessException e) {
            throw new DataAccessException("403 Error: Username already taken");
        }
        return new RegisterResult(registeredUser.get(0), registeredUser.get(1));
    }
}

record LoginRequest(String username, String password) {}
record LoginResult(String username, String authToken) {}

record RegisterRequest(String username, String password, String email) {}
record RegisterResult(String username, String authToken) {}
