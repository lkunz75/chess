package server;

import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import messages.ServerMessage;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    // should session come from MySQL

    public final ConcurrentHashMap<Integer, List<Session>> connections = new ConcurrentHashMap<>();

    public void add(Integer gameID, Session session) {
        // come back and figure out how to put session into your list
        List<Session> sessions = connections.get(gameID);
        sessions.add(session);
        connections.put(gameID, sessions);
    }

    public void remove(Integer gameID, Session session) {
        List<Session> sessions = connections.get(gameID);
        sessions.remove(session);
        connections.put(gameID, sessions); // putting the new one in
    }

    // figure out how to serialize it in this

    public void broadcast(Session excludeSession, ServerMessage notification) throws IOException {
        for (List s: connections.values()) {
            for (Object session : s) {
                if (session.isOpen() && !session.equals(excludeSession)) {
                    session.getRemote().sendString(notification);
                }
            }
        }
    }



}
