package com.example.cinamacentermanagement.controllers;

import com.example.cinamacentermanagement.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * Controller class for creating movie schedules.
 */
public class CreateScheduleController {

    @FXML
    private ComboBox<String> movieComboBox;

    @FXML
    private ComboBox<String> hallComboBox;

    @FXML
    private ComboBox<Integer> yearComboBox;

    @FXML
    private ComboBox<String> monthComboBox;

    @FXML
    private ComboBox<String> dayComboBox;

    @FXML
    private ComboBox<String> hourComboBox;

    @FXML
    private ComboBox<String> minuteComboBox;

    @FXML
    private ComboBox<String> secondComboBox;

    @FXML
    private Button addScheduleButton;

    private Connection connection;

    /**
     * Sets the database connection and loads initial data.
     *
     * @param connection the database connection
     * @throws SQLException if a database access error occurs
     */
    public void setConnection(Connection connection) throws SQLException {
        this.connection = connection;
        loadMovies();
        loadHalls();
        loadDateTimeOptions();
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    private void initialize() {
        addScheduleButton.setOnAction(event -> {
            try {
                addSchedule();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        monthComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                int year = yearComboBox.getValue() != null ? yearComboBox.getValue() : 2025;
                updateDaysComboBox(year, Integer.parseInt(newValue));
            }
        });

        yearComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                int month = monthComboBox.getValue() != null ? Integer.parseInt(monthComboBox.getValue()) : 1;
                updateDaysComboBox(newValue, month);
            }
        });
    }

    /**
     * Loads the list of movies from the database and populates the movie combo box.
     *
     * @throws SQLException if a database access error occurs
     */
    private void loadMovies() throws SQLException {
        connection = DatabaseConnection.getConnection();
        if (connection == null) {
            showAlert("Database Error", "No database connection.", Alert.AlertType.ERROR);
            return;
        }

        ObservableList<String> movies = FXCollections.observableArrayList();
        String query = "SELECT title FROM Movies";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                movies.add(resultSet.getString("title"));
            }
            movieComboBox.setItems(movies);
        } catch (SQLException e) {
            showAlert("Database Error", "Error loading movies: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Loads the list of halls from the database and populates the hall combo box.
     *
     * @throws SQLException if a database access error occurs
     */
    private void loadHalls() throws SQLException {
        connection = DatabaseConnection.getConnection();
        if (connection == null) {
            showAlert("Database Error", "No database connection.", Alert.AlertType.ERROR);
            return;
        }

        ObservableList<String> halls = FXCollections.observableArrayList();
        String query = "SELECT name FROM Halls";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                halls.add(resultSet.getString("name"));
            }
            hallComboBox.setItems(halls);
        } catch (SQLException e) {
            showAlert("Database Error", "Error loading halls: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Loads the date and time options for the schedule.
     */
    private void loadDateTimeOptions() {
        yearComboBox.setItems(FXCollections.observableArrayList(2025, 2026, 2027));
        monthComboBox.setItems(FXCollections.observableArrayList(
                FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12).stream()
                        .map(i -> String.format("%02d", i)).collect(Collectors.toList())
        ));
        updateDaysComboBox(2025, 1); // Load days for January by default

        hourComboBox.setItems(FXCollections.observableArrayList(
                FXCollections.observableArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23).stream()
                        .map(i -> String.format("%02d", i)).collect(Collectors.toList())
        ));
        minuteComboBox.setItems(FXCollections.observableArrayList(
                FXCollections.observableArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59).stream()
                        .map(i -> String.format("%02d", i)).collect(Collectors.toList())
        ));
        secondComboBox.setItems(FXCollections.observableArrayList(
                FXCollections.observableArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59).stream()
                        .map(i -> String.format("%02d", i)).collect(Collectors.toList())
        ));
    }

    /**
     * Updates the days combo box based on the selected year and month.
     *
     * @param year the selected year
     * @param month the selected month
     */
    private void updateDaysComboBox(int year, int month) {
        int maxDays;
        switch (month) {
            case 2:
                maxDays = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) ? 29 : 28;
                break;
            case 4: case 6: case 9: case 11:
                maxDays = 30;
                break;
            default:
                maxDays = 31;
                break;
        }
        dayComboBox.setItems(FXCollections.observableArrayList(
                FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31).stream()
                        .filter(i -> i <= maxDays)
                        .map(i -> String.format("%02d", i)).collect(Collectors.toList())
        ));
    }

    /**
     * Adds a new schedule to the database.
     *
     * @throws SQLException if a database access error occurs
     */
    private void addSchedule() throws SQLException {
        connection = DatabaseConnection.getConnection();
        if (connection == null) {
            showAlert("Database Error", "No database connection.", Alert.AlertType.ERROR);
            return;
        }

        String movie = movieComboBox.getValue();
        String hall = hallComboBox.getValue();
        Integer year = yearComboBox.getValue();
        String month = monthComboBox.getValue();
        String day = dayComboBox.getValue();
        String hour = hourComboBox.getValue();
        String minute = minuteComboBox.getValue();
        String second = secondComboBox.getValue();

        if (movie == null || hall == null || year == null || month == null || day == null || hour == null || minute == null || second == null) {
            showAlert("Validation Error", "All fields are required.", Alert.AlertType.ERROR);
            return;
        }

        int movieId = getIdFromName("Movies", "title", movie);
        int hallId = getIdFromName("Halls", "name", hall);
        int availableSeats = getAvailableSeats(hallId);

        LocalDateTime sessionTime = LocalDateTime.of(year, Integer.parseInt(month), Integer.parseInt(day), Integer.parseInt(hour), Integer.parseInt(minute), Integer.parseInt(second));
        String formattedSessionTime = sessionTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String query = "INSERT INTO Sessions (movie_id, hall_id, session_time, available_seats) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, movieId);
            statement.setInt(2, hallId);
            statement.setString(3, formattedSessionTime);
            statement.setInt(4, availableSeats);
            statement.executeUpdate();
            showAlert("Success", "Schedule added successfully.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Database Error", "Error adding schedule: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the ID of a record from the database based on the table name, column name, and value.
     *
     * @param tableName the name of the table
     * @param columnName the name of the column
     * @param value the value to search for
     * @return the ID of the record, or -1 if not found
     */
    private int getIdFromName(String tableName, String columnName, String value) {
        String query = "SELECT " + tableName.substring(0, tableName.length() - 1) + "_id FROM " + tableName + " WHERE LOWER(" + columnName + ") = LOWER(?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, value.trim());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                } else {
                    showAlert("Database Error", "No matching record found for " + value, Alert.AlertType.ERROR);
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error fetching ID: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Retrieves the available seats for a hall from the database.
     *
     * @param hallId the ID of the hall
     * @return the number of available seats, or -1 if not found
     */
    private int getAvailableSeats(int hallId) {
        String query = "SELECT capacity FROM Halls WHERE hall_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, hallId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("capacity");
                } else {
                    showAlert("Database Error", "No matching record found for hall ID: " + hallId, Alert.AlertType.ERROR);
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error fetching available seats: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
        return -1;
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