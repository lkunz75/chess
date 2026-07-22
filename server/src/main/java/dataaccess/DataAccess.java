package dataaccess;

import model.AuthData;
import model.GameData;
import model.GameInfo;
import model.UserData;

import java.util.Collection;
import java.util.List;

public interface DataAccess {
    void createUserData(UserData user) throws DataAccessException;
    UserData getUserData(String username) throws DataAccessException;
    boolean getUserPassword(String username, String password) throws DataAccessException;
    void deleteAllUserData() throws DataAccessException;

    AuthData.AuthRecord createAuthData(UserData user) throws DataAccessException;
    void deleteAuthToken(String authToken) throws DataAccessException;
    void deleteAllAuthData() throws DataAccessException;

    List<GameInfo> listGames() throws DataAccessException;
    GameData getGame(String gameName) throws DataAccessException;
    void createGame(GameData game) throws DataAccessException;
    int newGameID() throws DataAccessException;
    boolean getColor(String color, int GameID) throws DataAccessException;
    void deleteAllGameData() throws DataAccessException;
    void joinGame(String username, String color, int gameID) throws DataAccessException;
}
