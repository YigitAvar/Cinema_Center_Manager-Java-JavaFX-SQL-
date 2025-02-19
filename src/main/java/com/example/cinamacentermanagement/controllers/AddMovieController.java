package com.example.cinamacentermanagement.controllers;

import com.example.cinamacentermanagement.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controller class for adding movies.
 */
public class AddMovieController {

    @FXML
    private TextField movieNameField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ComboBox<String> genreComboBox;

    @FXML
    private TextField imageUrlField;

    @FXML
    private ImageView imageView;

    @FXML
    private Button saveButton;

    @FXML
    private Button selectImageButton;

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
     * Initializes the controller class.
     */
    @FXML
    private void initialize() {
        try {
            connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            showAlert("Database Connection Failed", "Unable to connect to the database.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
        saveButton.setOnAction(_ -> {
            try {
                saveMovie();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        selectImageButton.setOnAction(_ -> selectImage());
        loadGenres();
    }

    /**
     * Loads the genres from the database and populates the genre combo box.
     */
    private void loadGenres() {
        ObservableList<String> genres = FXCollections.observableArrayList();
        String query = "SELECT DISTINCT genre FROM Movies";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                genres.add(resultSet.getString("genre"));
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error loading genres: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
        genreComboBox.setItems(genres);
    }

    /**
     * Opens a file chooser to select an image and updates the image preview.
     */
    private void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                Path iconDirectory = Paths.get("movie_icons");
                if (!Files.exists(iconDirectory)) {
                    Files.createDirectories(iconDirectory);
                }
                Path destinationPath = iconDirectory.resolve(selectedFile.getName());
                Files.copy(selectedFile.toPath(), destinationPath);
                imageUrlField.setText(destinationPath.toString());
                updateImagePreview(destinationPath.toString());
            } catch (IOException e) {
                showAlert("File Error", "Failed to copy the image file.", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    /**
     * Updates the image preview with the selected image.
     *
     * @param imageUrl the URL of the image
     */
    private void updateImagePreview(String imageUrl) {
        try {
            Image image = new Image("file:" + imageUrl);
            imageView.setImage(image);
        } catch (Exception e) {
            imageView.setImage(null);
            System.err.println("Error loading image: " + e.getMessage());
        }
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
     * Saves the movie to the database.
     *
     * @throws SQLException if a database access error occurs
     */
    private void saveMovie() throws SQLException {
        setConnection(connection);
        checkConnection();
        String movieName = movieNameField.getText();
        String description = descriptionField.getText();
        String genreInfo = genreComboBox.getValue();
        String imageUrl = imageUrlField.getText();

        if (movieName.isEmpty() || description.isEmpty() || imageUrl.isEmpty() || genreInfo == null) {
            showAlert("Validation Error", "All fields are required.", Alert.AlertType.ERROR);
            return;
        }

        try {
            String insertQuery = "INSERT INTO Movies (title, summary, poster, genre) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, movieName);
            preparedStatement.setString(2, description);
            preparedStatement.setString(3, imageUrl);
            preparedStatement.setString(4, genreInfo);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                showAlert("Add Movie Management", "Movie added successfully.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Add Movie Management", "Failed to add movie.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add movie.", Alert.AlertType.ERROR);
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