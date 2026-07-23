package service;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.UserDataAccess;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import service.userrequests.*;

public class UserService {
    DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    //ADD PASSWORD HASHING HERE
    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        if (registerRequest.username() == null || registerRequest.password() == null
                || registerRequest.email() == null) {
            throw new DataAccessException("400 Error: Bad request");
        }
        String hashedPassword = BCrypt.hashpw(registerRequest.password(), BCrypt.gensalt());
        UserData user = new UserData(registerRequest.username(), hashedPassword, registerRequest.email());
        UserData dataBaseUser = dataAccess.getUserData(user.username());
        if (dataBaseUser != null){
            throw new DataAccessException("403 Error: Username already taken");
        }
        else {
            dataAccess.createUserData(user);
            AuthData.AuthRecord userAuthData = dataAccess.createAuthData(user);
            return new RegisterResult(userAuthData.username(), userAuthData.authToken());
        }
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        UserData dataBaseUser = dataAccess.getUserData(loginRequest.username());
        if (loginRequest.username() == null || loginRequest.password() == null) {
            throw new DataAccessException("400 Error: Bad Request");
        }
        if (dataBaseUser == null || !BCrypt.checkpw(loginRequest.password(), dataBaseUser.password())){
            throw new DataAccessException("401 Error: Unauthorized");
        }
        AuthData.AuthRecord userAuthData = dataAccess.createAuthData(dataBaseUser);
        return new LoginResult(userAuthData.username(), userAuthData.authToken());
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws DataAccessException {
        try {
            AuthData.AuthRecord authData = dataAccess.getAuthData(logoutRequest.authToken());
            if (authData == null) {
                throw new DataAccessException("401 Error: Unauthorized");
            }
            else {
                dataAccess.deleteAuthToken(logoutRequest.authToken());
                return new LogoutResult();
            }
        } catch (DataAccessException e) {
            throw new DataAccessException("401 Error: Unauthorized");
        }
    }

    public DeleteUserResult deleteUser(DeleteUserRequest deleteRequest) throws DataAccessException {
        dataAccess.deleteAllUserData();
        dataAccess.deleteAllAuthData();
        return new DeleteUserResult();
    }
}