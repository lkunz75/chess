package dataaccess;

import com.google.gson.Gson;
import model.UserData;

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

    private int executeUpdateUser(String statement, Object... params) throws DataAccessException {
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
            throw new DataAccessException(DataAccessException.Code.ServerError, String.format("unable to update database: %s, %s, %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
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
