package co.edu.uptc.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import co.edu.uptc.model.Administrator;
import co.edu.uptc.model.ReservationSystem;
import co.edu.uptc.persistence.DataStore;

public class Server {

    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    public static void main(String[] args) {
        new Server().start();
    }

    public void start() {
        DataStore dataStore = new DataStore(ServerConfig.DATA_FILE);
        ReservationSystem system = loadSystem(dataStore);
        OnlineClients onlineClients = new OnlineClients();
        RequestProcessor processor = new RequestProcessor(system, dataStore, onlineClients);
        try (ServerSocket serverSocket = new ServerSocket(ServerConfig.PORT)) {
            LOGGER.log(Level.INFO, "Server listening on port {0}",
                    String.valueOf(ServerConfig.PORT));
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket, processor, onlineClients)).start();
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
