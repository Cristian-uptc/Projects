package co.edu.uptc.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class WaitlistRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int id;
    private final TimeSlot timeSlot;
    private final Client client;
    private final LocalDateTime requestDateTime;

    public WaitlistRequest(int id, TimeSlot timeSlot, Client client) {
        this.id = id;
        this.timeSlot = timeSlot;
        this.client = client;
        this.requestDateTime = LocalDateTime.now();
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

    public LocalDateTime getRequestDateTime() {
        return requestDateTime;
    }

    public boolean isExpired(LocalDateTime now) {
        return !timeSlot.getStartDateTime().isAfter(now);
    }
}
