package model;
import java.util.UUID;

public class AuthData {
    // create an authToken
    public record AuthRecord(String username, String authToken) {} // tests caught the order being wrong! YAY!3

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
