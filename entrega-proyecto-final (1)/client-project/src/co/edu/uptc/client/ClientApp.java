package co.edu.uptc.client;

import co.edu.uptc.client.communication.ClientConfig;
import co.edu.uptc.client.communication.ServerConnection;
import co.edu.uptc.client.presenter.AdminPresenter;
import co.edu.uptc.client.presenter.ClientPresenter;
import co.edu.uptc.client.presenter.LoginPresenter;
import co.edu.uptc.client.view.AdminView;
import co.edu.uptc.client.view.ClientView;
import co.edu.uptc.client.view.LoginView;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import co.edu.uptc.model.Client;
import co.edu.uptc.model.User;

public class ClientApp {

    private static final Logger LOGGER = Logger.getLogger(ClientApp.class.getName());

    private ServerConnection connection;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientApp().start());
    }

    private void start() {
        useSystemLookAndFeel();
        try {
            connection = new ServerConnection(ClientConfig.HOST, ClientConfig.PORT,
                    this::onDisconnected);
        } catch (IOException e) {
            showFatalError(Messages.error("CONNECTION_ERROR"));
            return;
        }
        Runtime.getRuntime().addShutdownHook(new Thread(connection::close));
        showLogin();
    }

    private void showLogin() {
        LoginView view = new LoginView();
        new LoginPresenter(view, connection, this::showMain);
        view.setVisible(true);
    }

    private void showMain(User user) {
        if (user instanceof Client) {
            ClientView view = new ClientView(((Client) user).getName());
            new ClientPresenter(view, connection, this::showLogin);
            view.setVisible(true);
        } else {
            AdminView view = new AdminView();
            new AdminPresenter(view, connection, this::showLogin);
            view.setVisible(true);
        }
    }

    private void onDisconnected() {
        SwingUtilities.invokeLater(() -> {
            showFatalError("Se perdió la conexión con el servidor.");
            System.exit(0);
        });
    }

    private void showFatalError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void useSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
            LOGGER.log(Level.FINE, "System look and feel not available", e);
        }
    }
}
