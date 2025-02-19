package com.example.cinamacentermanagement.controllers;

import com.example.cinamacentermanagement.UserSession;
import com.example.cinamacentermanagement.database.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Controller class for handling user login operations in the cinema center management system.
 */
public class LoginController {

    @FXML
    public Button loginButton;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorMessageLabel;

    /**
     * Handles the login button click action.
     *
     * @param event the action event
     */
    @FXML
    private void clickAction(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorMessageLabel.setText("Username or password cannot be empty!");
            return;
        }

        try {
            if (authenticateUser(username, password)) {
                UserSession.setLoggedInUsername(username);
            } else {
                errorMessageLabel.setText("Wrong username or password!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            errorMessageLabel.setText("Error: " + e.getMessage());
        }
    }

    /**
     * Authenticates the user with the provided username and password.
     *
     * @param username the username
     * @param password the password
     * @return true if authentication is successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    private boolean authenticateUser(String username, String password) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT role, first_name, last_name, password FROM Employees WHERE username = ?")) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String storedPassword = resultSet.getString("password");

                    // Determine if stored password is hashed or plain
                    boolean isAuthenticated = storedPassword.equals(password) || hashPassword(password).equals(storedPassword);

                    if (isAuthenticated) {
                        String role = resultSet.getString("role");
                        String firstName = resultSet.getString("first_name");
                        String lastName = resultSet.getString("last_name");
                        String fullName = firstName + " " + lastName;
                        UserSession.setLoggedInUsername(fullName);

                        if ("admin".equals(role)) {
                            adminPage();
                        } else if ("cashier".equals(role)) {
                            cashierPage();
                        } else if ("manager".equals(role)) {
                            managerPage();
                        }
                        return true;
                    } else {
                        return false; // Incorrect password
                    }
                } else {
                    return false; // User not found
                }
            }
        }
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
     * Loads the admin page.
     */
    private void adminPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinamacentermanagement/fxml/admin.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Page");
            stage.show();
            closeCurrentWindow();
        } catch (IOException e) {
            e.printStackTrace();
            errorMessageLabel.setText("Error " + e.getMessage());
        }

    }

    /**
     * Loads the cashier page.
     */
    private void cashierPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinamacentermanagement/fxml/cashierStep1.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("User Page");
            stage.show();
            closeCurrentWindow();
        } catch (IOException e) {
            e.printStackTrace();
            errorMessageLabel.setText("Error " + e.getMessage());
        }
    }

    /**
     * Loads the manager page.
     */
    private void managerPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinamacentermanagement/fxml/manager2.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Manager Page");
            stage.show();
            closeCurrentWindow();
        } catch (IOException e) {
            e.printStackTrace();
            errorMessageLabel.setText("Error " + e.getMessage());
        }
    }

    /**
     * Handles the logout action.
     *
     * @param event the action event
     * @throws IOException if an I/O error occurs
     */
    public void handleLogout(ActionEvent event) throws IOException {
        UserSession.clearSession();
        Parent loginRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/cinamacentermanagement/fxml/login.fxml")));
        Scene loginScene = new Scene(loginRoot);
        Stage primaryStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    /**
     * Closes the current window.
     */
    private void closeCurrentWindow() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }
}