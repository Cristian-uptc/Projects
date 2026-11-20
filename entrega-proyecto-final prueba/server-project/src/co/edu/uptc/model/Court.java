package co.edu.uptc.model;

import java.io.Serializable;

public class Court implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int id;
    private String name;
    private String sport;
    private int capacity;
    private double hourlyRate;
    private boolean active;

    public Court(int id, String name, String sport, int capacity, double hourlyRate) {
        this.id = id;
        this.name = name;
        this.sport = sport;
        this.capacity = capacity;
        this.hourlyRate = hourlyRate;
        this.active = true;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSport() {
        return sport;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public boolean isActive() {
        return active;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Court)) {
            return false;
        }
        return id == ((Court) o).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
