package co.edu.uptc.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int MIN_HOURS_TO_CANCEL = 2;

    private final int id;
    private final TimeSlot timeSlot;
    private final Client client;
    private ReservationStatus status;

    public Reservation(int id, TimeSlot timeSlot, Client client) {
        this.id = id;
        this.timeSlot = timeSlot;
        this.client = client;
        this.status = ReservationStatus.ACTIVE;
    }

    public int getId() {
        return id;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public Client getClient() {
        return client;
    }

    public ReservationStatus getStatus(LocalDateTime now) {
        if (status == ReservationStatus.ACTIVE && timeSlot.hasPassed(now)) {
            return ReservationStatus.FINISHED;
        }
        return status;
    }

    public boolean isActive(LocalDateTime now) {
        return getStatus(now) == ReservationStatus.ACTIVE;
    }

    public boolean canCancel(LocalDateTime now) {
        return isActive(now)
                && !now.isAfter(timeSlot.getStartDateTime().minusHours(MIN_HOURS_TO_CANCEL));
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }
}
