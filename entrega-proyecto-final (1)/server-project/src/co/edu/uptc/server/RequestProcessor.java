package co.edu.uptc.server;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import co.edu.uptc.model.Client;
import co.edu.uptc.model.Court;
import co.edu.uptc.model.Reservation;
import co.edu.uptc.model.ReservationSystem;
import co.edu.uptc.model.TimeSlot;
import co.edu.uptc.model.User;
import co.edu.uptc.network.Action;
import co.edu.uptc.network.Request;
import co.edu.uptc.network.Response;
import co.edu.uptc.persistence.DataStore;

public class RequestProcessor {

    private static final Logger LOGGER = Logger.getLogger(RequestProcessor.class.getName());

    private final ReservationSystem system;
    private final DataStore dataStore;
    private final OnlineClients onlineClients;

    public RequestProcessor(ReservationSystem system, DataStore dataStore,
                            OnlineClients onlineClients) {
        this.system = system;
        this.dataStore = dataStore;
        this.onlineClients = onlineClients;
    }

    public Response process(Request request, Session session) {
        try {
            Response response;
            synchronized (system) {
                response = dispatch(request, session);
                if (request.getAction().isMutating()) {
                    dataStore.save(system);
                }
            }
            return afterProcessing(request, response);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return Response.error(e.getMessage());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Unexpected error processing " + request.getAction(), e);
            return Response.error("INTERNAL_ERROR");
        }
    }

    private Response afterProcessing(Request request, Response response) {
        if (request.getAction() != Action.CANCEL_RESERVATION) {
            return response;
        }
        Reservation assigned = response.getData();
        if (assigned != null) {
            onlineClients.notifyClient(assigned.getClient().getUsername(), assigned);
        }
        return Response.ok(null);
    }

    private Response dispatch(Request request, Session session) {
        switch (request.getAction()) {
            case LOGIN:
                return login(request, session);
            case REGISTER:
                return register(request);
            case LOGOUT:
                session.setUser(null);
                return Response.ok(null);
            case GET_ACTIVE_COURTS:
                return Response.ok(new ArrayList<>(system.getActiveCourts()));
            case GET_OCCUPIED_SLOTS:
                return occupiedSlots(request);
            case GET_COURTS:
            case ADD_COURT:
            case EDIT_COURT:
            case DEACTIVATE_COURT:
                requireAdministrator(session);
                return dispatchAdministrator(request);
            default:
                return dispatchClient(request, requireClient(session));
        }
    }

    private Response dispatchAdministrator(Request request) {
        switch (request.getAction()) {
            case GET_COURTS:
                return Response.ok(new ArrayList<>(system.getCourts()));
            case ADD_COURT:
                system.addCourt(request.getParam(0), request.getParam(1),
                        request.<Integer>getParam(2), request.<Double>getParam(3));
                return Response.ok(null);
            case EDIT_COURT:
                system.editCourt(request.<Integer>getParam(4), request.getParam(0),
                        request.getParam(1), request.<Integer>getParam(2),
                        request.<Double>getParam(3));
                return Response.ok(null);
            default:
                system.deactivateCourt(request.<Integer>getParam(0));
                return Response.ok(null);
        }
    }

    private Response dispatchClient(Request request, Client client) {
        switch (request.getAction()) {
            case CREATE_RESERVATION:
                system.createReservation(readTimeSlot(request), client);
                return Response.ok(null);
            case CANCEL_RESERVATION:
                return Response.ok(system.cancelReservation(request.<Integer>getParam(0), client));
            case GET_MY_RESERVATIONS:
                return Response.ok(new ArrayList<>(system.getReservationsByClient(client)));
            case JOIN_WAITLIST:
                system.joinWaitlist(readTimeSlot(request), client);
                return Response.ok(null);
            case GET_MY_WAITLIST:
                return Response.ok(new ArrayList<>(system.getWaitlistByClient(client)));
            default:
                throw new IllegalArgumentException("UNKNOWN_ACTION");
        }
    }

    private Response login(Request request, Session session) {
        User user = system.login(request.getParam(0), request.getParam(1));
        if (user == null) {
            return Response.error("INVALID_CREDENTIALS");
        }
        session.setUser(user);
        return Response.ok(user);
    }

    private Response register(Request request) {
        system.registerClient(new Client(request.getParam(0), request.getParam(1),
                request.getParam(2), request.getParam(3), request.getParam(4)));
        return Response.ok(null);
    }

    private Response occupiedSlots(Request request) {
        int courtId = request.getParam(0);
        LocalDate date = request.getParam(1);
        return Response.ok(new ArrayList<>(system.getOccupiedSlots(findCourt(courtId), date)));
    }

    private TimeSlot readTimeSlot(Request request) {
        int courtId = request.getParam(0);
        LocalDate date = request.getParam(1);
        int hour = request.getParam(2);
        return new TimeSlot(findCourt(courtId), date, hour);
    }

    private Court findCourt(int courtId) {
        for (Court court : system.getCourts()) {
            if (court.getId() == courtId) {
                return court;
            }
        }
        throw new IllegalArgumentException("COURT_NOT_FOUND");
    }

    private void requireAdministrator(Session session) {
        if (session.getUser() == null || session.getUser() instanceof Client) {
            throw new IllegalStateException("NOT_AUTHORIZED");
        }
    }

    private Client requireClient(Session session) {
        if (!(session.getUser() instanceof Client)) {
            throw new IllegalStateException("NOT_AUTHORIZED");
        }
        return (Client) session.getUser();
    }
}
