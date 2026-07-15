package dataaccess;
import model.*;

import javax.xml.crypto.Data;

// maybe split into 3 sections, UserDataAccess, GameDataAccess, and AuthDataAccess
public interface UserDataAccess {
    // will eventually have the others
    void createUserData(UserData user) throws DataAccessException;
    UserData getUserData(String username) throws DataAccessException;
    boolean getUserPassword(String password) throws DataAccessException;
}
