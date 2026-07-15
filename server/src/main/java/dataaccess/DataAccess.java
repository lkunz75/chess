package dataaccess;
import model.*;

public interface DataAccess {
    UserData createUser(UserData username) throws DataAccessException;
}
