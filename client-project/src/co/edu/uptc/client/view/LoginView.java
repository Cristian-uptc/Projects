package co.edu.uptc.client.view;

import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginView extends BaseFrame {

    private static final long serialVersionUID = 1L;

    private static final int FIELD_COLUMNS = 15;

    private final JTextField usernameField = new JTextField(FIELD_COLUMNS);
    private final JPasswordField passwordField = new JPasswordField(FIELD_COLUMNS);
    private final JButton loginButton = new JButton("Iniciar sesión");
    private final JButton registerButton = new JButton("Registrarse");

    public LoginView() {
        super("Reserva de canchas - Inicio de sesión");
        JPanel form = createFormPanel();
        addRow(form, "Usuario:", usernameField);
        addRow(form, "Contraseña:", passwordField);
        form.add(registerButton);
        form.add(loginButton);
        add(form);
        getRootPane().setDefaultButton(loginButton);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void setLoginAction(ActionListener action) {
        loginButton.addActionListener(action);
    }

    public void setRegisterAction(ActionListener action) {
        registerButton.addActionListener(action);
    }
}
