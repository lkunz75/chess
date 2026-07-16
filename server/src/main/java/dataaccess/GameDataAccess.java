package dataaccess;
import model.GameData;
import java.util.List;

public interface GameDataAccess {
    List<List<String>> listGames() throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void createGame(GameData game) throws DataAccessException;
    int newGameID() throws DataAccessException;
    boolean getColor() throws DataAccessException;
    void deleteAllGameData() throws DataAccessException;
}