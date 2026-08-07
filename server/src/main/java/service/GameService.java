package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import service.gamerequests.*;
import model.AuthData;
import model.GameData;
import model.GameInfo;

import java.util.List;


public class GameService {
    private final UserService service;
    private final DataAccess dataAccess;
    // create this so I can go through the registeredData
    // have to have this so we can change between the different types flawlessly
    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        this.service = new UserService(dataAccess);
    }

    public ListResult list(ListRequest listRequest) throws DataAccessException {
        AuthData.AuthRecord authData = service.dataAccess.getAuthData(listRequest.authToken());
        if (authData == null) {
            throw new DataAccessException("401 Error: Unauthorized");
        }
        else {
            List<GameInfo> games = service.dataAccess.listGames();
            return new ListResult(games);
        }
    }

    public CreateResult create(CreateRequest createRequest) throws DataAccessException {
        AuthData.AuthRecord authData = service.dataAccess.getAuthData(createRequest.authToken());
        if (authData == null) {
            throw new DataAccessException("401 Error: Unauthorized");
        }
        else if (createRequest.gameName() == null){
            throw new DataAccessException("400 Error: Bad request");
        }
        else {
            if (service.dataAccess.getGame(createRequest.gameName()) == null) {
                int newID = service.dataAccess.newGameID();
                GameData game = new GameData(newID, null, null, createRequest.gameName(), new ChessGame());
                service.dataAccess.createGame(game);
                return new CreateResult(newID);
            }
            throw new DataAccessException("400 Error: Bad request");
        }
    }

    public JoinResult join(JoinRequest joinRequest) throws DataAccessException {
        AuthData.AuthRecord authData = service.dataAccess.getAuthData(joinRequest.authToken());
        if (authData == null) {
            throw new DataAccessException("401 Error: Unauthorized");
        }
        if (joinRequest.playerColor() == null || joinRequest.gameID() == 0){
            throw new DataAccessException("400 Error: Bad request");
        }
        if (joinRequest.playerColor().equals("WHITE") || joinRequest.playerColor().equals("BLACK")){
            if (service.dataAccess.getColor(joinRequest.playerColor(), joinRequest.gameID())){
                service.dataAccess.joinGame(authData.username(), joinRequest.playerColor(), joinRequest.gameID());
                return new JoinResult();
            }
            List<GameInfo> games = service.dataAccess.listGames();
            for (GameInfo game: games) {
                if (authData.username().equals(game.blackUsername()) || authData.username().equals(game.whiteUsername())) {
                    service.dataAccess.joinGame(authData.username(), joinRequest.playerColor(), joinRequest.gameID());
                    return new JoinResult();
                }
            }
            throw new DataAccessException("403 Error: already taken");
        }
        throw new DataAccessException("400 Error: bad request");
    }

    public DeleteResult delete(DeleteRequest deleteRequest) throws DataAccessException {
        service.dataAccess.deleteAllGameData();
        service.dataAccess.deleteAllUserData();
        return new DeleteResult();
    }
}