package co.edu.uptc.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import co.edu.uptc.model.Administrator;
import co.edu.uptc.model.ReservationSystem;
import co.edu.uptc.persistence.DataStore;
import co.edu.uptc.server.presenter.ServerPresenter;

public class Server {

    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    private final int port;

    public Server(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        new Server(readPort(args)).start();
    }

    private static int readPort(String[] args) {
        if (args.length == 0) {
            return ServerConfig.DEFAULT_PORT;
        }
        try {
            int value = Integer.parseInt(args[0].trim());
            if (value < 1 || value > 65535) {
                throw new NumberFormatException();
            }
            return value;
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid port ''{0}''. Using {1}",
                    new Object[]{args[0], String.valueOf(ServerConfig.DEFAULT_PORT)});
            return ServerConfig.DEFAULT_PORT;
        }
    }

    public void start() {
        DataStore dataStore = new DataStore(ServerConfig.DATA_FILE);
        ReservationSystem system = loadSystem(dataStore);
        OnlineClients onlineClients = new OnlineClients();
        ServerPresenter presenter = new ServerPresenter(system, dataStore, onlineClients);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOGGER.log(Level.INFO, "Server listening on port {0}", String.valueOf(port));
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket, presenter, onlineClients)).start();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Server stopped", e);
        }
    }

    private ReservationSystem loadSystem(DataStore dataStore) {
        ReservationSystem system = dataStore.load();
        if (system == null) {
            system = new ReservationSystem(new Administrator(ADMIN_USERNAME, ADMIN_PASSWORD));
            dataStore.save(system);
        }
        return system;
    }
}