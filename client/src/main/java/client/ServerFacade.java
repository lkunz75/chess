package client; // needs to be moved, but not sure how to still access gamerequests

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.gamerequests.*;
import service.userrequests.*;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    
    public ServerFacade(String url) {
        serverUrl = url;
    }


    public RegisterResult register(RegisterRequest registerRequest) throws Exception {
        var request = buildRequest("POST", "/user", registerRequest, null);
        var response = sendRequest(request);
        return handleResponse(response, RegisterResult.class);
    }

    public LoginResult login(LoginRequest loginRequest) throws Exception {
        var request = buildRequest("POST", "/session", loginRequest, null);
        var response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws Exception {
        var request = buildRequest("DELETE", "/session", logoutRequest, logoutRequest.authToken());
        // uses authorization header!!
        var response = sendRequest(request);
        return handleResponse(response, LogoutResult.class);
    }

    public void delete(DeleteRequest deleteRequest, DeleteUserRequest deleteUserRequest) throws Exception {
        var request = buildRequest("DELETE", "/db", deleteRequest, null);
        var response = sendRequest(request);
        handleResponse(response, DeleteResult.class);
        var request2 = buildRequest("DELETE", "/db", deleteUserRequest, null);
        var response2 = sendRequest(request2);
        handleResponse(response, DeleteUserResult.class);
    }

    public CreateResult create(CreateRequest createRequest) throws Exception {
        var request = buildRequest("POST", "/game", createRequest, createRequest.authToken());
        var response = sendRequest(request);
        return handleResponse(response, CreateResult.class);
    }

    public ListResult list(ListRequest listRequest) throws Exception {
        var request = buildRequest("GET", "/game", listRequest, listRequest.authToken());
        var response = sendRequest(request);
        return handleResponse(response, ListResult.class);
    }

    public JoinResult join(JoinRequest joinRequest) throws Exception {
        var request = buildRequest("PUT", "/game", joinRequest, joinRequest.authToken());
        var response = sendRequest(request);
        return handleResponse(response, JoinResult.class);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        // creates the new URL with the request info
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (authToken != null) {
            // have to check AuthToken so authorization checks can be run
            request.setHeader("authorization", authToken );
        }
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
    
    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws Exception {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body!= null) {
                var map = new Gson().fromJson(body, HashMap.class);
                String message = map.get("message").toString();
                throw new Exception(message.substring(3));
            }
            throw new Exception(String.valueOf(status)); // to make it a string
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