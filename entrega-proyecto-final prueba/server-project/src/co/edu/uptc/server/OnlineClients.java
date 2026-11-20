package co.edu.uptc.server;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import co.edu.uptc.network.Response;

public class OnlineClients {

    private final Map<String, ClientHandler> handlers = new ConcurrentHashMap<>();

    public void add(String username, ClientHandler handler) {
        handlers.put(username, handler);
    }

    public void remove(String username, ClientHandler handler) {
        handlers.remove(username, handler);
    }

    public void notifyClient(String username, Serializable data) {
        ClientHandler handler = handlers.get(username);
        if (handler != null) {
            handler.send(Response.notification(data));
        }
    }
}
