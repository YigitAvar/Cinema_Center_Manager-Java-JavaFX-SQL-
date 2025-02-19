package com.example.cinamacentermanagement.model;

public class TicketDetails {
    private final String movieName;
    private final String hallName; // Added hall name property
    private final String seatInfo;
    private final double price;

    public TicketDetails(String movieName, String hallName, String seatInfo, double price) {
        this.movieName = movieName;
        this.hallName = hallName;
        this.seatInfo = seatInfo;
        this.price = price;
    }

    public String getMovieName() {
        return movieName;
    }

    public String getHallName() {
        return hallName;
    }

    public String getSeatInfo() {
        return seatInfo;
    }

    public double getPrice() {
        return price;
    }
}

