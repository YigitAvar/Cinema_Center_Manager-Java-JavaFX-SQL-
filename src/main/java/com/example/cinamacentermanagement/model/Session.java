package com.example.cinamacentermanagement.model;

public class Session {
    private int sessionId;
    private String sessionTime; // Use formatted date-time as String for UI
    private int availableSeats;
    private int hallId;
    private String hallName;
    private int movieId;
    private String movieTitle;

    public Session(int sessionId, String sessionTime, int availableSeats, int hallId, String hallName, int movieId, String movieTitle) {
        this.sessionId = sessionId;
        this.sessionTime = sessionTime;
        this.availableSeats = availableSeats;
        this.hallId = hallId;
        this.hallName = hallName;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
    }
    
    // Getters and Setters
    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(String sessionTime) {
        this.sessionTime = sessionTime;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public int getHallId() {
        return hallId;
    }
    

    public void setHallId(int hallId) {
        this.hallId = hallId;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    @Override
    public String toString() {
        return "Session{" +
               "sessionId=" + sessionId +
               ", sessionTime='" + sessionTime + '\'' +
               ", availableSeats=" + availableSeats +
               ", hallId=" + hallId +
               ", hallName='" + hallName + '\'' +
               ", movieId=" + movieId +
               ", movieTitle='" + movieTitle + '\'' +
               '}';
    }
}

