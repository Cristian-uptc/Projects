package co.edu.uptc.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import co.edu.uptc.model.User;
import co.edu.uptc.network.Action;
import co.edu.uptc.network.Request;
import co.edu.uptc.network.Response;
import co.edu.uptc.server.presenter.ServerPresenter;

public class ClientHandler implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());

    private final Socket socket;
    private final ServerPresenter presenter;
    private final OnlineClients onlineClients;
    private final Session session = new Session();
    private ObjectOutputStream out;

    public ClientHandler(Socket socket, ServerPresenter presenter, OnlineClients onlineClients) {
        this.socket = socket;
        this.presenter = presenter;
        this.onlineClients = onlineClients;
    }

    @Override
    public void run() {
        try (Socket client = socket) {
            out = new ObjectOutputStream(client.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            while (!client.isClosed()) {
                handle((Request) in.readObject()); 
            }
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.INFO, "Client disconnected: {0}", e.toString());
        } finally {
            releaseSession();
        }
    }

    public synchronized void send(Response response) {
        try {
            out.reset();
            out.writeObject(response);
            out.flush();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Could not send to client", e);
        }
    }

    private void handle(Request request) {
        if (request.getAction() == Action.LOGOUT) {
            releaseSession(); 
        }
        Response response = presenter.process(request, session);
        if (request.getAction() == Action.LOGIN && response.isSuccess()) {
            onlineClients.add(session.getUser().getUsername(), this);
        }
        send(response);
    }

    private void releaseSession() {
        User user = session.getUser();
        if (user != null) {
            onlineClients.remove(user.getUsername(), this);
        }
    }
}