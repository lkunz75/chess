package service;

import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.GameInfo;
import service.gameRequests.*;

import java.util.List;


public class GameService {
    // create this so I can go through the registeredData
    UserService service = new UserService();
    public ListResult list(ListRequest listRequest) throws DataAccessException {
        AuthData.AuthRecord authData = service.registeredData.getAuthData(listRequest.authToken());
        if (authData == null) {
            throw new DataAccessException("401 Error: Unauthorized");
        }
        else {
            List<GameInfo> games = service.registeredData.listGames();
            return new ListResult(games);
        }
    }

    public CreateResult create(CreateRequest createRequest) throws DataAccessException {
        AuthData.AuthRecord authData = service.registeredData.getAuthData(createRequest.authToken());
        if (authData == null) {
            throw new DataAccessException("401 Error: Unauthorized");
        }
        else if (createRequest.gameName() == null){
            throw new DataAccessException("400 Error: Bad request");
        }
        else {
            if (service.registeredData.getGame(createRequest.gameName()) == null) {
                int newID = service.registeredData.newGameID();
                GameData game = new GameData(newID, null, null, createRequest.gameName(), null);
                service.registeredData.createGame(game);
                return new CreateResult(newID);
            }
            throw new DataAccessException("400 Error: Bad request");
        }
    }

    public JoinResult join(JoinRequest joinRequest) throws DataAccessException {
        AuthData.AuthRecord authData = service.registeredData.getAuthData(joinRequest.authToken());
        if (authData == null) {
            throw new DataAccessException("401 Error: Unauthorized");
        }
        if (joinRequest.color() == null || joinRequest.gameID() == 0){
            throw new DataAccessException("400 Error: Bad request");
        }
        if (joinRequest.color().equals("WHITE") || joinRequest.color().equals("BLACK")){
            if (service.registeredData.getColor(joinRequest.color(), joinRequest.gameID())){
                service.registeredData.joinGame(authData.username(), joinRequest.color(), joinRequest.gameID());
                return new JoinResult();
            }
            throw new DataAccessException("403 Error: already taken");
        }
        throw new DataAccessException("400 Error: bad request");
    }

    public DeleteResult delete(DeleteRequest deleteRequest) throws DataAccessException {
        service.registeredData.deleteAllGameData();
        service = new UserService();
        return new DeleteResult();
    }
}