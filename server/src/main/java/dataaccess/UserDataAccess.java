package dataaccess;
import model.*;

public interface UserDataAccess {
    // will eventually have the others
    void createUserData(UserData user) throws DataAccessException;
    UserData getUserData(String username) throws DataAccessException;
    boolean getUserPassword(String password) throws DataAccessException;
    void deleteAllUserData() throws DataAccessException;
}
