package dataaccess;
import model.*;

// maybe split into 3 sections, UserDataAccess, GameDataAccess, and AuthDataAccess
public interface UserDataAccess {
    // will eventually have the others
    void createUserData(UserData user) throws DataAccessException;
    UserData getUserData(String username) throws DataAccessException;
    boolean getUserPassword(String password) throws DataAccessException;
    void deleteAllUserData() throws DataAccessException;
}
