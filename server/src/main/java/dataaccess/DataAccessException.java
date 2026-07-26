package dataaccess;

import com.google.gson.Gson;
import org.eclipse.jetty.http.HttpStatus;
import service.ErrorMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception{
    public DataAccessException(String message) {
        super(message);
    }
    public DataAccessException(String message, Throwable ex) {
        super(message, ex);
    }

    public static DataAccessException fromJson(String json) {
        var map = new Gson().fromJson(json, HashMap.class);
        String message = map.get("message").toString();
        // does substring, because they do not need to 404 or number error
        return new DataAccessException(message.substring(3));
    }
}
