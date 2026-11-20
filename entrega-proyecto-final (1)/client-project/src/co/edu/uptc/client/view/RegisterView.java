package co.edu.uptc.client.view;

import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class RegisterView extends BaseFrame {

    private static final long serialVersionUID = 1L;

    private static final int FIELD_COLUMNS = 15;

    private final JTextField fullNameField = new JTextField(FIELD_COLUMNS);
    private final JTextField documentField = new JTextField(FIELD_COLUMNS);
    private final JTextField phoneField = new JTextField(FIELD_COLUMNS);
    private final JTextField usernameField = new JTextField(FIELD_COLUMNS);
    private final JPasswordField passwordField = new JPasswordField(FIELD_COLUMNS);
    private final JButton registerButton = new JButton("Registrarse");
    private final JButton cancelButton = new JButton("Cancelar");

    public RegisterView() {
        super("Reserva de canchas - Registro de cliente");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel form = createFormPanel();
        addRow(form, "Nombre:", fullNameField);
        addRow(form, "Documento:", documentField);
        addRow(form, "Teléfono:", phoneField);
        addRow(form, "Usuario:", usernameField);
        addRow(form, "Contraseña:", passwordField);
        form.add(cancelButton);
        form.add(registerButton);
        add(form);
        getRootPane().setDefaultButton(registerButton);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    public String getFullName() {
        return fullNameField.getText().trim();
    }

    public String getDocument() {
        return documentField.getText().trim();
    }

    public String getPhone() {
        return phoneField.getText().trim();
    }

    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void setRegisterAction(ActionListener action) {
        registerButton.addActionListener(action);
    }

    public void setCancelAction(ActionListener action) {
        cancelButton.addActionListener(action);
    }
}
