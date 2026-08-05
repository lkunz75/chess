package server;

import model.GameData;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    // should session come from MySQL

    public final ConcurrentHashMap<Integer, List<Session>> connections = new ConcurrentHashMap<>();

    public void add(Integer gameID, Session session) {
        List<Session> sessions = connections.get(gameID);
        // had a nullptr exception
        if (sessions == null) {
            List<Session> newList = new ArrayList<>();
            newList.add(session);
            connections.put(gameID, newList);
        }
        if (sessions != null) {
            sessions.add(session);
            connections.put(gameID, sessions);
        }
    }

    public void remove(Integer gameID, Session session) {
        List<Session> sessions = connections.get(gameID);
        sessions.remove(session);
        connections.put(gameID, sessions); // putting the new one in
    }

    // figure out how to serialize it in this

    public void broadcast(Session excludeSession, Integer gameID, String notification) throws IOException {
        List<Session> myConnections = connections.get(gameID);
        if (myConnections != null) {
            for (Session connection : myConnections) {
                if (connection.isOpen() && !connection.equals(excludeSession)) {
                    connection.getRemote().sendString(notification);
                }
            }
        }
    }



}
