package com.example.cinamacentermanagement.controllers;

import com.example.cinamacentermanagement.Discount;
import com.example.cinamacentermanagement.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controller class for managing revenue and discounts in the cinema center management system.
 */
public class RevenueController {

    @FXML
    public Label dateLabel;
    @FXML
    private Label totalRevenueLabel;
    @FXML
    private Label totalTaxLabel;
    @FXML
    private TableView<Discount> ageBasedDiscountTable;
    @FXML
    private TableColumn<Discount, Integer> discountIdColumn;
    @FXML
    private TableColumn<Discount, Integer> ageMinColumn;
    @FXML
    private TableColumn<Discount, Integer> ageMaxColumn;
    @FXML
    private TableColumn<Discount, Double> discountPercentageColumn;
    @FXML
    private Button viewDiscountsButton;
    @FXML
    private Button updateDiscountsButton;

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
     * Checks if the database connection is valid and reconnects if necessary.
     *
     * @throws SQLException if a database access error occurs
     */
    private void checkConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DatabaseConnection.getConnection();
        }
    }

    /**
     * Initializes the controller class. This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        try {
            connection = DatabaseConnection.getConnection();
            calculateRevenueAndTaxes();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        discountIdColumn.setCellValueFactory(new PropertyValueFactory<>("discountId"));
        ageMinColumn.setCellValueFactory(new PropertyValueFactory<>("ageMin"));
        ageMaxColumn.setCellValueFactory(new PropertyValueFactory<>("ageMax"));
        discountPercentageColumn.setCellValueFactory(new PropertyValueFactory<>("discountPercentage"));

        viewDiscountsButton.setOnAction(event -> {
            try {
                loadDiscounts();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        updateDiscountsButton.setOnAction(event -> {
            try {
                updateDiscounts();
            } catch (SQLException e) {
                showAlert("Database Error", "Error updating discount: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        });
    }

    /**
     * Calculates the total revenue and total tax for the current date and updates the corresponding labels.
     *
     * @throws SQLException if a database access error occurs
     */
    /**
     * Calculates the total revenue and total tax for the most recent date in the Revenue table and updates the corresponding labels.
     *
     * @throws SQLException if a database access error occurs
     */
    private void calculateRevenueAndTaxes() throws SQLException {

        checkConnection();

        double totalRevenue = 0.0;
        double totalTax = 0.0;
        String date = "";

        // Retrieve the most recent date from the Revenue table
        String dateQuery = "SELECT MAX(date) AS recent_date FROM Revenue";
        try (PreparedStatement dateStatement = connection.prepareStatement(dateQuery);
             ResultSet dateResultSet = dateStatement.executeQuery()) {
            if (dateResultSet.next()) {
                date = dateResultSet.getString("recent_date");
            }
        }

        // Sum the total_revenue and total_tax for the most recent date
        String revenueQuery = "SELECT SUM(total_revenue) AS total_revenue, SUM(total_tax) AS total_tax FROM Revenue WHERE date = ?";
        try (PreparedStatement revenueStatement = connection.prepareStatement(revenueQuery)) {
            revenueStatement.setString(1, date);
            try (ResultSet revenueResultSet = revenueStatement.executeQuery()) {
                if (revenueResultSet.next()) {
                    totalRevenue = revenueResultSet.getDouble("total_revenue");
                    totalTax = revenueResultSet.getDouble("total_tax");
                }
            }
        }

        totalRevenueLabel.setText(String.format("%.2f ₺", totalRevenue));
        totalTaxLabel.setText(String.format("%.2f ₺", totalTax));
        dateLabel.setText(String.format("%s", date));
    }

    /**
     * Updates the age based discount rates.
     *
     * @throws SQLException if a database access error occurs
     */
    @FXML
    private void updateDiscounts() throws SQLException {
        checkConnection();

        TextInputDialog idDialog = new TextInputDialog();
        idDialog.setTitle("Update Discount Rates");
        idDialog.setHeaderText("Enter Discount ID");
        idDialog.setContentText("Discount ID:");

        idDialog.showAndWait().ifPresent(discountId -> {
            try {
                int id = Integer.parseInt(discountId);

                // Check if the discount ID exists
                String checkQuery = "SELECT * FROM agebaseddiscounts WHERE discount_id = ?";
                PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setInt(1, id);
                ResultSet resultSet = checkStatement.executeQuery();

                if (!resultSet.next()) {
                    showAlert("Discount Not Found", "The discount ID \"" + id + "\" does not exist.", Alert.AlertType.ERROR);
                    return;
                }

                TextInputDialog minAgeDialog = new TextInputDialog();
                minAgeDialog.setTitle("Update Discount Rates");
                minAgeDialog.setHeaderText("Enter Minimum Age");
                minAgeDialog.setContentText("Minimum Age:");

                minAgeDialog.showAndWait().ifPresent(minAge -> {
                    try {
                        int min = Integer.parseInt(minAge);
                        if (min <= 0) {
                            showAlert("Validation Error", "Minimum age must be greater than zero.", Alert.AlertType.ERROR);
                            return;
                        }

                        TextInputDialog maxAgeDialog = new TextInputDialog();
                        maxAgeDialog.setTitle("Update Discount Rates");
                        maxAgeDialog.setHeaderText("Enter Maximum Age");
                        maxAgeDialog.setContentText("Maximum Age:");

                        maxAgeDialog.showAndWait().ifPresent(maxAge -> {
                            try {
                                int max = Integer.parseInt(maxAge);
                                if (max <= 0) {
                                    showAlert("Validation Error", "Maximum age must be greater than zero.", Alert.AlertType.ERROR);
                                    return;
                                }
                                if (min > max) {
                                    showAlert("Validation Error", "Minimum age cannot be greater than maximum age.", Alert.AlertType.ERROR);
                                    return;
                                }
                                if (min == max) {
                                    showAlert("Validation Error", "Minimum age cannot be equal to maximum age.", Alert.AlertType.ERROR);
                                    return;
                                }

                                TextInputDialog rateDialog = new TextInputDialog();
                                rateDialog.setTitle("Update Discount Rates");
                                rateDialog.setHeaderText("Enter new discount rate");
                                rateDialog.setContentText("New Discount Rate:");

                                rateDialog.showAndWait().ifPresent(newRate -> {
                                    try {
                                        double newDiscount = Double.parseDouble(newRate);
                                        if (newDiscount < 0 || newDiscount > 100) {
                                            showAlert("Validation Error", "Discount rate must be between 0 and 100.", Alert.AlertType.ERROR);
                                            return;
                                        }

                                        String updateQuery = "UPDATE agebaseddiscounts SET age_min = ?, age_max = ?, discount_percentage = ? WHERE discount_id = ?";
                                        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                                            preparedStatement.setInt(1, min);
                                            preparedStatement.setInt(2, max);
                                            preparedStatement.setDouble(3, newDiscount);
                                            preparedStatement.setInt(4, id);

                                            int rowsUpdated = preparedStatement.executeUpdate();
                                            if (rowsUpdated > 0) {
                                                showAlert("Discount Update", "Discount rate updated successfully.", Alert.AlertType.INFORMATION);
                                            } else {
                                                showAlert("Discount Update", "Failed to update discount rates.", Alert.AlertType.ERROR);
                                            }
                                        }
                                    } catch (SQLException | NumberFormatException e) {
                                        e.printStackTrace();
                                        showAlert("Error", "Failed to update discount rates.", Alert.AlertType.ERROR);
                                    }
                                });
                            } catch (NumberFormatException e) {
                                showAlert("Error", "Invalid Maximum Age. Please enter a valid number.", Alert.AlertType.ERROR);
                            }
                        });
                    } catch (NumberFormatException e) {
                        showAlert("Error", "Invalid Minimum Age. Please enter a valid number.", Alert.AlertType.ERROR);
                    }
                });
            } catch (NumberFormatException | SQLException e) {
                showAlert("Error", "Invalid Discount ID. Please enter a valid number.", Alert.AlertType.ERROR);
            }
        });
    }

    /**
     * Loads the discount data from the database and populates the discount table.
     *
     * @throws SQLException if a database access error occurs
     */
    private void loadDiscounts() throws SQLException {

       checkConnection();

        ageBasedDiscountTable.getItems().clear();
        String query = "SELECT discount_id, age_min, age_max, discount_percentage FROM agebaseddiscounts";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int discountId = resultSet.getInt("discount_id");
                int ageMin = resultSet.getInt("age_min");
                int ageMax = resultSet.getInt("age_max");
                double discountPercentage = resultSet.getDouble("discount_percentage");
                ageBasedDiscountTable.getItems().add(new Discount(discountId, ageMin, ageMax, discountPercentage));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the action of viewing discounts by loading the discount data.
     *
     * @throws SQLException if a database access error occurs
     */
    @FXML
    private void viewDiscounts() throws SQLException {
        loadDiscounts();
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
}