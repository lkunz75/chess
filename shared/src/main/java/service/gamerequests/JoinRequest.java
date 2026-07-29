package service.gamerequests;
public record JoinRequest(String authToken, String playerColor, int gameID) {}
