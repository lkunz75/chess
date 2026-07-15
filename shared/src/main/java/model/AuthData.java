package model;
import java.util.UUID;

public class AuthData {
    // create an authToken
    record AuthRecord(String authToken, String username) {}

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
