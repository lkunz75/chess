package model;
import chess.ChessGame;
// user this format. Classes hold all info you need for DataTypes
public record GameData (int gameID, String whiteUsername, String blackUsername, ChessGame game, String gameName) {}
