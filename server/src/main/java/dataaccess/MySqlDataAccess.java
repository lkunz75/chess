package dataaccess;

import java.sql.Connection;
import java.sql.SQLDataException;
import java.sql.SQLException;

public class MySqlDataAccess {
    public MySqlDataAccess() throws DataAccessException{
        configureDatabase;
    }

    // we have to have a function here for every function in our MemoryDataAccess class!




    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS userData (
              'username' varchar(256) NOT NULL,
              'password' varchar(256) NOT NULL,
              'email' varchar(256) NOT NULL,
              'json' TEXT DEFAULT NULL,
              PRIMARY KEY ('username'),
              INDEX(password)
              INDEX(email)
            ) ENGINE=InnoDB DEFAULT CHARSET=utft8mb4 COLLATE=utf8mb_0900_ai_ci
            """,
            //int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game
           // do I auto increment here too??
            // how do I declare it's a chess game??
            """
            CREATE TABLE IF NOT EXISTS gameData (
              'gameID' int NOT NULL AUTO_INCREMENT,
              'whiteUsername' varchar(256) NOT NULL,
              'blackUsername' varchar(256) NOT NULL,
              'gameName' varchar(256) NOT NULL,
              'game' ENUM(ChessGame),
              'json' TEXT DEFAULT NULL,
              PRIMARY KEY ('gameID'),
              INDEX(whiteUSername)
              INDEX(blackUSername)
              INDEX(gameName)
              INDEX(game)
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
            throw new DataAccessException(DataAccessException.Code.ServerError, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
