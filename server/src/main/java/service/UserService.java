package service;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import service.userrequests.*;

public class UserService {
    // created registeredData above so it accesses the same thing once instead of creating new each time
    MemoryDataAccess registeredData = new MemoryDataAccess();

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        if (registerRequest.username() == null || registerRequest.password() == null
                || registerRequest.email() == null) {
            throw new DataAccessException("400 Error: Bad request");
        }
        UserData user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        UserData dataBaseUser = registeredData.getUserData(user.username());
        if (dataBaseUser != null){
            throw new DataAccessException("403 Error: Username already taken");
        }
        else {
            registeredData.createUserData(user);
            AuthData.AuthRecord userAuthData = registeredData.createAuthData(user);
            return new RegisterResult(userAuthData.username(), userAuthData.authToken());
        }
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        UserData dataBaseUser = registeredData.getUserData(loginRequest.username());
        if (loginRequest.username() == null || loginRequest.password() == null) {
            throw new DataAccessException("400 Error: Bad Request");
        }
        if (dataBaseUser == null || !registeredData.getUserPassword(loginRequest.username(), loginRequest.password())){
            throw new DataAccessException("401 Error: Unauthorized");
        }
        AuthData.AuthRecord userAuthData = registeredData.createAuthData(dataBaseUser);
        return new LoginResult(userAuthData.username(), userAuthData.authToken());
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws DataAccessException {
        try {
            AuthData.AuthRecord authData = registeredData.getAuthData(logoutRequest.authToken());
            if (authData == null) {
                throw new DataAccessException("401 Error: Unauthorized");
            }
            else {
                registeredData.deleteAuthToken(logoutRequest.authToken());
                return new LogoutResult();
            }
        } catch (DataAccessException e) {
            throw new DataAccessException("401 Error: Unauthorized");
        }
    }

    public DeleteUserResult deleteUser(DeleteUserRequest deleteRequest) throws DataAccessException {
        registeredData.deleteAllUserData();
        registeredData.deleteAllAuthData();
        registeredData = new MemoryDataAccess();
        return new DeleteUserResult();
    }
}