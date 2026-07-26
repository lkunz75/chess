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

    public String toJson() {
        int code = Integer.parseInt(getMessage().substring(0,3));
        return new Gson().toJson(Map.of("message", getMessage(), "status", code));
    }

    public static DataAccessException fromJson(String json) {
        var map = new Gson().fromJson(json, HashMap.class);
        var status = HttpStatus.Code.valueOf(map.get("status").toString());
        String message = map.get("message").toString();
        return new DataAccessException(message);
    }
}
