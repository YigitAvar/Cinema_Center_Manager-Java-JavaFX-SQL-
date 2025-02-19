package com.example.cinamacentermanagement.controllers;

import com.example.cinamacentermanagement.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.sql.Connection;

/**
 * Controller class for managing operations for manager's interface.
 */
public class ManagerOperationsController2 {
    @FXML
    private Button InventoryButton;

    @FXML
    private Button PersonnelButton;

    @FXML
    private Button PricingButton;

    @FXML
    private Button RevenueButton;

    @FXML
    private Label loggedInAsLabel;

    private Connection connection;

    @FXML
    private Button logoutButton;

    @FXML
    private VBox menuArea;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {

        String loggedInUsername = UserSession.getLoggedInUsername();
        loggedInAsLabel.setText("Welcome " + loggedInUsername);

        InventoryButton.setOnAction(event -> Inventory());
        PersonnelButton.setOnAction(event -> Personnel());
        PricingButton.setOnAction(event -> Pricing());
        RevenueButton.setOnAction(event -> Revenue());
        logoutButton.setOnAction(this::handleLogout);
    }

    /**
     * Loads the Inventory Management view.
     */
    private void Inventory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinamacentermanagement/fxml/inventory.fxml"));
            Parent root = loader.load();

            InventoryController controller = loader.getController();
            controller.setConnection(connection);

            menuArea.getChildren().clear();
            menuArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open Inventory Management.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Loads the Personnel Management view.
     */
    private void Personnel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinamacentermanagement/fxml/personnel.fxml"));
            Parent root = loader.load();

            PersonnelController controller = loader.getController();
            controller.setConnection(connection);

            menuArea.getChildren().clear();
            menuArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open Personnel Management.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Loads the Revenue Management view.
     */
    private void Revenue() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinamacentermanagement/fxml/revenue.fxml"));
            Parent root = loader.load();

            RevenueController controller = loader.getController();// sorun var add değişicek
            controller.setConnection(connection);

            menuArea.getChildren().clear();
            menuArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open Revenue Management.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Loads the Pricing Management view.
     */
    private void Pricing() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinamacentermanagement/fxml/pricing.fxml"));
            Parent root = loader.load();

            PricingController controller = loader.getController();// sorun var add değişicek
            controller.setConnection(connection);

            menuArea.getChildren().clear();
            menuArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open Pricing Management.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Displays an alert dialog with the specified title, message, and alert type.
     *
     * @param title the title of the alert dialog
     * @param message the message to display in the alert dialog
     * @param alertType the type of alert
     */
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
     * Sets the database connection.
     *
     * @param connection the database connection
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}



