package commands;

import java.net.http.WebSocket;
import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand {

    private final CommandType commandType;

    private final String authToken;

    private final Integer gameID;

    public UserGameCommand(CommandType commandType, String authToken, Integer gameID) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
    }

    public enum CommandType {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    // Just needs to store data

    /* MAKE_MOVE needs to have chess move in it
    The logic below should go into the Handler
    * make_move needs at least 4 subclasses.
    * 1 get current board and board info
    * 2 get move information
    * 3 get possible moves
    * 4 mark them on the current board
    * 5 send out a notification based on outcome
    * Also check checkmate, stalemate, ect. */

    /* CONNECT
    * used for a user to make a WebSocket connection
    * as a player or observer*/
    public String connect() {
        // get something to check if its even valid
        if (getAuthToken() != null && getGameID() > 0) {
            // connect to WebSocket

        }
    }

    /* LEAVE
     * tells the server you are leaving the game
     * so notifications stop
     */

    /* RESIGN
    * forfeits the match and ends the game
    * no more moves can be made
     */

    public CommandType getCommandType() {
        return commandType;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Integer getGameID() {
        return gameID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserGameCommand that)) {
            return false;
        }
        return getCommandType() == that.getCommandType() &&
                Objects.equals(getAuthToken(), that.getAuthToken()) &&
                Objects.equals(getGameID(), that.getGameID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthToken(), getGameID());
    }
}
