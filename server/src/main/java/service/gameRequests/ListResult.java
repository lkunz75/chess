package service.gameRequests;
import model.GameInfo;
import java.util.List;

public record ListResult(List<GameInfo> games) {}
