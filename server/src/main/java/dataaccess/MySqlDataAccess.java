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

public class MySqlDataAccess implements DataAccess{
    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    // we have to have a function here for every function in our MemoryDataAccess class!
    public UserData getUserData(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()){
            var statement = "SELECT * FROM userData WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username); // fills the ?
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("500 ERROR: unable to update database: %s", e.getMessage()));
        }
    }

    // just moved location to visually follow MemoryDataAccess
    public boolean getUserPassword(String username, String password) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()){
            var statement = "SELECT * FROM userData WHERE username=?";
            // * gets all the data in the row/all columns that belong in the table. Builds the rows of the table
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    // always call rs.next to get to the first row
                    if (!rs.next()) {return false;}
                    UserData user = readUser(rs);
                    if (user.password().equals(password)) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("500 ERROR: unable to update database: %s", e.getMessage()));
        }
        return false;
    }

    public void createUserData(UserData user) throws DataAccessException {
        var statement = "INSERT INTO userData (username, password, email, json) VALUES (?,?,?,?)";
        String json = new Gson().toJson(user);
        int rowsMade = executeUpdate(statement, user.username(), user.password(), user.email(), json);
        return;
    }

    public AuthData.AuthRecord createAuthData(UserData user) throws DataAccessException {
        var statement = "INSERT INTO authData (username, authToken) VALUES (?,?)";
        String authToken = AuthData.generateToken();
        executeUpdate(statement, user.username(), authToken);
        return new AuthData.AuthRecord(user.username(), authToken);
    }

    public AuthData.AuthRecord getAuthData(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()){
            var statement = "SELECT * FROM authData WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken); // fills the ?
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {return null;}
                    return readAuth(rs);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("500 ERROR: unable to update database: %s", e.getMessage()));
        }
    }

    public void deleteAuthToken(String authToken) throws DataAccessException {
        var statement = "DELETE FROM authData WHERE authToken=?";
        executeUpdate(statement, authToken);
    }

    public List<GameInfo> listGames() throws DataAccessException{
        List<GameInfo> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM gameData";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGameInfo(rs));
                    }
                    return result;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("500 Error: unable to update database: %s", e.getMessage()));
        }
    }

    public void createGame(GameData game) throws DataAccessException {
        var statement = "INSERT INTO gameData (gameID, whiteUsername, blackUsername, gameName, game, json) VALUES (?,?,?,?,?,?)";
        String json = new Gson().toJson(game);
        String playedGame = new Gson().toJson(game.game());
        int rowsMade = executeUpdate(statement, game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), playedGame, json);
        if (rowsMade != 1) {throw new DataAccessException("500 Error: Did not make");};
    }

    public int newGameID() throws DataAccessException {
        int max = 0;
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM gameData";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                try (ResultSet rs = ps.executeQuery()) {
                    while(rs.next()) {
                        //without this code quality is overnested, so I looked up the Java equivalent to python max
                        max = Math.max(max, rs.getInt("gameID"));
                    }
                }
                return max+1;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("500 Error: unable to update database: %s", e.getMessage()));
        }
    }

    public boolean getColor(String color, int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM gameData WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {return false;}
                    GameData gameData = readGameData(rs);
                    if (color.equals("WHITE") && gameData.whiteUsername() == null) {return true;}
                    if (color.equals("BLACK") && gameData.blackUsername() == null) {return true;}
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("500 Error: unable to update database: %s", e.getMessage()));
        }
        return false;
    }

    public GameData getGame(String gameName) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()){
            var statement = "SELECT * FROM gameData WHERE gameName=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, gameName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGameData(rs);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("500 Error: unable to update database: %s", e.getMessage()));
        }
        return null;
    }

    public void joinGame(String username, String color, int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM gameData WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {return;}
                    GameData gameData = readGameData(rs);
                    if (color.equals("WHITE") && gameData.whiteUsername() == null) {
                        executeUpdate("UPDATE gameData SET whiteUsername =? WHERE gameID=?", username, gameID);
                        return;
                    }
                    if (color.equals("BLACK") && gameData.blackUsername() == null) {
                        executeUpdate("UPDATE gameData SET blackUsername =? WHERE gameID=?", username, gameID);
                        return;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("500 Error: unable to update database: %s", e.getMessage()));
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
                    if (param instanceof String p) {ps.setString(i + 1, p);}
                    else if (param instanceof Integer p) {ps.setInt(i + 1, p);}
                    else if (param == null) {ps.setNull(i + 1, NULL);}
                }
                return ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("500 ERROR: unable to update database: %s", e.getMessage()));
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
              INDEX(password),
              INDEX(email)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS gameData (
              `gameID` int NOT NULL,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `game` TEXT,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameID`),
              INDEX(whiteUsername),
              INDEX(blackUsername),
              INDEX(gameName)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS authData (
              `username` varchar(256) NOT NULL,
              `authToken` varchar(256) NOT NULL,
              PRIMARY KEY (authToken)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
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
            throw new DataAccessException(String.format("500 ERROR: Unable to configure database: %s", ex.getMessage()));
        }
    }

    private UserData readUser(ResultSet rowInfo) throws SQLException {
        String username = rowInfo.getString("username");
        String password = rowInfo.getString("password");
        String email = rowInfo.getString("email");
        return new UserData(username, password, email);
    }

    private AuthData.AuthRecord readAuth(ResultSet rowInfo) throws SQLException {
        String username = rowInfo.getString("username");
        String authToken = rowInfo.getString("authToken");
        return new AuthData.AuthRecord(username, authToken);
    }

    private GameData readGameData(ResultSet rowInfo) throws SQLException{
        int gameID = rowInfo.getInt("gameID");
        String whiteUsername = rowInfo.getString("whiteUsername");
        String blackUsername = rowInfo.getString("blackUsername");
        String gameName = rowInfo.getString("gameName");

        //ChessGame serialization and deserialization
        //Is this where I should implement this? Nothing is actually getting changed within my chess game
        var serializer = new Gson();
        ChessGame chessBoardJson = serializer.fromJson(rowInfo.getString("game"), ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, chessBoardJson);
    }

    private GameInfo readGameInfo(ResultSet rowInfo) throws SQLException {
        int gameID = rowInfo.getInt("gameID");
        String whiteUsername = rowInfo.getString("whiteUsername");
        String blackUsername = rowInfo.getString("blackUsername");
        String gameName = rowInfo.getString("gameName");
        return new GameInfo(gameID, whiteUsername, blackUsername, gameName);
    }
}