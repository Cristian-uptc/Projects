package co.edu.uptc.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class TimeSlot implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Court court;
    private final LocalDate date;
    private final int startHour;

    public TimeSlot(Court court, LocalDate date, int startHour) {
        this.court = court;
        this.date = date;
        this.startHour = startHour;
    }

    public Court getCourt() {
        return court;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getStartHour() {
        return startHour;
    }

    public LocalDateTime getStartDateTime() {
        return date.atTime(startHour, 0);
    }

    public LocalDateTime getEndDateTime() {
        return getStartDateTime().plusHours(1);
    }

    public boolean hasPassed(LocalDateTime now) {
        return !getEndDateTime().isAfter(now);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimeSlot)) {
            return false;
        }
        TimeSlot other = (TimeSlot) o;
        return startHour == other.startHour
                && court.equals(other.court)
                && date.equals(other.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(court, date, startHour);
    }
}
