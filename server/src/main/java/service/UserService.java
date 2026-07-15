package service;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.util.log.Log;

import java.util.List;

public class UserService {
    // created registeredData above so it accesses the same thing once instead of creating new each time
    MemoryDataAccess registeredData = new MemoryDataAccess();

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        UserData user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        try {
            UserData dataBaseUser = registeredData.getUserData(user.username());
            if (dataBaseUser != null){
                throw new DataAccessException("403 Error: Username already taken");
            }
            else {
                registeredData.createUserData(user);
                AuthData.AuthRecord userAuthData = registeredData.createAuthData(user);
                return new RegisterResult(userAuthData.username(), userAuthData.authToken());
            }
        } catch (DataAccessException e) {
            throw new DataAccessException("403 Error: Username already taken");
        }
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException{
        try{
            UserData dataBaseUser = registeredData.getUserData(loginRequest.username());
            if (dataBaseUser == null || (!registeredData.getUserPassword(loginRequest.username(), loginRequest.password()))){
                throw new DataAccessException("400 Error: Bad Request");
            }
            else {
                AuthData.AuthRecord userAuthData = registeredData.createAuthData(dataBaseUser);
                return new LoginResult(userAuthData.username(), userAuthData.authToken());
            }
        } catch (DataAccessException e) {
            throw new DataAccessException("400 Error: Bad Request");
        }
    }
}

record RegisterRequest(String username, String password, String email) {}
record RegisterResult(String username, String authToken) {}

record LoginRequest(String username, String password) {}
record LoginResult(String username, String authToken) {}

record LogoutRequest(String authToken) {}
record LogoutResult() {}
