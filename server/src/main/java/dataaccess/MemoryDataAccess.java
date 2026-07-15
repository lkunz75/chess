package dataaccess;
import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// responsible for storing and retrieving the server's data
public class MemoryDataAccess {
    List<List<String>> userInfo = new ArrayList<>();

    public void createUser(UserData username) throws DataAccessException {
        // insert new user here
        // also gives them an authToken
        for (List<String> user:userInfo) {
            String name = user.getFirst(); // same as .get(0)
            if (username.username().toString().equals(name)) {
                throw new DataAccessException("403 Error: Username already taken");
            }
        }
        new_user = new UserData(username.username(), username.password(), username.email());
        userInfo.add(Arrays.asList(username.username().toString(), AuthData.generateToken()));
    }

}
