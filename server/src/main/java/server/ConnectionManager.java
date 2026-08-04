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
        connections.put(gameID, session);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    // figure out how to serialize it in this

    public void broadcast(Session excludeSession, ServerMessage notification) throws IOException {
        for (Session s: connections.values()) {
            if (s.isOpen() && !s.equals(excludeSession)) {
                s.getRemote().sendString(notification);
            }
        }
    }



}
