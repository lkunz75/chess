package dataaccess;
import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// responsible for storing and retrieving the server's data
public class MemoryDataAccess {
    List<List<String>> userInfo = new ArrayList<>();

    public List<String> createUser(UserData user) throws DataAccessException {
        // insert new user here
        // also gives them an authToken
        List<String> createdUser = new ArrayList<>();
        for (List<String> info:userInfo) {
            String name = info.getFirst(); // same as .get(0)
            if (user.username().toString().equals(name)) {
                throw new DataAccessException("403 Error: Username already taken");
            }
        }
        UserData new_user = new UserData(user.username(), user.password(), user.email());
        String authToken = AuthData.generateToken();
        userInfo.add(Arrays.asList(user.username().toString(), authToken));
        createdUser.add(user.username().toString());
        createdUser.add(authToken);
        return createdUser;
    }
}
