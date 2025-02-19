package com.example.cinamacentermanagement.controllers;

import com.example.cinamacentermanagement.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * Controller class for updating movie schedules.
 */
public class UpdateScheduleController {

    @FXML
    private ListView<String> scheduleListView;
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
    private Button updateButton;

    private Connection connection;
    private ObservableList<String> scheduleList = FXCollections.observableArrayList();
    private String selectedMovie, selectedHall, selectedDate;

    private PreparedStatement loadMoviesStatement;
    private PreparedStatement loadHallsStatement;
    private PreparedStatement updateScheduleStatement;
    private PreparedStatement loadSchedulesStatement;

    /**
     * Sets the database connection and prepares SQL statements.
     *
     * @param connection the database connection
     * @throws SQLException if a database access error occurs
     */
    public void setConnection(Connection connection) throws SQLException {
        this.connection = connection;
        prepareStatements();
        loadSchedules();
        loadMovies();
        loadHalls();
        loadDateTimeOptions();
    }

    /**
     * Prepares the SQL statements for loading schedules, movies, and halls.
     *
     * @throws SQLException if a database access error occurs
     */
    private void prepareStatements() throws SQLException {
        connection = DatabaseConnection.getConnection();
        if (connection != null) {
            loadSchedulesStatement = connection.prepareStatement(
                    "SELECT Movies.title, Sessions.movie_id, Halls.name, Sessions.hall_id, Sessions.session_time " +
                            "FROM Sessions " +
                            "JOIN Movies ON Sessions.movie_id = Movies.movie_id " +
                            "JOIN Halls ON Sessions.hall_id = Halls.hall_id " +
                            "ORDER BY Sessions.movie_id"
            );

            loadMoviesStatement = connection.prepareStatement("SELECT title FROM Movies");
            loadHallsStatement = connection.prepareStatement("SELECT name FROM Halls");
        }
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    private void initialize() {
        updateButton.setOnAction(event -> {
            try {
                updateSchedule();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        scheduleListView.setOnMouseClicked(this::handleScheduleSelection);

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
     * Loads the list of schedules from the database and populates the schedule list view.
     */
    private void loadSchedules() {
        scheduleList.clear();
        try (ResultSet resultSet = loadSchedulesStatement.executeQuery()) {
            while (resultSet.next()) {
                String schedule = String.format("%s - %s - %s",
                        resultSet.getString("title"),
                        resultSet.getString("name"),
                        resultSet.getString("session_time"));
                scheduleList.add(schedule);
            }
            scheduleListView.setItems(scheduleList);
        } catch (SQLException e) {
            showAlert("Database Error", "Error loading schedules: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Loads the list of movies from the database and populates the movie combo box.
     */
    private void loadMovies() {
        movieComboBox.getItems().clear();
        ObservableList<String> movies = FXCollections.observableArrayList();
        try (ResultSet resultSet = loadMoviesStatement.executeQuery()) {
            while (resultSet.next()) {
                movies.add(resultSet.getString("title"));
            }
            movieComboBox.setItems(movies);
        } catch (SQLException e) {
            showAlert("Database Error", "Error loading movies: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Loads the list of halls from the database and populates the hall combo box.
     */
    private void loadHalls() {
        hallComboBox.getItems().clear();
        ObservableList<String> halls = FXCollections.observableArrayList();
        try (ResultSet resultSet = loadHallsStatement.executeQuery()) {
            while (resultSet.next()) {
                halls.add(resultSet.getString("name"));
            }
            hallComboBox.setItems(halls);
        } catch (SQLException e) {
            showAlert("Database Error", "Error loading halls: " + e.getMessage(), Alert.AlertType.ERROR);
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
        updateDaysComboBox(2025, 1); // Load default days for January

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
     * Handles the selection of a schedule from the list view.
     *
     * @param event the mouse event
     */
    @FXML
    private void handleScheduleSelection(MouseEvent event) {
        String selectedSchedule = scheduleListView.getSelectionModel().getSelectedItem();
        if (selectedSchedule != null) {
            String[] parts = selectedSchedule.split(" - ");
            if (parts.length == 3) {
                selectedMovie = parts[0];
                selectedHall = parts[1];
                selectedDate = parts[2];
                movieComboBox.setValue(selectedMovie);
                hallComboBox.setValue(selectedHall);
                LocalDateTime dateTime = LocalDateTime.parse(selectedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                yearComboBox.setValue(dateTime.getYear());
                monthComboBox.setValue(String.format("%02d", dateTime.getMonthValue()));
                dayComboBox.setValue(String.format("%02d", dateTime.getDayOfMonth()));
                hourComboBox.setValue(String.format("%02d", dateTime.getHour()));
                minuteComboBox.setValue(String.format("%02d", dateTime.getMinute()));
                secondComboBox.setValue(String.format("%02d", dateTime.getSecond()));
            }
        }
    }

    /**
     * Updates the selected schedule in the database.
     *
     * @throws SQLException if a database access error occurs
     */
    private void updateSchedule() throws SQLException {
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

        LocalDateTime sessionTime = LocalDateTime.of(year, Integer.parseInt(month), Integer.parseInt(day), Integer.parseInt(hour), Integer.parseInt(minute), Integer.parseInt(second));
        String formattedSessionTime = sessionTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String updateSQL = "UPDATE Sessions " +
                "SET movie_id = (SELECT movie_id FROM Movies WHERE title = ?), " +
                "    hall_id = (SELECT hall_id FROM Halls WHERE name = ?), " +
                "    session_time = ? " +
                "WHERE movie_id = (SELECT movie_id FROM Movies WHERE title = ?) " +
                "AND hall_id = (SELECT hall_id FROM Halls WHERE name = ?) " +
                "AND session_time = ?";

        try (PreparedStatement updateScheduleStatement = connection.prepareStatement(updateSQL)) {
            updateScheduleStatement.setString(1, movie);
            updateScheduleStatement.setString(2, hall);
            updateScheduleStatement.setString(3, formattedSessionTime);
            updateScheduleStatement.setString(4, selectedMovie);
            updateScheduleStatement.setString(5, selectedHall);
            updateScheduleStatement.setString(6, selectedDate);

            int rowsAffected = updateScheduleStatement.executeUpdate();

            if (rowsAffected > 0) {
                showAlert("Success", "Schedule updated successfully.", Alert.AlertType.INFORMATION);
                loadSchedules();
            } else {
                showAlert("Error", "No matching schedule found to update.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error updating schedule: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
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