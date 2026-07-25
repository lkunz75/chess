package server;

import com.google.gson.Gson;

import dataaccess.*;
import service.*;
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
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws DataAccessException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body!= null) {
                throw DataAccessException.fromJson(body);
            }
            throw new DataAccessException(DataAccessException.fromHttpStatusCode(status));
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
