package co.edu.uptc.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ReservationSystem implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int OPENING_HOUR = 6;
    private static final int LAST_START_HOUR = 21;
    private static final int MAX_DAYS_AHEAD = 30;

    private final Administrator administrator;
    private final List<Client> clients;
    private final List<Court> courts;
    private final List<Reservation> reservations;
    private final Queue<WaitlistRequest> waitlist;
    private int nextCourtId;
    private int nextReservationId;
    private int nextRequestId;

    public ReservationSystem(Administrator administrator) {
        this.administrator = administrator;
        this.clients = new ArrayList<>();
        this.courts = new ArrayList<>();
        this.reservations = new ArrayList<>();
        this.waitlist = new LinkedList<>();
        this.nextCourtId = 1;
        this.nextReservationId = 1;
        this.nextRequestId = 1;
    }

    public synchronized User login(String username, String password) {
        if (administrator.validateCredentials(username, password)) {
            return administrator;
        }
        for (Client client : clients) {
            if (client.validateCredentials(username, password)) {
                return client;
            }
        }
        return null;
    }

    public synchronized void registerClient(Client client) {
        if (administrator.getUsername().equals(client.getUsername())) {
            throw new IllegalStateException("USERNAME_TAKEN");
        }
        for (Client existing : clients) {
            if (existing.getUsername().equals(client.getUsername())) {
                throw new IllegalStateException("USERNAME_TAKEN");
            }
            if (existing.getDocumentId().equals(client.getDocumentId())) {
                throw new IllegalStateException("DOCUMENT_TAKEN");
            }
        }
        clients.add(client);
    }

    public synchronized Court addCourt(String name, String sport, int capacity, double hourlyRate) {
        Court court = new Court(nextCourtId++, name, sport, capacity, hourlyRate);
        courts.add(court);
        return court;
    }

    public synchronized void editCourt(int courtId, String name, String sport,
                                       int capacity, double hourlyRate) {
        Court court = findCourt(courtId);
        court.setName(name);
        court.setSport(sport);
        court.setCapacity(capacity);
        court.setHourlyRate(hourlyRate);
    }

    public synchronized void deactivateCourt(int courtId) {
        Court court = findCourt(courtId);
        LocalDateTime now = LocalDateTime.now();
        for (Reservation reservation : reservations) {
            if (reservation.getTimeSlot().getCourt().equals(court) && reservation.isActive(now)) {
                throw new IllegalStateException("COURT_HAS_ACTIVE_RESERVATIONS");
            }
        }
        court.setActive(false);
    }

    public synchronized List<Court> getCourts() {
        return new ArrayList<>(courts);
    }

    public synchronized List<Court> getActiveCourts() {
        List<Court> result = new ArrayList<>();
        for (Court court : courts) {
            if (court.isActive()) {
                result.add(court);
            }
        }
        return result;
    }

    public synchronized List<TimeSlot> getOccupiedSlots(Court court, LocalDate date) {
        List<TimeSlot> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (Reservation reservation : reservations) {
            TimeSlot slot = reservation.getTimeSlot();
            if (reservation.isActive(now) && slot.getCourt().equals(court)
                    && slot.getDate().equals(date)) {
                result.add(slot);
            }
        }
        return result;
    }

    public synchronized Reservation createReservation(TimeSlot timeSlot, Client client) {
        validateFutureSlot(timeSlot);
        if (!timeSlot.getCourt().isActive()) {
            throw new IllegalStateException("COURT_INACTIVE");
        }
        if (findActiveReservation(timeSlot) != null) {
            throw new IllegalStateException("SLOT_OCCUPIED");
        }
        Reservation reservation = new Reservation(nextReservationId++, timeSlot, client);
        reservations.add(reservation);
        return reservation;
    }

    public synchronized Reservation cancelReservation(int reservationId, Client client) {
        LocalDateTime now = LocalDateTime.now();
        Reservation reservation = null;
        for (Reservation candidate : reservations) {
            if (candidate.getId() == reservationId
                    && candidate.getClient().getUsername().equals(client.getUsername())) {
                reservation = candidate;
            }
        }
        if (reservation == null) {
            throw new IllegalArgumentException("RESERVATION_NOT_FOUND");
        }
        if (!reservation.canCancel(now)) {
            throw new IllegalStateException("CANNOT_CANCEL");
        }
        reservation.cancel();
        return assignFreedSlot(reservation.getTimeSlot());
    }

    public synchronized List<Reservation> getReservationsByClient(Client client) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation reservation : reservations) {
            if (reservation.getClient().getUsername().equals(client.getUsername())) {
                result.add(reservation);
            }
        }
        return result;
    }

    public synchronized WaitlistRequest joinWaitlist(TimeSlot timeSlot, Client client) {
        validateFutureSlot(timeSlot);
        Reservation occupant = findActiveReservation(timeSlot);
        if (occupant == null) {
            throw new IllegalStateException("SLOT_AVAILABLE");
        }
        if (occupant.getClient().getUsername().equals(client.getUsername())) {
            throw new IllegalStateException("OWN_RESERVATION");
        }
        for (WaitlistRequest request : waitlist) {
            if (request.getTimeSlot().equals(timeSlot)
                    && request.getClient().getUsername().equals(client.getUsername())) {
                throw new IllegalStateException("ALREADY_WAITING");
            }
        }
        WaitlistRequest request = new WaitlistRequest(nextRequestId++, timeSlot, client);
        waitlist.add(request);
        return request;
    }

    public synchronized List<WaitlistRequest> getWaitlistByClient(Client client) {
        discardExpiredRequests(LocalDateTime.now());
        List<WaitlistRequest> result = new ArrayList<>();
        for (WaitlistRequest request : waitlist) {
            if (request.getClient().getUsername().equals(client.getUsername())) {
                result.add(request);
            }
        }
        return result;
    }

    private Reservation assignFreedSlot(TimeSlot timeSlot) {
        discardExpiredRequests(LocalDateTime.now());
        Iterator<WaitlistRequest> iterator = waitlist.iterator();
        while (iterator.hasNext()) {
            WaitlistRequest request = iterator.next();
            if (request.getTimeSlot().equals(timeSlot)) {
                iterator.remove();
                Reservation reservation =
                        new Reservation(nextReservationId++, timeSlot, request.getClient());
                reservations.add(reservation);
                return reservation;
            }
        }
        return null;
    }

    private void discardExpiredRequests(LocalDateTime now) {
        waitlist.removeIf(request -> request.isExpired(now));
    }

    private void validateFutureSlot(TimeSlot timeSlot) {
        LocalDateTime now = LocalDateTime.now();
        if (timeSlot.getStartHour() < OPENING_HOUR || timeSlot.getStartHour() > LAST_START_HOUR) {
            throw new IllegalArgumentException("INVALID_HOUR");
        }
        if (!timeSlot.getStartDateTime().isAfter(now)) {
            throw new IllegalStateException("SLOT_IN_PAST");
        }
        if (timeSlot.getDate().isAfter(now.toLocalDate().plusDays(MAX_DAYS_AHEAD))) {
            throw new IllegalStateException("SLOT_TOO_FAR");
        }
    }

    private Reservation findActiveReservation(TimeSlot timeSlot) {
        LocalDateTime now = LocalDateTime.now();
        for (Reservation reservation : reservations) {
            if (reservation.isActive(now) && reservation.getTimeSlot().equals(timeSlot)) {
                return reservation;
            }
        }
        return null;
    }

    private Court findCourt(int courtId) {
        for (Court court : courts) {
            if (court.getId() == courtId) {
                return court;
            }
        }
        throw new IllegalArgumentException("COURT_NOT_FOUND");
    }
}
