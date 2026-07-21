package dataaccess;

import com.google.gson.Gson;
import model.UserData;

import javax.xml.crypto.Data;
import java.sql.*;

import static java.sql.Types.NULL;

public class MySqlDataAccess {
    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    // we have to have a function here for every function in our MemoryDataAccess class!
    public UserData createUserData(UserData user) throws DataAccessException {
        var statement = "INSERT INTO userData (username, password, email, json) VALUES (?,?,?,?)";
        String json = new Gson().toJson(user);
        int rowsMade = executeUpdateUser(statement, user.username(), user.password(), user.email(), json);
        return new UserData(user.username(), user.password(), user.email());
    }

    public String getUserPassword(String username, String password) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()){
            var statement = "SELECT *, json FROM userData WHERE username=?";
            // * gets all the data in the row (all columns that belong in the table. Builds the rows of the table
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    ps.setString(1, username);
                    if (rs.next()) {
                        // could have a while loop to check uniqueness for gameData
                        // always call rs.next to get to the first row
                        String newPassword = readUser(rs);

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
            // move these into 3 levels
            """
            CREATE TABLE IF NOT EXISTS userData (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`username`),
              INDEX(password)
              INDEX(email)
            ) ENGINE=InnoDB DEFAULT CHARSET=utft8mb4 COLLATE=utf8mb_0900_ai_ci
            """,
            //int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game
            // how do I declare it's a chess game??
            """
            CREATE TABLE IF NOT EXISTS gameData (
              `gameID` int,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `game` ENUM(ChessGame),
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameID`),
              INDEX(whiteUSername),
              INDEX(blackUSername),
              INDEX(gameName),
              INDEX(game)
            ) ENGINE=InnoDB DEFAULT CHARSET=utft8mb4 COLLATE=utf8mb_0900_ai_ci
            """,
            // String username, String authToken
            """
            CREATE TABLE IF NOT EXISTS authData (
              `username` varchar(256) NOT NULL,
              `authToken` varchar(256) NOT NULL,
              PRIMARY KEY (`username`),
              INDEX(authToken),
            ) ENGINE=InnoDB DEFAULT CHARSET=utft8mb4 COLLATE=utf8mb_0900_ai_ci
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
            // come back
            throw new DataAccessException(DataAccessException.ServerError, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
