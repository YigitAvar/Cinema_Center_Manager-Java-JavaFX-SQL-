package com.example.cinamacentermanagement.controllers;

import com.example.cinamacentermanagement.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;


/**
 * Controller class for managing inventory operations in the cinema center management system.
 */
public class InventoryController {

    @FXML
    private Button viewInventoryButton;

    @FXML
    private Button updateInventoryButton;

    @FXML
    private TableView<Product> inventoryTable;

    @FXML
    private TableColumn<Product, Integer> productIdColumn;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, Double> priceColumn;

    @FXML
    private TableColumn<Product, Integer> stockQuantityColumn;

    @FXML
    private TableColumn<Product, String> descriptionColumn;

    @FXML
    private TableColumn<Product, String> categoryColumn;

    @FXML
    private TableColumn<Product, Double> taxColumn;

    @FXML
    private ImageView productImageView;

    @FXML
    private Button updatePriceButton;

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
        } catch (SQLException e) {
            showAlert("Database Connection Failed", "Unable to connect to the database.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }

        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        taxColumn.setCellValueFactory(new PropertyValueFactory<>("tax"));

        viewInventoryButton.setOnAction(_ -> {
            try {
                viewInventory();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to retrieve inventory.", Alert.AlertType.ERROR);
            }
        });

        updatePriceButton.setOnAction(_ -> {
            try {
                updateProductPrices();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to retrieve inventory.", Alert.AlertType.ERROR);
            }
        });

        updateInventoryButton.setOnAction(_ -> {
            try {
                updateInventory();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to update inventory.", Alert.AlertType.ERROR);
            }
        });

        inventoryTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateProductImage(newValue.getImageUrl());
            }
        });
    }

    /**
     * Initializes the controller class. This method is automatically called after the FXML file has been loaded.
     */
    private void viewInventory() throws SQLException {
        setConnection(connection);
        checkConnection();
        ObservableList<Product> inventoryList = FXCollections.observableArrayList();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Products");

            while (resultSet.next()) {
                Product product = new Product(
                        resultSet.getInt("product_id"),
                        resultSet.getString("name"),
                        resultSet.getDouble("price"),
                        resultSet.getString("category"),
                        resultSet.getString("description"),
                        resultSet.getInt("stock"),
                        resultSet.getString("image"),
                        resultSet.getDouble("tax")
                );
                inventoryList.add(product);
            }

            inventoryTable.setItems(inventoryList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to retrieve inventory: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Updates the product image based on the provided image URL.
     *
     * @param imageUrl the URL of the product image
     */
    private void updateProductImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            productImageView.setImage(null);
            return;
        }
        try {
            Image image = new Image(imageUrl);
            productImageView.setImage(image);
        } catch (Exception e) {
            productImageView.setImage(null);
            System.err.println("Error loading image: " + e.getMessage());
        }
    }

    /**
     * Updates the prices of products in the inventory.
     *
     * @throws SQLException if a database access error occurs
     */
    private void updateProductPrices() throws SQLException {
        checkConnection();

        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Update Product Price");
        nameDialog.setHeaderText("Enter Product Name");
        nameDialog.setContentText("Product Name:");

        nameDialog.showAndWait().ifPresent(productName -> {
            try {
                // Check if the product exists
                String checkQuery = "SELECT * FROM Products WHERE name = ?";
                PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setString(1, productName);
                ResultSet resultSet = checkStatement.executeQuery();

                if (!resultSet.next()) {
                    showAlert("Product Not Found", "The product \"" + productName + "\" does not exist.", Alert.AlertType.ERROR);
                    return;
                }

                TextInputDialog priceDialog = new TextInputDialog();
                priceDialog.setTitle("Update Product Price");
                priceDialog.setHeaderText("Enter new product price");
                priceDialog.setContentText("New Product Price:");

                priceDialog.showAndWait().ifPresent(newPrice -> {
                    try {
                        double price = Double.parseDouble(newPrice);
                        if (price < 0) {
                            showAlert("Validation Error", "Product price can't be negative.", Alert.AlertType.ERROR);
                            return;
                        }
                        if (price == 0) {
                            showAlert("Validation Error", "Product price can't be zero.", Alert.AlertType.ERROR);
                            return;
                        }

                        String updateQuery = "UPDATE Products SET price = ? WHERE name = ?";
                        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                            preparedStatement.setDouble(1, price);
                            preparedStatement.setString(2, productName);

                            int rowsUpdated = preparedStatement.executeUpdate();
                            if (rowsUpdated > 0) {
                                showAlert("Product Update", "Product price updated successfully.", Alert.AlertType.INFORMATION);
                            } else {
                                showAlert("Product Update", "Failed to update product price.", Alert.AlertType.ERROR);
                            }
                        }
                    } catch (SQLException | NumberFormatException e) {
                        e.printStackTrace();
                        showAlert("Error", "Failed to update product price.", Alert.AlertType.ERROR);
                    }
                });
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to check product existence.", Alert.AlertType.ERROR);
            }
        });
    }

    /**
     * Updates the inventory data in the database.
     *
     * @throws SQLException if a database access error occurs
     */
    private void updateInventory() throws SQLException {
        setConnection(connection);
        checkConnection();

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Update Inventory");
        dialog.setHeaderText("Enter the Product Name");
        dialog.setContentText("Product Name:");

        dialog.showAndWait().ifPresent(productName -> {
            try {
                // Check if the product exists
                String checkQuery = "SELECT * FROM Products WHERE name = ?";
                PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setString(1, productName);
                ResultSet resultSet = checkStatement.executeQuery();

                if (!resultSet.next()) {
                    showAlert("Product Not Found", "The product \"" + productName + "\" does not exist.", Alert.AlertType.ERROR);
                    return;
                }

                TextInputDialog quantityDialog = new TextInputDialog();
                quantityDialog.setTitle("Update Quantity");
                quantityDialog.setHeaderText("Enter the new quantity for the product");
                quantityDialog.setContentText("New Quantity:");

                quantityDialog.showAndWait().ifPresent(quantity -> {
                    try {
                        int newQuantity = Integer.parseInt(quantity);
                        if (newQuantity < 0) {
                            showAlert("Validation Error", "Product quantity can't be negative.", Alert.AlertType.ERROR);
                            return;
                        }

                        String updateQuery = "UPDATE Products SET stock = ? WHERE name = ?";
                        PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                        preparedStatement.setInt(1, newQuantity);
                        preparedStatement.setString(2, productName);

                        int rowsUpdated = preparedStatement.executeUpdate();
                        if (rowsUpdated > 0) {
                            showAlert("Inventory Update", "Product stock updated successfully.", Alert.AlertType.INFORMATION);
                        } else {
                            showAlert("Inventory Update", "Failed to update product stock.", Alert.AlertType.ERROR);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showAlert("Error", "Failed to update inventory.", Alert.AlertType.ERROR);
                    } catch (NumberFormatException e) {
                        showAlert("Error", "Invalid quantity. Please enter a valid number.", Alert.AlertType.ERROR);
                    }
                });
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to check product existence.", Alert.AlertType.ERROR);
            }
        });
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