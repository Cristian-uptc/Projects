package co.edu.uptc.client.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class AdminView extends BaseFrame {

    private static final long serialVersionUID = 1L;

    private static final int FIELD_COLUMNS = 12;
    private static final int WINDOW_WIDTH = 760;
    private static final int WINDOW_HEIGHT = 500;

    private final ReadOnlyTable courtsTable = new ReadOnlyTable(
            "ID", "Nombre", "Deporte", "Capacidad", "Tarifa por hora", "Estado");
    private final JTextField nameField = new JTextField(FIELD_COLUMNS);
    private final JTextField sportField = new JTextField(FIELD_COLUMNS);
    private final JTextField capacityField = new JTextField(FIELD_COLUMNS);
    private final JTextField rateField = new JTextField(FIELD_COLUMNS);
    private final JButton addButton = new JButton("Agregar cancha");
    private final JButton editButton = new JButton("Guardar cambios");
    private final JButton deactivateButton = new JButton("Desactivar cancha");
    private final JButton logoutButton = new JButton("Cerrar sesión");

    public AdminView() {
        super("Reserva de canchas - Administrador");
        JPanel form = createFormPanel();
        addRow(form, "Nombre:", nameField);
        addRow(form, "Deporte:", sportField);
        addRow(form, "Capacidad:", capacityField);
        addRow(form, "Tarifa por hora:", rateField);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(addButton);
        buttons.add(editButton);
        buttons.add(deactivateButton);
        buttons.add(logoutButton);
        JPanel south = new JPanel(new BorderLayout());
        south.add(form, BorderLayout.CENTER);
        south.add(buttons, BorderLayout.SOUTH);
        add(new JScrollPane(courtsTable), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
    }

    public void setCourtRows(List<Object[]> rows) {
        courtsTable.setRows(rows);
    }

    public int getSelectedRow() {
        return courtsTable.getSelectedRow();
    }

    public String getCourtName() {
        return nameField.getText().trim();
    }

    public String getSport() {
        return sportField.getText().trim();
    }

    public String getCapacityText() {
        return capacityField.getText().trim();
    }

    public String getRateText() {
        return rateField.getText().trim();
    }

    public void setForm(String name, String sport, String capacity, String rate) {
        nameField.setText(name);
        sportField.setText(sport);
        capacityField.setText(capacity);
        rateField.setText(rate);
    }

    public void setSelectionListener(Runnable listener) {
        courtsTable.setSelectionListener(listener);
    }

    public void setAddAction(ActionListener action) {
        addButton.addActionListener(action);
    }

    public void setEditAction(ActionListener action) {
        editButton.addActionListener(action);
    }

    public void setDeactivateAction(ActionListener action) {
        deactivateButton.addActionListener(action);
    }

    public void setLogoutAction(ActionListener action) {
        logoutButton.addActionListener(action);
    }
}
