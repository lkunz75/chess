package dataaccess;
import model.*;

public interface AuthDataAccess {
    AuthData.AuthRecord createAuthData(UserData user) throws DataAccessException;
    AuthData.AuthRecord getAuthToken(String authToken) throws DataAccessException;
    void deleteAuthToken(String authToken) throws DataAccessException;
}
