package com.example.cinamacentermanagement.controllers;

import com.example.cinamacentermanagement.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Controller class for handling admin operations.
 */
public class AdminController {

    @FXML
    private Button addMovieButton;

    @FXML
    private Button updateMovieButton;

    @FXML
    private Button createScheduleButton;

    @FXML
    private Button updateScheduleButton;

    private Connection connection;

    @FXML
    private Button ProductRefundButton;
    @FXML
    private Button TicketRefundButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Label loggedInAsLabel;

    @FXML
    private VBox menuArea;

    /**
     * Initializes the controller class.
     */
    @FXML
    private void initialize() {
        String loggedInUsername = UserSession.getLoggedInUsername();
        loggedInAsLabel.setText("Welcome " + loggedInUsername);

        addMovieButton.setOnAction(event -> addMovie());
        updateMovieButton.setOnAction(event -> updateMovie());
        createScheduleButton.setOnAction(event -> createSchedule());
        updateScheduleButton.setOnAction(event -> updateSchedule());
        ProductRefundButton.setOnAction(event -> ProductRefund());
        TicketRefundButton.setOnAction(event -> TicketRefund());
        logoutButton.setOnAction(this::handleLogout);
    }

    /**
     * Opens the add movie dialog.
     */
    private void addMovie() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinamacentermanagement/fxml/add_movie.fxml"));
            Parent root = loader.load();

            AddMovieController controller = loader.getController();
            controller.setConnection(connection);

            menuArea.getChildren().clear();
            menuArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open add movie dialog.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Opens the update movie dialog.
     */
    private void updateMovie() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinamacentermanagement/fxml/update_movie.fxml"));
            Parent root = loader.load();

            UpdateMovieController controller = loader.getController();
            controller.setConnection(connection);

            menuArea.getChildren().clear();
            menuArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open update movie dialog.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Opens the create schedule dialog.
     */
    private void createSchedule() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinamacentermanagement/fxml/create_schedule.fxml"));
            Parent root = loader.load();

            CreateScheduleController controller = loader.getController();
            controller.setConnection(connection);

            menuArea.getChildren().clear();
            menuArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open create schedule dialog.", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Opens the update schedule dialog.
     */
    private void updateSchedule() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinamacentermanagement/fxml/UpdateSchedule.fxml"));
            Parent root = loader.load();

            UpdateScheduleController controller = loader.getController();
            controller.setConnection(connection);

            menuArea.getChildren().clear();
            menuArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open create schedule dialog.", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Opens the product refund dialog.
     */
    private void ProductRefund() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinamacentermanagement/fxml/product_refund.fxml"));
            Parent root = loader.load();

            ProductRefundController controller = loader.getController();
            controller.setConnection(connection);

            menuArea.getChildren().clear();
            menuArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open refund selection dialog.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Opens the ticket refund dialog.
     */
    private void TicketRefund() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinamacentermanagement/fxml/ticket_refund.fxml"));
            Parent root = loader.load();

            TicketRefundController controller = loader.getController();
            controller.setConnection(connection);

            menuArea.getChildren().clear();
            menuArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open refund selection dialog.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Handles the logout action.
     *
     * @param event the action event
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            LoginController loginController = new LoginController();
            loginController.handleLogout(event);
        } catch (IOException e) {
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

    /**
     * Sets the database connection.
     *
     * @param connection the database connection
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}