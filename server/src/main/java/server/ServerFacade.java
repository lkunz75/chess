package server;

import com.google.gson.Gson;

import dataaccess.*;
import service.*;
import service.gamerequests.*;
import service.userrequests.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    
    public ServerFacade(String url) {
        serverUrl = url;
    }


    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        var request = buildRequest("POST", "/user", registerRequest);
        var response = sendRequest(request);
        return handleResponse(response, RegisterResult.class);
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        var request = buildRequest("POST", "/session", loginRequest);
        var response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws DataAccessException {
        var request = buildRequest("DELETE", "/session", logoutRequest);
        var response = sendRequest(request);
        return handleResponse(response, LogoutResult.class);
    }

    public DeleteUserResult deleteUser(DeleteUserRequest deleteUserRequest) throws DataAccessException {
        var request = buildRequest("DELETE", "db", deleteUserRequest);
        var response = sendRequest(request);
        return handleResponse(response, DeleteUserResult.class);
    }

    public DeleteResult delete(DeleteRequest deleteRequest) throws DataAccessException {
        var request = buildRequest("DELETE", "db", deleteRequest);
        var response = sendRequest(request);
        return handleResponse(response, DeleteResult.class);
    }

    public CreateResult create(CreateRequest createRequest) throws DataAccessException {
        var request = buildRequest("POST", "/game", createRequest);
        var response = sendRequest(request);
        return handleResponse(response, CreateResult.class);
    }

    public ListResult list(ListRequest listRequest) throws DataAccessException {
        var request = buildRequest("GET", "/game", listRequest);
        var response = sendRequest(request);
        return handleResponse(response, ListResult.class);
    }

    public JoinResult join(JoinRequest joinRequest) throws DataAccessException {
        var request = buildRequest("PUT", "/game", joinRequest);
        var response = sendRequest(request);
        return handleResponse(response, JoinResult.class);
    }
    
    // followed PetShop
    private HttpRequest buildRequest(String method, String path, Object body) {
        // creates the new URL with the request info
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body)); 
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }
    
    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } 
        return HttpRequest.BodyPublishers.noBody();
    }
    
    private HttpResponse<String> sendRequest(HttpRequest request) throws DataAccessException {
        try {
            // figure out what send should be!
            return client.send(request, HttpResponse.BodyHandlers.ofString());
            //Exception is a more general thing that will catch the errors send might produce
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws DataAccessException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body!= null) {
                // figure out how to do this!
                throw DataAccessException.fromJson(body);
            }
            throw new DataAccessException(String.valueOf(status)); // to make it a string
        }
        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }
        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}