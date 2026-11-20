package co.edu.uptc.client.communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import co.edu.uptc.network.Request;
import co.edu.uptc.network.Response;

public class ServerConnection {

    private static final Logger LOGGER = Logger.getLogger(ServerConnection.class.getName());
    private static final int RESPONSE_TIMEOUT_SECONDS = 10;

    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private final BlockingQueue<Response> responses = new LinkedBlockingQueue<>();
    private final Runnable disconnectListener;
    private volatile Consumer<Object> notificationListener;
    private volatile boolean closed;

    public ServerConnection(String host, int port, Runnable disconnectListener) throws IOException {
        this.socket = new Socket(host, port);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.out.flush();
        this.in = new ObjectInputStream(socket.getInputStream());
        this.disconnectListener = disconnectListener;
        Thread reader = new Thread(this::readLoop);
        reader.setDaemon(true);
        reader.start();
    }

    public void setNotificationListener(Consumer<Object> listener) {
        this.notificationListener = listener;
    }

    public void close() {
        closed = true;
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.log(Level.FINE, "Error closing the connection", e);
        }
    }

    public synchronized Response send(Request request) {
        try {
            responses.clear();
            out.reset();
            out.writeObject(request);
            out.flush();
            Response response = responses.poll(RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            return response != null ? response : Response.error("CONNECTION_ERROR");
        } catch (IOException e) {
            return Response.error("CONNECTION_ERROR");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Response.error("CONNECTION_ERROR");
        }
    }

    private void readLoop() {
        try {
            while (true) {
                Response response = (Response) in.readObject();
                dispatch(response);
            }
        } catch (IOException | ClassNotFoundException e) {
            if (!closed) {
                disconnectListener.run();
            }
        }
    }

    private void dispatch(Response response) {
        Consumer<Object> listener = notificationListener;
        if (!response.isNotification()) {
            responses.add(response);
        } else if (listener != null) {
            listener.accept(response.getData());
        }
    }
}
