package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;
import model.UserData;
import service.*;
import service.gameRequests.*;
import service.userRequests.*;

public class Server {

    private final Javalin javalin;
    private final UserService userService = new UserService();
    private final GameService gameService = new GameService();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::registerHandler);
        javalin.delete("/db", this::deleteHandler);
        javalin.post("/session", this::loginHandler);
        javalin.delete("/session", this::logoutHandler);
        javalin.post("/game", this::createHandler);
        javalin.get("/game", this::listHandler);
        javalin.put("/game", this::joinHandler);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    public void parseExceptions(DataAccessException e, Context context) {
        // this gets the first 3, since I've coded it to always start with the numbers
        // parseInt turns it into integers
        String message = e.getMessage();
        context.status(Integer.parseInt(message.substring(0,3)));
        context.result(new Gson().toJson(new ErrorMessage(message)));
    }

    // make a Handler() function
    private void registerHandler(Context context) throws DataAccessException {
        UserData user = new Gson().fromJson(context.body(), UserData.class); // deserialize
        RegisterRequest registerRequest = new RegisterRequest(user.username(), user.password(), user.email());
        try{
            RegisterResult registerResult = userService.register(registerRequest);
            context.result(new Gson().toJson(registerResult)); // serialized it
        } catch (DataAccessException e) {
            parseExceptions(e, context);
        }
    }

    private void loginHandler(Context context) throws DataAccessException {
        UserData user = new Gson().fromJson(context.body(), UserData.class); // deserialize
        LoginRequest loginRequest = new LoginRequest(user.username(), user.password());
        try {
            LoginResult loginResult = userService.login(loginRequest);
            context.result(new Gson().toJson(loginResult)); // serialized it
        } catch (DataAccessException e) {
            parseExceptions(e, context);
        }
    }

    private void logoutHandler(Context context) throws DataAccessException {
        LogoutRequest logoutRequest = new LogoutRequest(context.header("authorization"));
        try {
            LogoutResult logoutResult = userService.logout(logoutRequest);
            context.result(new Gson().toJson(logoutResult)); // serialized it
        } catch (DataAccessException e) {
            parseExceptions(e, context);
        }
    }

    private void createHandler(Context context) throws DataAccessException{
        String authToken = context.header("authorization");
        // looked up this notation
        JsonObject json = new Gson().fromJson(context.body(), JsonObject.class);
        String gameName = null;
        if (json != null && json.has("gameName") && !json.get("gameName").isJsonNull()) {
            // have to check things this way to make sure we can check it
            gameName = json.get("gameName").getAsString();
        }
        CreateRequest createRequest = new CreateRequest(authToken, gameName);
        try {
            CreateResult createResult = gameService.create(createRequest);
            context.result(new Gson().toJson(createResult)); // serialized it
        } catch (DataAccessException e) {
            parseExceptions(e, context);
        }
    }

    private void listHandler(Context context) throws DataAccessException{
        ListRequest listRequest = new ListRequest(context.header("authorization"));
        try {
            ListResult listResult = gameService.list(listRequest);
            context.result(new Gson().toJson(listResult)); // serialized it
        } catch (DataAccessException e) {
            parseExceptions(e, context);
        }
    }

    private void joinHandler(Context context) throws DataAccessException{
        //playerColor, gameID
        String authToken = context.header("authorization");
        String playerColor = null;
        int gameID = 0;
        JsonObject json = new Gson().fromJson(context.body(), JsonObject.class);
        if (json != null && json.has("playerColor") && !json.get("playerColor").isJsonNull()) {
            playerColor = json.get("playerColor").getAsString();
        }
        if (json != null && json.has("gameID") && !json.get("gameID").isJsonNull()) {
            // have to check things this way to make sure we can check it
            gameID = json.get("gameID").getAsInt();
        }
        JoinRequest joinRequest = new JoinRequest(authToken, playerColor, gameID);
        try {
            JoinResult joinResult = gameService.join(joinRequest);
            context.result(new Gson().toJson(joinResult)); // serialized it
        } catch (DataAccessException e) {
            parseExceptions(e, context);
        }
    }

    private void deleteHandler(Context context) throws DataAccessException {
        DeleteUserRequest deleteUserRequest = new DeleteUserRequest();
        DeleteUserResult deleteUserResult = userService.deleteUser(deleteUserRequest);
        context.result(new Gson().toJson(deleteUserResult));
        DeleteRequest deleteRequest = new DeleteRequest();
        DeleteResult deleteResult = gameService.delete(deleteRequest);
        context.result(new Gson().toJson(deleteResult));
    }
}