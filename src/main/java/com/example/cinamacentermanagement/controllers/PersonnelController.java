package com.example.cinamacentermanagement.controllers;

import com.example.cinamacentermanagement.UserSession;
import com.example.cinamacentermanagement.database.DatabaseConnection;
import com.example.cinamacentermanagement.model.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Optional;

/**
 * Controller class for managing personnel operations.
 */
public class PersonnelController {

    @FXML
    private TableView<Employee> employeesTableView;
    @FXML
    private TableColumn<Employee, Integer> idColumn;
    @FXML
    private TableColumn<Employee, String> firstNameColumn;
    @FXML
    private TableColumn<Employee, String> lastNameColumn;
    @FXML
    private TableColumn<Employee, String> usernameColumn;
    @FXML
    private TableColumn<Employee, String> roleColumn;
    @FXML
    private TableColumn<Employee, String> passwordColumn;
    @FXML
    private Button hirePersonnelButton;
    @FXML
    private Button firePersonnelButton;
    @FXML
    private Button viewAllEmployeesButton;

    private Connection connection;

    /**
     * Controller class for managing personnel operations.
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
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        try {
            connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            showAlert("Database Connection Failed", "Unable to connect to the database.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }

        hirePersonnelButton.setOnAction(_ -> {
            try {
                hirePersonnel();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to retrieve personnel.", Alert.AlertType.ERROR);
            }
        });

        firePersonnelButton.setOnAction(_ -> {
            try {
                firePersonnel();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to update personnel.", Alert.AlertType.ERROR);
            }
        });

        viewAllEmployeesButton.setOnAction(_ -> {
            try {
                viewAllEmployees();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to retrieve employees.", Alert.AlertType.ERROR);
            }
        });

        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        roleColumn.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
        passwordColumn.setCellValueFactory(cellData -> cellData.getValue().passwordProperty());
    }

    /**
     * Retrieves and displays all employees in the table view.
     *
     * @throws SQLException if a database access error occurs
     */
    private void viewAllEmployees() throws SQLException {
        checkConnection();

        String query = "SELECT * FROM Employees";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            ObservableList<Employee> employees = FXCollections.observableArrayList();
            while (resultSet.next()) {
                Employee employee = new Employee(
                        resultSet.getInt("employee_id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("role")
                );
                employees.add(employee);
            }

            employeesTableView.setItems(employees);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to retrieve employees.", Alert.AlertType.ERROR);
        }
    }
    // Hash password here
    /**
     * Hires a new personnel by collecting their details and inserting them into the database.
     *
     * @throws SQLException if a database access error occurs
     */
    private void hirePersonnel() throws SQLException {

        checkConnection();

        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Hire Personnel");
        nameDialog.setHeaderText("Enter Personnel's Full Name");
        nameDialog.setContentText("Full Name:");

        nameDialog.showAndWait().ifPresent(fullName -> {
            // Split full name into Firstname and Lastname
            String[] nameParts = fullName.split(" ", 2);
            String firstName = nameParts.length > 0 ? nameParts[0] : "";
            String lastName = nameParts.length > 1 ? nameParts[1] : "";

            TextInputDialog usernameDialog = new TextInputDialog();
            usernameDialog.setTitle("Hire Personnel");
            usernameDialog.setHeaderText("Enter Personnel's Username");
            usernameDialog.setContentText("Username:");

            usernameDialog.showAndWait().ifPresent(username -> {
                TextInputDialog passwordDialog = new TextInputDialog();
                passwordDialog.setTitle("Hire Personnel");
                passwordDialog.setHeaderText("Enter Personnel's Password");
                passwordDialog.setContentText("Password:");

                passwordDialog.showAndWait().ifPresent(password -> {
                    TextInputDialog roleDialog = new TextInputDialog();
                    roleDialog.setTitle("Hire Personnel");
                    roleDialog.setHeaderText("Enter Personnel's Role");
                    roleDialog.setContentText("Role:");

                    roleDialog.showAndWait().ifPresent(role -> {

                        try {
                            // Hash the password
                            String hashedPassword = hashPassword(password);

                            String insertQuery = "INSERT INTO Employees (first_name, last_name, username, password, role) VALUES (?, ?, ?, ?, ?)";
                            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                            preparedStatement.setString(1, firstName);
                            preparedStatement.setString(2, lastName);
                            preparedStatement.setString(3, username);
                            preparedStatement.setString(4, hashedPassword); // Use hashed password
                            preparedStatement.setString(5, role);

                            int rowsInserted = preparedStatement.executeUpdate();
                            if (rowsInserted > 0) {
                                showAlert("Personnel Management", "Personnel hired successfully.", Alert.AlertType.INFORMATION);
                            } else {
                                showAlert("Personnel Management", "Failed to hire personnel.", Alert.AlertType.ERROR);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                            showAlert("Error", "Failed to hire personnel.", Alert.AlertType.ERROR);
                        }
                    });
                });
            });
        });
    }

    /**
     * Hashes a password using SHA-256.
     *
     * @param password the password to hash
     * @return the hashed password in hexadecimal format
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    /**
     * Fires a personnel by removing their record from the database.
     *
     * @throws SQLException if a database access error occurs
     */
    public void firePersonnel() throws SQLException {
        checkConnection();

        String loggedInUsername = UserSession.getLoggedInUsername();
        String personnelName;

        while (true) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Fire Personnel");
            dialog.setHeaderText("Enter Personnel's Username To Fire");
            dialog.setContentText("Username:");

            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                personnelName = result.get().trim();

                // Validate input
                if (personnelName.isEmpty()) {
                    showAlert("Invalid Input", "Username cannot be empty. Please try again.", Alert.AlertType.ERROR);
                } else if (!personnelName.matches("[a-zA-Z0-9_çÇğĞıİöÖşŞüÜ]+")) {
                    showAlert("Invalid Input", "Username can only contain letters, digits, and underscores. Please try again.", Alert.AlertType.ERROR);
                } else if (personnelName.equalsIgnoreCase(loggedInUsername)) {
                    showAlert("Error", "You cannot fire yourself!", Alert.AlertType.ERROR);
                } else {
                    break;
                }
            } else {
                showAlert("Operation Cancelled", "Firing operation cancelled.", Alert.AlertType.INFORMATION);
                return;
            }
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection == null) {
                showAlert("Database Error", "Database is not connected.", Alert.AlertType.ERROR);
                return;
            }

            String checkQuery = "SELECT username FROM Employees WHERE username = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setString(1, personnelName);
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        showAlert("Not Found", "There is no employee with the username: " + personnelName, Alert.AlertType.ERROR);
                        return;
                    }
                }
            }

            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Firing");
            confirmationAlert.setHeaderText("Are you sure you want to fire this employee?");
            confirmationAlert.setContentText("Username: " + personnelName);

            Optional<ButtonType> confirmation = confirmationAlert.showAndWait();
            if (confirmation.isEmpty() || confirmation.get() != ButtonType.OK) {
                showAlert("Operation Cancelled", "Firing operation cancelled.", Alert.AlertType.INFORMATION);
                return;
            }

            String deleteQuery = "DELETE FROM Employees WHERE username = ?";
            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                deleteStatement.setString(1, personnelName);
                int rowsAffected = deleteStatement.executeUpdate();

                if (rowsAffected > 0) {
                    showAlert("Success", "Employee with username " + personnelName + " has been fired.", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "An error occurred while attempting to fire the employee.", Alert.AlertType.ERROR);
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", "An error occurred while firing the employee.", Alert.AlertType.ERROR);
            e.printStackTrace();
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
}