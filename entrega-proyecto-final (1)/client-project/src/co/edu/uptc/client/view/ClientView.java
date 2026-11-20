package co.edu.uptc.client.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

public class ClientView extends BaseFrame {

    private static final long serialVersionUID = 1L;

    private static final int DAYS_AHEAD = 30;
    private static final int MARGIN = 8;
    private static final int WINDOW_WIDTH = 720;
    private static final int WINDOW_HEIGHT = 480;

    private final JTabbedPane tabs = new JTabbedPane();
    private final ReadOnlyTable courtsTable =
            new ReadOnlyTable("Nombre", "Deporte", "Capacidad", "Tarifa por hora");
    private final JComboBox<String> courtCombo = new JComboBox<>();
    private final JComboBox<LocalDate> dateCombo = new JComboBox<>();
    private final ReadOnlyTable slotsTable = new ReadOnlyTable("Horario", "Estado");
    private final JButton reserveButton = new JButton("Reservar");
    private final JButton waitlistButton = new JButton("Unirse a la lista de espera");
    private final ReadOnlyTable reservationsTable =
            new ReadOnlyTable("Cancha", "Fecha", "Horario", "Estado");
    private final ReadOnlyTable waitlistTable =
            new ReadOnlyTable("Cancha", "Fecha", "Horario", "Solicitada");
    private final JButton cancelButton = new JButton("Cancelar reserva");
    private final JButton logoutButton = new JButton("Cerrar sesión");

    public ClientView(String clientName) {
        super("Reserva de canchas - " + clientName);
        LocalDate today = LocalDate.now();
        for (int day = 0; day <= DAYS_AHEAD; day++) {
            dateCombo.addItem(today.plusDays(day));
        }
        tabs.addTab("Canchas", new JScrollPane(courtsTable));
        tabs.addTab("Reservar", buildBookingTab());
        tabs.addTab("Mis reservas", buildReservationsTab());
        JLabel welcome = new JLabel("Bienvenido, " + clientName);
        welcome.setBorder(new EmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.add(logoutButton);
        add(welcome, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
    }

    private JPanel buildBookingTab() {
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filters.add(new JLabel("Cancha:"));
        filters.add(courtCombo);
        filters.add(new JLabel("Fecha:"));
        filters.add(dateCombo);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(reserveButton);
        buttons.add(waitlistButton);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(filters, BorderLayout.NORTH);
        panel.add(new JScrollPane(slotsTable), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildReservationsTab() {
        JPanel reservations = new JPanel(new BorderLayout());
        reservations.setBorder(BorderFactory.createTitledBorder("Mis reservas"));
        reservations.add(new JScrollPane(reservationsTable), BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancelButton);
        reservations.add(buttons, BorderLayout.SOUTH);
        JPanel waitlist = new JPanel(new BorderLayout());
        waitlist.setBorder(BorderFactory.createTitledBorder("Mis solicitudes en lista de espera"));
        waitlist.add(new JScrollPane(waitlistTable), BorderLayout.CENTER);
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, MARGIN));
        panel.add(reservations);
        panel.add(waitlist);
        return panel;
    }

    public void setCourtRows(List<Object[]> rows) {
        courtsTable.setRows(rows);
    }

    public void setCourtNames(List<String> names) {
        courtCombo.removeAllItems();
        for (String name : names) {
            courtCombo.addItem(name);
        }
    }

    public int getSelectedCourtIndex() {
        return courtCombo.getSelectedIndex();
    }

    public LocalDate getSelectedDate() {
        return (LocalDate) dateCombo.getSelectedItem();
    }

    public void setSlotRows(List<Object[]> rows) {
        slotsTable.setRows(rows);
    }

    public int getSelectedSlotRow() {
        return slotsTable.getSelectedRow();
    }

    public void setReservationRows(List<Object[]> rows) {
        reservationsTable.setRows(rows);
    }

    public int getSelectedReservationRow() {
        return reservationsTable.getSelectedRow();
    }

    public void setWaitlistRows(List<Object[]> rows) {
        waitlistTable.setRows(rows);
    }

    public void setTabChangeListener(Runnable listener) {
        tabs.addChangeListener(event -> listener.run());
    }

    public void setSlotQueryListener(Runnable listener) {
        courtCombo.addActionListener(event -> listener.run());
        dateCombo.addActionListener(event -> listener.run());
    }

    public void setReserveAction(ActionListener action) {
        reserveButton.addActionListener(action);
    }

    public void setWaitlistAction(ActionListener action) {
        waitlistButton.addActionListener(action);
    }

    public void setCancelAction(ActionListener action) {
        cancelButton.addActionListener(action);
    }

    public void setLogoutAction(ActionListener action) {
        logoutButton.addActionListener(action);
    }
}
