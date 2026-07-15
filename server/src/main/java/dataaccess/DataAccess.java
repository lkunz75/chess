package dataaccess;
import model.*;

// maybe split into 3 sections, UserDataAccess, GameDataAccess, and AuthDataAccess
public interface DataAccess {
    // will eventually have the others
    UserData createUser(UserData username) throws DataAccessException;
}
