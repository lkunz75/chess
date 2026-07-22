package dataaccess;
import model.AuthData;
import model.GameData;
import model.GameInfo;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// responsible for storing and retrieving the server's data
public class MemoryDataAccess implements DataAccess {
    static List<AuthData.AuthRecord> authInfo = new ArrayList<>();
    static List<UserData> userInfo = new ArrayList<>();
    static List<GameData> allGameData = new ArrayList<>();
    static List<GameInfo> listGames = new ArrayList<>();
    static int currentID = 0;

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

    public boolean getUserPassword(String username, String password){
        for (UserData info:userInfo){
            String userPassword = info.password();
            String name = info.username();
            if (userPassword.equals(password) && name.equals(username)) {
                return true; // we want to return our already found data
            }
        }
        return false;
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

    public AuthData.AuthRecord getAuthData(String authToken){
        if (authToken == null) {return null;} // person is signed out
        for (AuthData.AuthRecord info:authInfo){
            String token = info.authToken(); // same as .get(0)
            if (token.equals(authToken)) {
                return info; // we want to return our already found data
            }
        }
        return null;
    }

    public void deleteAuthToken(String authToken){
        int index = 0;
        for (AuthData.AuthRecord info:authInfo){
            String token = info.authToken(); // same as .get(0)
            if (token.equals(authToken)) {
                // remove it all because it will get reassigned when done
                authInfo.remove(index);
                return; // return so we don't keep looping through for no reason
            }
            index++;
        }
    }

    public List<GameInfo> listGames(){
        return listGames;
    }

    public GameData getGame(String gameName) {
        for (GameData game: allGameData){
            String name = game.gameName();
            if (name.equals(gameName)){
                return game;
            }
        }
        return null;
    }

    public void createGame(GameData game){
        allGameData.add(game);
        GameInfo newGameInfo = new GameInfo(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName());
        listGames.add(newGameInfo);
    }

    public int newGameID() {
        currentID++;
        return currentID;
    }

    public boolean getColor(String color, int gameID){
        for (GameData game: allGameData){
            int id = game.gameID();
            String whiteName = game.whiteUsername();
            String blackName = game.blackUsername();
            if (id == gameID){
                if (color.equals("WHITE") && whiteName == null){
                    return true;
                }
                else if (color.equals("BLACK") && blackName == null){
                    return true;
                }
            }
        }
        return false;
    }

    public void joinGame(String username, String color, int gameID){
        int index = 0;
        for (GameData game: allGameData){
            int id = game.gameID();
            if (id == gameID){
                GameData newGame = null;
                if (color.equals("WHITE")){
                    newGame = new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game());
                }
                else {
                    newGame = new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game());
                }
                allGameData.remove(game); //since it's a collection, remove by the actual game!
                listGames.remove(index);
                createGame(newGame);
                // insert the updated game
                return;
            }
            index++;
        }
    }

    public void deleteAllGameData() {
        currentID = 0;
        allGameData = new ArrayList<>();
        listGames = new ArrayList<>();
    }

    public void deleteAllAuthData() {authInfo = new ArrayList<>();}

    public void deleteAllUserData() {userInfo = new ArrayList<>();}
}
