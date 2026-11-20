package co.edu.uptc.client.presenter;

import co.edu.uptc.client.Messages;
import co.edu.uptc.client.communication.ServerConnection;
import co.edu.uptc.client.view.AdminView;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import co.edu.uptc.model.Court;
import co.edu.uptc.network.Action;
import co.edu.uptc.network.Response;

public class AdminPresenter extends BasePresenter<AdminView> {

    private static final int EDIT_PARAM_COUNT = 5;

    private final Runnable onLogout;
    private List<Court> courts = new ArrayList<>();

    public AdminPresenter(AdminView view, ServerConnection connection, Runnable onLogout) {
        super(view, connection);
        this.onLogout = onLogout;
        view.setSelectionListener(this::fillFormWithSelection);
        view.setAddAction(event -> addCourt());
        view.setEditAction(event -> editCourt());
        view.setDeactivateAction(event -> deactivateCourt());
        view.setLogoutAction(event -> logout());
        loadCourts();
    }

    private void loadCourts() {
        sendAsync(Action.GET_COURTS, this::showCourts);
    }

    private void showCourts(Response response) {
        courts = response.getData();
        List<Object[]> rows = new ArrayList<>();
        for (Court court : courts) {
            rows.add(new Object[]{court.getId(), court.getName(), court.getSport(),
                court.getCapacity(), court.getHourlyRate(),
                court.isActive() ? "Activa" : "Inactiva"});
        }
        view.setCourtRows(rows);
    }

    private void fillFormWithSelection() {
        Court court = selectedCourt();
        if (court != null) {
            view.setForm(court.getName(), court.getSport(),
                    String.valueOf(court.getCapacity()), String.valueOf(court.getHourlyRate()));
        }
    }

    private void addCourt() {
        Serializable[] params = readForm();
        if (params != null) {
            sendAsync(Action.ADD_COURT, response -> {
                view.setForm("", "", "", "");
                loadCourts();
            }, params);
        }
    }

    private void editCourt() {
        Court court = requireSelectedCourt();
        Serializable[] form = readForm();
        if (court == null || form == null) {
            return;
        }
        Serializable[] params = Arrays.copyOf(form, EDIT_PARAM_COUNT);
        params[EDIT_PARAM_COUNT - 1] = court.getId();
        sendAsync(Action.EDIT_COURT, response -> loadCourts(), params);
    }

    private void deactivateCourt() {
        Court court = requireSelectedCourt();
        if (court != null && view.confirm("¿Desactivar la cancha " + court.getName() + "?")) {
            sendAsync(Action.DEACTIVATE_COURT, response -> loadCourts(), court.getId());
        }
    }

    private void logout() {
        sendAsync(Action.LOGOUT, response -> {
            view.dispose();
            onLogout.run();
        });
    }

    private Serializable[] readForm() {
        if (view.getCourtName().isEmpty() || view.getSport().isEmpty()) {
            view.showError(Messages.error("EMPTY_FIELDS"));
            return null;
        }
        try {
            int capacity = Integer.parseInt(view.getCapacityText());
            double rate = Double.parseDouble(view.getRateText());
            if (capacity <= 0 || rate <= 0) {
                throw new NumberFormatException();
            }
            return new Serializable[]{view.getCourtName(), view.getSport(), capacity, rate};
        } catch (NumberFormatException e) {
            view.showError(Messages.error("INVALID_NUMBER"));
            return null;
        }
    }

    private Court selectedCourt() {
        int row = view.getSelectedRow();
        return row >= 0 && row < courts.size() ? courts.get(row) : null;
    }

    private Court requireSelectedCourt() {
        Court court = selectedCourt();
        if (court == null) {
            view.showError(Messages.error("NO_SELECTION"));
        }
        return court;
    }
}
