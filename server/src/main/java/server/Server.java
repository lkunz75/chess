package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;
import model.UserData;
import service.*;

public class Server {

    private final Javalin javalin;
    private final UserService service = new UserService();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::registerHandler);
        javalin.delete("/db", this::deleteHandler);
        javalin.post("/session", this::loginHandler);
        javalin.delete("/session", this::logoutHandler);

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
            RegisterResult registerResult = service.register(registerRequest);
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
            LoginResult loginResult = service.login(loginRequest);
            context.result(new Gson().toJson(loginResult)); // serialized it
        } catch (DataAccessException e) {
            context.status(400);
            context.result(e.getMessage()); // come back
        }
    }

    private void logoutHandler(Context context) throws DataAccessException {
        UserData user = new Gson().fromJson(context.body(), UserData.class); // deserialize
        LogoutRequest logoutRequest = new LogoutRequest(user.username(), user.password());
        try {
            LogoutResult logoutResult = service.login(logoutRequest);
            context.result(new Gson().toJson(logoutResult)); // serialized it
        } catch (DataAccessException e) {
            context.status(400);
            context.result(e.getMessage()); // come back
        }
    }

    private void deleteHandler(Context context) throws DataAccessException {
        DeleteUserRequest deleteUserRequest = new DeleteUserRequest();
        DeleteUserResult deleteUserResult = service.deleteUser(deleteUserRequest);
        context.result(new Gson().toJson(deleteUserResult));
    }


}
