package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;

import java.util.Collection;
import java.util.List;


public class GameService {
    // create this so I can go through the registeredData
    UserService service = new UserService();
    public ListResult list(ListRequest listRequest) throws DataAccessException {
        try {
            AuthData.AuthRecord authData = service.registeredData.getAuthData(listRequest.authToken());
            if (authData == null) {
                throw new DataAccessException("401 Error: Unauthorized");
            }
            else {
                List<List<String>> games = service.registeredData.listGames();
                return new ListResult(games);
            }
        } catch (DataAccessException e) {
            throw new DataAccessException("401 Error: Unauthorized");
        }
    }

    public CreateResult create(CreateRequest createRequest) throws DataAccessException {
        try {
            AuthData.AuthRecord authData = service.registeredData.getAuthData(createRequest.authToken());
            if (authData == null) {
                throw new DataAccessException("401 Error: Unauthorized");
            }
            else {
                if (service.registeredData.getGame(createRequest.gameName()) == null) {
                    int newID = service.registeredData.newGameID();
                    GameData game = new GameData(newID, null, null, createRequest.gameName(), null);
                    service.registeredData.createGame(game);
                    return new CreateResult(newID);
                }
                throw new DataAccessException("401 Error: Unauthorized");
            }
        } catch (DataAccessException e) {
            throw new DataAccessException("401 Error: Unauthorized");
        }
    }

    public JoinResult join(JoinRequest joinRequest) throws DataAccessException {
        try {
            AuthData.AuthRecord authData = service.registeredData.getAuthData(joinRequest.authToken());
            if (authData== null) {
                throw new DataAccessException("401 Error: Unauthorized");
            }
            else {
                if (service.registeredData.getColor(joinRequest.color(), joinRequest.GameID())){
                    service.registeredData.joinGame(authData.username(), joinRequest.color(), joinRequest.GameID());
                    return new JoinResult();
                }
                throw new DataAccessException("401 Error: Unauthorized");
            }
        } catch (DataAccessException e) {
            throw new DataAccessException("401 Error: Unauthorized");
        }
    }

    public DeleteResult delete(DeleteRequest deleteRequest) throws DataAccessException {
        service.registeredData.deleteAllGameData();
        service = new UserService();
        return new DeleteResult();
    }
}