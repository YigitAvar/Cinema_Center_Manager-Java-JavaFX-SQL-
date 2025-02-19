package com.example.cinamacentermanagement.controllers;

import com.example.cinamacentermanagement.dao.MovieDao;
import com.example.cinamacentermanagement.dao.SessionDao;
import com.example.cinamacentermanagement.model.Movie;
import com.example.cinamacentermanagement.model.Session;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

import javax.print.DocFlavor.URL;

/**
 * Controller class for the second step of the cashier process.
 */
public class CashierStep2Controller {

    @FXML
    private ComboBox<Movie> movieComboBox;
    @FXML
    private TableView<Session> sessionsTable;
    @FXML
    private TableColumn<Session, String> dayColumn;
    @FXML
    private TableColumn<Session, String> sessionTimeColumn;
    @FXML
    private TableColumn<Session, String> hallColumn;
    @FXML
    private TableColumn<Session, Integer> vacantSeatsColumn;
    @FXML
    private Button selectButton;
    @FXML
    private Button backButton;

    private final MovieDao movieDao = new MovieDao();
    private final SessionDao sessionDao = new SessionDao();

    private Movie selectedMovie;
    private Session selectedSession;

    // Optional fields if you need them (e.g. from Step1 or user input)
    private int customerId = 123; // Example placeholder
    private int customerAge = 30; // Example placeholder

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        List<Movie> movies = movieDao.getAllMovies();
        ObservableList<Movie> movieList = FXCollections.observableArrayList(movies);
        movieComboBox.setItems(movieList);
        configureTableColumns();
        selectButton.setDisable(true);
    }

    /**
     * Configures the table columns for displaying session details.
     */
    private void configureTableColumns() {
        dayColumn.setCellValueFactory(cellData -> {
            String[] parts = safeSplitSessionTime(cellData.getValue().getSessionTime());
            return new ReadOnlyStringWrapper(parts[0]);
        });

        sessionTimeColumn.setCellValueFactory(cellData -> {
            String[] parts = safeSplitSessionTime(cellData.getValue().getSessionTime());
            return new ReadOnlyStringWrapper(parts[1]);
        });

        hallColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getHallName()));

        vacantSeatsColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getAvailableSeats()));
    }

    /**
     * Safely splits the session time string into day and time parts.
     *
     * @param sessionTime the session time string
     * @return an array containing the day and time parts
     */
    private String[] safeSplitSessionTime(String sessionTime) {
        if (sessionTime == null || !sessionTime.contains(" ")) {
            return new String[] { "Invalid", "Invalid" };
        }
        return sessionTime.split(" ");
    }

    /**
     * Handles the event when a movie is selected from the combo box.
     *
     * @param event the action event
     */
    @FXML
    private void onMovieSelected(ActionEvent event) {
        selectedMovie = movieComboBox.getValue();
        if (selectedMovie != null) {
            populateSessions(selectedMovie);
        }
    }

    /**
     * Populates the sessions table with sessions for the selected movie.
     *
     * @param movie the selected movie
     */
    private void populateSessions(Movie movie) {
        List<Session> sessions = sessionDao.getSessionsByMovieId(movie.getMovieId());
        ObservableList<Session> sessionList = FXCollections.observableArrayList(sessions);
        ObservableList<Session> filteredSessions = sessionList.filtered(s -> s.getAvailableSeats() > 0);

        if (filteredSessions.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Sessions", "No sessions are available for the selected movie.");
            selectButton.setDisable(true);
        } else {
            selectButton.setDisable(false);
        }
        sessionsTable.setItems(filteredSessions);
    }

    /**
     * Handles the event when a session is selected from the table.
     *
     * @param event the mouse event
     */
    @FXML
    private void onSessionSelected(MouseEvent event) {
        selectedSession = sessionsTable.getSelectionModel().getSelectedItem();
        selectButton.setDisable(selectedSession == null);
    }

    /**
     * Handles the event when the select button is clicked.
     *
     * @param event the action event
     */
    @FXML
    private void onSelectButtonClicked(ActionEvent event) {
        if (selectedSession != null) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/example/cinamacentermanagement/fxml/cashierStep3.fxml"));

                Parent thirdStageRoot = loader.load();

                // Grab the controller of Step3
                CashierStep3Controller controller = loader.getController();
                // Pass the Session object and optional IDs
                controller.setSelectedSession(selectedSession);
                controller.initialize(selectedSession.getSessionId());

                // Create a *new* stage for Step3
                Stage newStage = new Stage();
                newStage.setTitle("Cashier Step 3");
                newStage.setScene(new Scene(thirdStageRoot));

                // Optionally, if you want it modal (blocking):
                // newStage.initModality(Modality.APPLICATION_MODAL);

                // Show the new stage (window) for Step3
                newStage.show();

                // Close (or hide) the current Step2 stage
                Stage currentStage = (Stage) selectButton.getScene().getWindow();
                currentStage.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles the event when the back button is clicked.
     *
     * @param event the action event
     */
    @FXML
    private void onBackButtonClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/cinamacentermanagement/views/cashierStep1.fxml"));
            Parent firstStageRoot = loader.load();
            Stage currentStage = (Stage) movieComboBox.getScene().getWindow();
            currentStage.setScene(new Scene(firstStageRoot));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows an alert dialog with the specified type, title, and content.
     *
     * @param type    the type of the alert
     * @param title   the title of the alert
     * @param content the content of the alert
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Sets the selected movie and populates the sessions for it.
     *
     * @param movie the selected movie
     */
    public void setSelectedMovie(Movie movie) {
        this.selectedMovie = movie;
        if (this.selectedMovie != null) {
            // Optionally, set the ComboBox to show this movie
            movieComboBox.setValue(this.selectedMovie);

            // Also, populate sessions
            populateSessions(this.selectedMovie);
        }
    }
}