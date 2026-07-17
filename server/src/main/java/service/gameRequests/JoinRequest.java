package service.gameRequests;
public record JoinRequest(String authToken, String color, int gameID) {}
