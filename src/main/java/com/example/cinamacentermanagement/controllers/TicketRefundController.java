package com.example.cinamacentermanagement.controllers;

import com.example.cinamacentermanagement.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controller class for handling ticket refunds.
 */
public class TicketRefundController {

    @FXML
    private ComboBox<String> movieTitleComboBox;

    @FXML
    private ComboBox<String> hallNameComboBox;

    @FXML
    private ComboBox<String> sessionTimeComboBox;

    @FXML
    private Button refundButton;

    private Connection connection;

    /**
     * Sets the database connection.
     *
     * @param connection the database connection
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    private void initialize() {
        refundButton.setOnAction(_ -> processRefund());
        loadMovieTitles();
        movieTitleComboBox.setOnAction(_ -> loadHallNames());
        hallNameComboBox.setOnAction(_ -> loadSessionTimes());
    }

    /**
     * Checks the database connection and reconnects if necessary.
     *
     * @throws SQLException if a database access error occurs
     */
    private void checkConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DatabaseConnection.getConnection();
        }
    }

    /**
     * Loads the movie titles from the database and populates the movie title combo box.
     */
    private void loadMovieTitles() {
        try {
            checkConnection();
            String query = "SELECT title FROM movies";
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movieTitleComboBox.getItems().add(rs.getString("title"));
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Unable to load movie titles.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Loads the hall names based on the selected movie title and populates the hall name combo box.
     */
    private void loadHallNames() {
        hallNameComboBox.getItems().clear();
        sessionTimeComboBox.getItems().clear();
        String movieTitle = movieTitleComboBox.getValue();
        if (movieTitle == null) {
            return;
        }

        try {
            checkConnection();

            // Get movie_id
            String getMovieQuery = "SELECT movie_id FROM movies WHERE title = ?";
            int movieId;
            try (PreparedStatement getMovieStmt = connection.prepareStatement(getMovieQuery)) {
                getMovieStmt.setString(1, movieTitle);
                try (ResultSet rs = getMovieStmt.executeQuery()) {
                    if (rs.next()) {
                        movieId = rs.getInt("movie_id");
                    } else {
                        return;
                    }
                }
            }

            // Get hall names
            String getHallQuery = "SELECT DISTINCT h.name FROM halls h JOIN sessions s ON h.hall_id = s.hall_id WHERE s.movie_id = ?";
            try (PreparedStatement getHallStmt = connection.prepareStatement(getHallQuery)) {
                getHallStmt.setInt(1, movieId);
                try (ResultSet rs = getHallStmt.executeQuery()) {
                    while (rs.next()) {
                        hallNameComboBox.getItems().add(rs.getString("name"));
                    }
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Unable to load hall names.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Loads the session times based on the selected movie title and hall name, and populates the session time combo box.
     */
    private void loadSessionTimes() {
        sessionTimeComboBox.getItems().clear();
        String movieTitle = movieTitleComboBox.getValue();
        String hallName = hallNameComboBox.getValue();
        if (movieTitle == null || hallName == null) {
            return;
        }

        try {
            checkConnection();

            // Get movie_id
            String getMovieQuery = "SELECT movie_id FROM movies WHERE title = ?";
            int movieId;
            try (PreparedStatement getMovieStmt = connection.prepareStatement(getMovieQuery)) {
                getMovieStmt.setString(1, movieTitle);
                try (ResultSet rs = getMovieStmt.executeQuery()) {
                    if (rs.next()) {
                        movieId = rs.getInt("movie_id");
                    } else {
                        return;
                    }
                }
            }

            // Get hall_id
            String getHallQuery = "SELECT hall_id FROM halls WHERE name = ?";
            int hallId;
            try (PreparedStatement getHallStmt = connection.prepareStatement(getHallQuery)) {
                getHallStmt.setString(1, hallName);
                try (ResultSet rs = getHallStmt.executeQuery()) {
                    if (rs.next()) {
                        hallId = rs.getInt("hall_id");
                    } else {
                        return;
                    }
                }
            }

            // Get session times
            String getSessionQuery = "SELECT session_time FROM sessions WHERE movie_id = ? AND hall_id = ?";
            try (PreparedStatement getSessionStmt = connection.prepareStatement(getSessionQuery)) {
                getSessionStmt.setInt(1, movieId);
                getSessionStmt.setInt(2, hallId);
                try (ResultSet rs = getSessionStmt.executeQuery()) {
                    while (rs.next()) {
                        sessionTimeComboBox.getItems().add(rs.getString("session_time"));
                    }
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Unable to load session times.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Processes the ticket refund by updating the available seats and total revenue in the database.
     */
    private void processRefund() {
        try {
            checkConnection();
        } catch (SQLException e) {
            showAlert("Database Error", "Unable to connect to the database.", Alert.AlertType.ERROR);
            e.printStackTrace();
            return;
        }

        String movieTitle = movieTitleComboBox.getValue();
        String hallName = hallNameComboBox.getValue();
        String sessionTime = sessionTimeComboBox.getValue();
        if (movieTitle == null || hallName == null || sessionTime == null) {
            showAlert("Validation Error", "Movie title, hall name, and session time are required.", Alert.AlertType.ERROR);
            return;
        }

        try {
            connection.setAutoCommit(false);

            // Get movie_id and ticket_price
            String getMovieQuery = "SELECT movie_id, ticket_price FROM movies WHERE title = ?";
            int movieId;
            double ticketPrice;
            try (PreparedStatement getMovieStmt = connection.prepareStatement(getMovieQuery)) {
                getMovieStmt.setString(1, movieTitle);
                try (ResultSet rs = getMovieStmt.executeQuery()) {
                    if (rs.next()) {
                        movieId = rs.getInt("movie_id");
                        ticketPrice = rs.getDouble("ticket_price");
                    } else {
                        showAlert("Error", "Movie not found.", Alert.AlertType.ERROR);
                        connection.rollback();
                        return;
                    }
                }
            }

            // Get hall_id
            String getHallQuery = "SELECT hall_id FROM halls WHERE name = ?";
            int hallId;
            try (PreparedStatement getHallStmt = connection.prepareStatement(getHallQuery)) {
                getHallStmt.setString(1, hallName);
                try (ResultSet rs = getHallStmt.executeQuery()) {
                    if (rs.next()) {
                        hallId = rs.getInt("hall_id");
                    } else {
                        showAlert("Error", "Hall not found.", Alert.AlertType.ERROR);
                        connection.rollback();
                        return;
                    }
                }
            }

            // Update available_seats
            String updateSeatsQuery = "UPDATE sessions SET available_seats = available_seats + 1 WHERE movie_id = ? AND hall_id = ? AND session_time = ?";
            try (PreparedStatement updateSeatsStmt = connection.prepareStatement(updateSeatsQuery)) {
                updateSeatsStmt.setInt(1, movieId);
                updateSeatsStmt.setInt(2, hallId);
                updateSeatsStmt.setString(3, sessionTime);
                updateSeatsStmt.executeUpdate();
            }

            // Update total_revenue
            String updateRevenueQuery = "UPDATE revenue SET total_revenue = total_revenue - ? WHERE revenue_id = ?";
            try (PreparedStatement updateRevenueStmt = connection.prepareStatement(updateRevenueQuery)) {
                updateRevenueStmt.setDouble(1, ticketPrice);
                updateRevenueStmt.setInt(2, 1); // Assuming revenue_id is 1, adjust as needed
                updateRevenueStmt.executeUpdate();
            }

            connection.commit();
            showAlert("Success", "Ticket refunded successfully. Available seats and revenue updated.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            showAlert("Database Error", "Error processing refund: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Displays an alert with the specified title, message, and alert type.
     *
     * @param title the title of the alert
     * @param message the message to be displayed in the alert
     * @param alertType the type of the alert
     */
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}