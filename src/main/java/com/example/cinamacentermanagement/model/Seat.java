package com.example.cinamacentermanagement.model;

public class Seat {
    private int seatId;
    private int hallId;
    private int rowNumber;
    private int seatNumber;
    private boolean isOccupied;

    // Constructor
    public Seat(int seatId, int hallId, int rowNumber, int seatNumber, boolean isOccupied) {
        this.seatId = seatId;
        this.hallId = hallId;
        this.rowNumber = rowNumber;
        this.seatNumber = seatNumber;
        this.isOccupied = isOccupied;
    }

    // Getters and Setters
    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public int getHallId() {
        return hallId;
    }

    public void setHallId(int hallId) {
        this.hallId = hallId;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean isOccupied) {
        this.isOccupied = isOccupied;
    }

    // toString Method (optional, useful for debugging)
    @Override
    public String toString() {
        return "Seat{" +
                "seatId=" + seatId +
                ", hallId=" + hallId +
                ", rowNumber=" + rowNumber +
                ", seatNumber=" + seatNumber +
                ", isOccupied=" + isOccupied +
                '}';
    }
}
