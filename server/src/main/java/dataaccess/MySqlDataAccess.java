package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.GameInfo;
import model.UserData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.NULL;

public class MySqlDataAccess {
    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    // we have to have a function here for every function in our MemoryDataAccess class!
    public UserData getUserData(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()){
            var statement = "SELECT *, json FROM userData WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    ps.setString(1, username); // fills the ?
                    if (rs.next()) {
                        return readUser(rs);

                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s", e.getMessage()));
        }
        return null;
    }

    // just moved location to visually follow MemoryDataAccess
    public boolean getUserPassword(String username, String password) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()){
            var statement = "SELECT *, json FROM userData WHERE username=?";
            // * gets all the data in the row/all columns that belong in the table. Builds the rows of the table
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    ps.setString(1, username);
                    // could have a while loop to check uniqueness for gameData
                    // always call rs.next to get to the first row
                    if (!rs.next()) {return false;}
                    UserData user = readUser(rs);
                    if (user.password().equals(password)) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s", e.getMessage()));
        }
        return false;
    }

    public UserData createUserData(UserData user) throws DataAccessException {
        var statement = "INSERT INTO userData (username, password, email, json) VALUES (?,?,?,?)";
        String json = new Gson().toJson(user);
        int rowsMade = executeUpdate(statement, user.username(), user.password(), user.email(), json);
        return new UserData(user.username(), user.password(), user.email());
    }

    public AuthData.AuthRecord createAuthData(UserData user) throws DataAccessException {
        var statement = "INSERT INTO authData (username, authToken, json) VALUES (?,?,?)";
        String json = new Gson().toJson(user);
        String authToken = AuthData.generateToken();
        executeUpdate(statement, user.username(), authToken, json);
        return new AuthData.AuthRecord(user.username(), authToken);
    }

    public AuthData.AuthRecord getAuthData(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()){
            var statement = "SELECT *, json FROM authData WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    ps.setString(2, authToken); // fills the ?
                    if (!rs.next()) { return null;}
                    AuthData.AuthRecord info = readAuth(rs);
                    if (info.authToken().equals(authToken)){
                        return info;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s", e.getMessage()));
        }
        return null;
    }

    public void deleteAuthToken(String authToken) throws DataAccessException {
        var statement = "DELETE FROM authData WHERE authToken=?";
        executeUpdate(statement, authToken);
    }

    public List<GameInfo> listGames() throws DataAccessException{
        List<GameInfo> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT *, json FROM gameData";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGameInfo(rs));
                    }
                    return result;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s", e.getMessage()));
        }
    }

    public GameData createGameData(GameData game) throws DataAccessException {
        var statement = "INSERT INTO userData (gameID, whiteUsername, blackUsername, gameName, game, json) VALUES (?,?,?,?,?,?)";
        String json = new Gson().toJson(game);
        int rowsMade = executeUpdate(statement, game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game(), json);
        return new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
    }

    public boolean getColor(String color, int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT *, json FROM gameData WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    ps.setInt(1, gameID); // fills the ?
                    if (!rs.next()) {return false;}
                    GameData gameData = readGameData(rs);
                    if (color.equals("WHITE") && gameData.whiteUsername() == null) {return true;}
                    if (color.equals("BLACK") && gameData.blackUsername() == null) {return true;}
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s", e.getMessage()));
        }
        return false;
    }

    public void joinGame(String username, String color, int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT *, json FROM gameData WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    ps.setInt(1, gameID);
                    if (!rs.next()) {return;}
                    GameData gameData = readGameData(rs);
                    if (color.equals("WHITE") && gameData.whiteUsername() == null) {
                        executeUpdate("UPDATE gameData SET whiteUsername =? WHERE gameID=?", username, gameID);
                    }
                    if (color.equals("BLACK") && gameData.blackUsername() == null) {
                        executeUpdate("UPDATE gameData SET blackUsername =? WHERE gameID=?", username, gameID);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s", e.getMessage()));
        }
    }

    public void deleteAllGameData() throws DataAccessException {
        var statement = "DELETE FROM GameData";
        executeUpdate(statement);
    }

    public void deleteAllUserData() throws DataAccessException {
        var statement = "DELETE FROM UserData";
        executeUpdate(statement);
    }

    public void deleteAllAuthData() throws DataAccessException {
        var statement = "DELETE FROM AuthData";
        executeUpdate(statement);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++){
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                return ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s", e.getMessage()));
        }
    }

    private final String[] createStatements = {
            // move these into 3 levels if you run into code quality errors
            """
            CREATE TABLE IF NOT EXISTS userData (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`username`),
              INDEX(password)
              INDEX(email)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS gameData (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `game` ENUM(ChessGame),
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameID`),
              INDEX(whiteUsername),
              INDEX(blackUsername),
              INDEX(gameName),
              INDEX(game)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS authData (
              `username` varchar(256) NOT NULL,
              `authToken` varchar(256) NOT NULL,
              PRIMARY KEY (`username`),
              INDEX(authToken),
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException{
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()){
            for (String statement: createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    private UserData readUser(ResultSet RowInfo) throws SQLException {
        String username = RowInfo.getString("username");
        String password = RowInfo.getString("password");
        String email = RowInfo.getString("email");
        return new UserData(username, password, email);
    }

    private AuthData.AuthRecord readAuth(ResultSet RowInfo) throws SQLException {
        String username = RowInfo.getString("username");
        String authToken = RowInfo.getString("authToken");
        return new AuthData.AuthRecord(username, authToken);
    }

    private GameData readGameData(ResultSet RowInfo) throws SQLException{
        int gameID = RowInfo.getInt("gameID");
        String whiteUsername = RowInfo.getString("whiteUsername");
        String blackUsername = RowInfo.getString("blackUsername");
        String gameName = RowInfo.getString("gameName");
        ChessGame game = RowInfo.getObject("game", ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    private GameInfo readGameInfo(ResultSet RowInfo) throws SQLException {
        int gameID = RowInfo.getInt("gameID");
        String whiteUsername = RowInfo.getString("whiteUsername");
        String blackUsername = RowInfo.getString("blackUsername");
        String gameName = RowInfo.getString("gameName");
        return new GameInfo(gameID, whiteUsername, blackUsername, gameName);
    }
}
