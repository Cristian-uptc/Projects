package co.edu.uptc.client;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
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
        connection = connectInteractively();
        if (connection == null) {
            System.exit(0); 
        }
        Runtime.getRuntime().addShutdownHook(new Thread(connection::close));
        showLogin();
    }

    private ServerConnection connectInteractively() {
        JTextField hostField = new JTextField(ClientConfig.DEFAULT_HOST, 12);
        JTextField portField = new JTextField(String.valueOf(ClientConfig.DEFAULT_PORT), 6);
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("IP del servidor:"));
        panel.add(hostField);
        panel.add(new JLabel("Puerto:"));
        panel.add(portField);

        while (true) {
            int option = JOptionPane.showConfirmDialog(null, panel, "Conexión al servidor",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) {
                return null;
            }
            try {
                String host = hostField.getText().trim();
                int port = Integer.parseInt(portField.getText().trim());
                if (host.isEmpty() || port < 1 || port > 65535) {
                    throw new NumberFormatException();
                }
                return new ServerConnection(host, port, this::onDisconnected);
            } catch (NumberFormatException e) {
                showFatalError("IP o puerto inválidos.");
            } catch (IOException e) {
                showFatalError(Messages.error("CONNECTION_ERROR"));
            }
        }
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
