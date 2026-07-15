package service;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;

import java.util.List;

public class UserService {
    MemoryDataAccess registeredData = new MemoryDataAccess(); // created once so it accesses the same thing once instead of creating new each time

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
}
record RegisterRequest(String username, String password, String email) {}
record RegisterResult(String username, String authToken) {}
