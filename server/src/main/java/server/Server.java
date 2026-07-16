package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.*;

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

    // make a Handler() function
    private void registerHandler(Context context) throws DataAccessException {
        UserData user = new Gson().fromJson(context.body(), UserData.class); // deserialize
        RegisterRequest registerRequest = new RegisterRequest(user.username(), user.password(), user.email());
        try{
            RegisterResult registerResult = userService.register(registerRequest);
            context.result(new Gson().toJson(registerResult)); // serialized it
        } catch (DataAccessException e) {
            context.status(403);
            context.result(e.getMessage()); // come back
        }
    }

    private void loginHandler(Context context) throws DataAccessException {
        UserData user = new Gson().fromJson(context.body(), UserData.class); // deserialize
        LoginRequest loginRequest = new LoginRequest(user.username(), user.password());
        try {
            LoginResult loginResult = userService.login(loginRequest);
            context.result(new Gson().toJson(loginResult)); // serialized it
        } catch (DataAccessException e) {
            context.status(400);
            context.result(e.getMessage()); // come back
        }
    }

    private void logoutHandler(Context context) throws DataAccessException {
        LogoutRequest logoutRequest = new LogoutRequest(context.header("authorization"));
        try {
            LogoutResult logoutResult = userService.logout(logoutRequest);
            context.result(new Gson().toJson(logoutResult)); // serialized it
        } catch (DataAccessException e) {
            context.status(401);
            context.result(e.getMessage()); // come back
        }
    }

    private void createHandler(Context context) throws DataAccessException{
        String authToken = context.header("authorization");
        // looked up this notation
        JsonObject json = new Gson().fromJson(context.body(), JsonObject.class);
        CreateRequest createRequest = new CreateRequest(authToken, json.get("gameName").getAsString());
        try {
            CreateResult createResult = gameService.create(createRequest);
            context.result(new Gson().toJson(createResult)); // serialized it
        } catch (DataAccessException e) {
            context.status(401);
            context.result(e.getMessage()); // come back
        }
    }

    private void listHandler(Context context) throws DataAccessException{
        ListRequest listRequest = new ListRequest(context.header("authorization"));
        try {
            ListResult listResult = gameService.list(listRequest);
            context.result(new Gson().toJson(listResult)); // serialized it
        } catch (DataAccessException e) {
            context.status(401);
            context.result(e.getMessage()); // come back
        }
    }

    private void joinHandler(Context context) throws DataAccessException{
        //playerColor, gameID
        String authToken = context.header("authorization");
        JsonObject json = new Gson().fromJson(context.body(), JsonObject.class);
        JoinRequest joinRequest = new JoinRequest(authToken, json.get("playerColor").getAsString(), json.get("gameID").getAsInt());
        try {
            JoinResult joinResult = gameService.join(joinRequest);
            context.result(new Gson().toJson(joinResult)); // serialized it
        } catch (DataAccessException e) {
            context.status(401);
            context.result(e.getMessage()); // come back
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