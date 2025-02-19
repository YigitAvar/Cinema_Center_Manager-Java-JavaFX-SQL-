package com.example.cinamacentermanagement.dao;
import com.example.cinamacentermanagement.model.Session;
import com.example.cinamacentermanagement.database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SessionDao {

    public List<String> getFormattedSessionsAfter(LocalDateTime dateTime) {
        List<String> formattedSessions = new ArrayList<>();
        String query = "SELECT s.session_time, h.name AS hall_name, m.title AS movie_title " +
                       "FROM Sessions s " +
                       "JOIN Halls h ON s.hall_id = h.hall_id " +
                       "JOIN Movies m ON s.movie_id = m.movie_id " +
                       "WHERE s.session_time > ? ORDER BY s.session_time ASC";
    
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
    
            statement.setTimestamp(1, Timestamp.valueOf(dateTime));
            ResultSet resultSet = statement.executeQuery();
    
            while (resultSet.next()) {
                String sessionDetails = String.format("Movie: %s | Time: %s | Hall: %s",
                        resultSet.getString("movie_title"),
                        resultSet.getString("session_time"),
                        resultSet.getString("hall_name"));
                formattedSessions.add(sessionDetails);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return formattedSessions;
    }
    
    
    public List<Session> getSessionsWithDetails() {
        List<Session> sessions = new ArrayList<>();
        String query = "SELECT s.session_id, s.session_time, s.available_seats, h.hall_id, h.name AS hall_name, m.movie_id, m.title AS movie_title " +
                "FROM Sessions s " +
                "JOIN Halls h ON s.hall_id = h.hall_id " +
                "JOIN Movies m ON s.movie_id = m.movie_id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Session session = new Session(
                        resultSet.getInt("session_id"),
                        resultSet.getString("session_time"),
                        resultSet.getInt("available_seats"),
                        resultSet.getInt("hall_id"),
                        resultSet.getString("hall_name"),
                        resultSet.getInt("movie_id"),
                        resultSet.getString("movie_title")
                );
                sessions.add(session);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sessions;
    }

    public List<Session> getSessionsByMovieId(int movieId) {
        List<Session> sessions = new ArrayList<>();
        String query = "SELECT s.session_id, s.session_time, s.available_seats, "
                + "h.hall_id, h.name AS hall_name, "
                + "m.movie_id, m.title AS movie_title "  // Ensure the alias matches your model
                + "FROM Sessions s "
                + "JOIN Halls h ON s.hall_id = h.hall_id "
                + "JOIN Movies m ON s.movie_id = m.movie_id "
                + "WHERE s.movie_id = ?";
    
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
    
            preparedStatement.setInt(1, movieId);
            ResultSet resultSet = preparedStatement.executeQuery();
    
            while (resultSet.next()) {
                int sessionId = resultSet.getInt("session_id");
                String sessionTime = resultSet.getString("session_time");
                int availableSeats = resultSet.getInt("available_seats");
                int hallId = resultSet.getInt("hall_id");
                String hallName = resultSet.getString("hall_name");
                String movieTitle = resultSet.getString("movie_title");  // This should be mapped correctly
    
                // Create Session object with correct movieTitle
                Session session = new Session(sessionId, sessionTime, availableSeats, hallId, hallName, movieId, movieTitle);
                sessions.add(session);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return sessions;
    }
    
    public boolean lockSeatForSession(int sessionId) {
        String query = "UPDATE Sessions SET available_seats = available_seats - 1 WHERE session_id = ? AND available_seats > 0";
        Connection connection = null;
    
        try {
            // Get a connection and set the isolation level
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false); // Start transaction
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
    
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, sessionId);
    
                // Execute update and check if a row was affected
                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    connection.commit(); // Commit transaction
                    return true; // Seat successfully locked
                } else {
                    connection.rollback(); // Rollback if no rows were updated
                    return false; // No seat available
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback(); // Rollback on error
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); // Restore auto-commit
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public double getTicketPriceBySessionId(int sessionId) {
        double price = 0.0; // Default price
    
        String query = """
            SELECT m.ticket_price
            FROM Sessions s
            INNER JOIN Movies m ON s.movie_id = m.movie_id
            WHERE s.session_id = ?
        """;
    
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
    
            statement.setInt(1, sessionId); // Set the session ID
            ResultSet resultSet = statement.executeQuery();
    
            if (resultSet.next()) {
                price = resultSet.getDouble("ticket_price"); // Fetch the ticket price
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return price; // Return the price
    }
    
    
    
}