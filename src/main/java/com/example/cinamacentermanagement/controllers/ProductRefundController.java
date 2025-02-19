package com.example.cinamacentermanagement.controllers;

import com.example.cinamacentermanagement.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controller class for handling product refunds.
 */
public class ProductRefundController {

    @FXML
    private ComboBox<String> productNameComboBox;

    @FXML
    private ComboBox<String> revenueIdComboBox;

    @FXML
    private Button refundButton;

    @FXML
    private VBox menuArea;

    private Connection connection;

    /**
     * Initializes the controller class.
     */
    @FXML
    private void initialize() {
        refundButton.setOnAction(_ -> processRefund());
        loadProductNames();
        loadRevenueDates();
    }

    /**
     * Sets the database connection.
     *
     * @param connection the database connection
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
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
     * Loads the product names from the database and populates the product name combo box.
     */
    private void loadProductNames() {
        try {
            checkConnection();
            String query = "SELECT name FROM products";
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productNameComboBox.getItems().add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Unable to load product names.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Loads the revenue dates from the database and populates the revenue date combo box.
     */
    private void loadRevenueDates() {
        try {
            checkConnection();
            String query = "SELECT date FROM revenue";
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    revenueIdComboBox.getItems().add(rs.getString("date"));
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Unable to load revenue dates.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Processes the product refund by updating the stock and total revenue in the database.
     */
    private void processRefund() {
        try {
            checkConnection();
        } catch (SQLException e) {
            showAlert("Database Error", "Unable to connect to the database.", Alert.AlertType.ERROR);
            e.printStackTrace();
            return;
        }

        String productName = productNameComboBox.getValue();
        String revenueDate = revenueIdComboBox.getValue();
        if (productName == null || revenueDate == null) {
            showAlert("Validation Error", "Product name and revenue date are required.", Alert.AlertType.ERROR);
            return;
        }

        try {
            connection.setAutoCommit(false);

            // Update stock
            String updateStockQuery = "UPDATE products SET stock = stock + 1 WHERE name = ?";
            try (PreparedStatement updateStockStmt = connection.prepareStatement(updateStockQuery)) {
                updateStockStmt.setString(1, productName);
                int rowsAffected = updateStockStmt.executeUpdate();
                if (rowsAffected == 0) {
                    showAlert("Error", "Product not found.", Alert.AlertType.ERROR);
                    connection.rollback();
                    return;
                }
            }

            // Get product price
            String getPriceQuery = "SELECT price FROM products WHERE name = ?";
            double productPrice;
            try (PreparedStatement getPriceStmt = connection.prepareStatement(getPriceQuery)) {
                getPriceStmt.setString(1, productName);
                try (ResultSet rs = getPriceStmt.executeQuery()) {
                    if (rs.next()) {
                        productPrice = rs.getDouble("price");
                    } else {
                        showAlert("Error", "Product not found.", Alert.AlertType.ERROR);
                        connection.rollback();
                        return;
                    }
                }
            }

            // Update total revenue
            String updateRevenueQuery = "UPDATE revenue SET total_revenue = total_revenue - ? WHERE date = ?";
            try (PreparedStatement updateRevenueStmt = connection.prepareStatement(updateRevenueQuery)) {
                updateRevenueStmt.setDouble(1, productPrice);
                updateRevenueStmt.setString(2, revenueDate);
                int rowsAffected = updateRevenueStmt.executeUpdate();
                if (rowsAffected == 0) {
                    showAlert("Error", "Revenue date not found.", Alert.AlertType.ERROR);
                    connection.rollback();
                    return;
                }
            }

            connection.commit();
            showAlert("Success", "Product refunded successfully. Stock and revenue updated.", Alert.AlertType.INFORMATION);
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