package dataaccess;
import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// responsible for storing and retrieving the server's data
public class MemoryDataAccess {
    //HashMaps would be simple
    List<AuthData.AuthRecord> authInfo = new ArrayList<>();
    List<UserData> userInfo = new ArrayList<>();

    public UserData getUserData(String username){
        // Checking will move to userService layer
        for (UserData info:userInfo){
            String name = info.username(); // same as .get(0)
            if (name.equals(username)) {
                return info; // we want to return our already found data
            }
        }
        // will return user data it found, or will return null (could throw the exception if you want)
        return null; // not found
    }

    public void createUserData(UserData user){
        // this is where I actually it after I know its valid
        // create, all we do it add it
        userInfo.add(user);
    }

    public AuthData.AuthRecord createAuthData(UserData user) throws DataAccessException {
        // only needs to be these three lines
        String authToken = AuthData.generateToken();
        AuthData.AuthRecord newAuthData = new AuthData.AuthRecord(user.username(), authToken);
        authInfo.add(newAuthData);
        return newAuthData;
    }
}
