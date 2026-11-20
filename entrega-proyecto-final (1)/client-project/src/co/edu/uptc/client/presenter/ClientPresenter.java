package co.edu.uptc.client.presenter;

import co.edu.uptc.client.Messages;
import co.edu.uptc.client.communication.ServerConnection;
import co.edu.uptc.client.view.ClientView;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.SwingUtilities;
import co.edu.uptc.model.Court;
import co.edu.uptc.model.Reservation;
import co.edu.uptc.model.TimeSlot;
import co.edu.uptc.model.WaitlistRequest;
import co.edu.uptc.network.Action;
import co.edu.uptc.network.Response;

public class ClientPresenter extends BasePresenter<ClientView> {

    private static final int OPENING_HOUR = 6;
    private static final int LAST_START_HOUR = 21;

    private final Runnable onLogout;
    private List<Court> courts = new ArrayList<>();
    private List<Reservation> reservations = new ArrayList<>();

    public ClientPresenter(ClientView view, ServerConnection connection, Runnable onLogout) {
        super(view, connection);
        this.onLogout = onLogout;
        connection.setNotificationListener(this::onNotification);
        view.setTabChangeListener(this::refreshAll);
        view.setSlotQueryListener(this::loadSlots);
        view.setReserveAction(event -> reserve());
        view.setWaitlistAction(event -> joinWaitlist());
        view.setCancelAction(event -> cancelReservation());
        view.setLogoutAction(event -> logout());
        refreshAll();
    }

    private void refreshAll() {
        sendAsync(Action.GET_ACTIVE_COURTS, this::showCourts);
        loadReservations();
    }

    private void showCourts(Response response) {
        courts = response.getData();
        List<Object[]> rows = new ArrayList<>();
        List<String> names = new ArrayList<>();
        for (Court court : courts) {
            rows.add(new Object[]{court.getName(), court.getSport(), court.getCapacity(),
                court.getHourlyRate()});
            names.add(court.getName());
        }
        view.setCourtRows(rows);
        view.setCourtNames(names);
    }

    private void loadSlots() {
        Court court = selectedCourt();
        if (court == null) {
            return;
        }
        LocalDate date = view.getSelectedDate();
        sendAsync(Action.GET_OCCUPIED_SLOTS, response -> showSlots(date, response),
                court.getId(), date);
    }

    private void showSlots(LocalDate date, Response response) {
        List<TimeSlot> occupied = response.getData();
        Set<Integer> occupiedHours = new HashSet<>();
        for (TimeSlot slot : occupied) {
            occupiedHours.add(slot.getStartHour());
        }
        view.setSlotRows(buildSlotRows(date, occupiedHours));
    }

    private List<Object[]> buildSlotRows(LocalDate date, Set<Integer> occupiedHours) {
        List<Object[]> rows = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int hour = OPENING_HOUR; hour <= LAST_START_HOUR; hour++) {
            String state = "Disponible";
            if (!date.atTime(hour, 0).isAfter(now)) {
                state = "Pasada";
            } else if (occupiedHours.contains(hour)) {
                state = "Ocupada";
            }
            rows.add(new Object[]{Messages.hourRange(hour), state});
        }
        return rows;
    }

    private void reserve() {
        Court court = selectedCourt();
        int row = view.getSelectedSlotRow();
        if (court == null || row < 0) {
            view.showError(Messages.error("NO_SELECTION"));
            return;
        }
        sendAsync(Action.CREATE_RESERVATION, response -> {
            view.showInfo("Reserva creada con éxito.");
            refreshAll();
        }, court.getId(), view.getSelectedDate(), OPENING_HOUR + row);
    }

    private void joinWaitlist() {
        Court court = selectedCourt();
        int row = view.getSelectedSlotRow();
        if (court == null || row < 0) {
            view.showError(Messages.error("NO_SELECTION"));
            return;
        }
        sendAsync(Action.JOIN_WAITLIST, response -> {
            view.showInfo("Quedaste en la lista de espera de ese horario.");
            loadReservations();
        }, court.getId(), view.getSelectedDate(), OPENING_HOUR + row);
    }

    private void loadReservations() {
        sendAsync(Action.GET_MY_RESERVATIONS, reservationsResponse ->
                sendAsync(Action.GET_MY_WAITLIST, waitlistResponse ->
                        showReservations(reservationsResponse, waitlistResponse)));
    }

    private void showReservations(Response reservationsResponse, Response waitlistResponse) {
        reservations = reservationsResponse.getData();
        List<WaitlistRequest> requests = waitlistResponse.getData();
        LocalDateTime now = LocalDateTime.now();
        List<Object[]> reservationRows = new ArrayList<>();
        for (Reservation reservation : reservations) {
            TimeSlot slot = reservation.getTimeSlot();
            reservationRows.add(new Object[]{slot.getCourt().getName(), slot.getDate(),
                Messages.hourRange(slot.getStartHour()),
                Messages.status(reservation.getStatus(now))});
        }
        List<Object[]> waitlistRows = new ArrayList<>();
        for (WaitlistRequest request : requests) {
            TimeSlot slot = request.getTimeSlot();
            waitlistRows.add(new Object[]{slot.getCourt().getName(), slot.getDate(),
                Messages.hourRange(slot.getStartHour()),
                request.getRequestDateTime().toLocalTime().withNano(0)});
        }
        view.setReservationRows(reservationRows);
        view.setWaitlistRows(waitlistRows);
    }

    private void cancelReservation() {
        int row = view.getSelectedReservationRow();
        if (row < 0 || row >= reservations.size()) {
            view.showError(Messages.error("NO_SELECTION"));
            return;
        }
        if (view.confirm("¿Cancelar la reserva seleccionada?")) {
            sendAsync(Action.CANCEL_RESERVATION, response -> {
                view.showInfo("Reserva cancelada.");
                refreshAll();
            }, reservations.get(row).getId());
        }
    }

    private void onNotification(Object data) {
        if (data instanceof Reservation) {
            Reservation reservation = (Reservation) data;
            SwingUtilities.invokeLater(() -> {
                view.showInfo("¡Se te asignó un horario liberado de la lista de espera!\n"
                        + Messages.describe(reservation));
                refreshAll();
            });
        }
    }

    private void logout() {
        sendAsync(Action.LOGOUT, response -> {
            connection.setNotificationListener(null);
            view.dispose();
            onLogout.run();
        });
    }

    private Court selectedCourt() {
        int index = view.getSelectedCourtIndex();
        return index >= 0 && index < courts.size() ? courts.get(index) : null;
    }
}
