package com.example.cinamacentermanagement.controllers;

import com.example.cinamacentermanagement.database.DatabaseConnection;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

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
 * Controller class for updating movies.
 */
public class UpdateMovieController {

    @FXML
    private ListView<String> movieListView;

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
    private ObservableList<String> movieList;
    private String selectedMovie;
    private PauseTransition imageUpdatePause;

    /**
     * Initializes the controller class.
     */
    @FXML
    private void initialize() {
        try {
            connection = DatabaseConnection.getConnection();
            if (connection == null) {
                showAlert("Database Connection Failed", "Unable to connect to the database.", Alert.AlertType.ERROR);
                return; // Uygulamayı kapatmak daha mantıklı olabilir
            }
            loadMovies();
            loadGenres();
        } catch (SQLException e) {
            showAlert("Database Error", "Error: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }

        movieListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedMovie = newValue;
            if (newValue != null) {
                loadMovieDetails(newValue);
            }
        });

        imageUpdatePause = new PauseTransition(Duration.seconds(0.5)); // 0.5 saniyelik gecikme
        imageUrlField.textProperty().addListener((observable, oldValue, newValue) -> {
            imageUpdatePause.setOnFinished(event -> updateImagePreview(newValue));
            imageUpdatePause.playFromStart();
        });

        saveButton.setOnAction(event -> {
            try {
                updateMovie();
            } catch (SQLException e) {
                showAlert("Database Error", "Error updating movie: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        });
        selectImageButton.setOnAction(_ -> selectImage());
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
     * Loads the movies from the database and populates the movie list view.
     *
     * @throws SQLException if a database access error occurs
     */
    private void loadMovies() throws SQLException {
        movieList = FXCollections.observableArrayList();
        String query = "SELECT title FROM Movies";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                movieList.add(resultSet.getString("title"));
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error loading movies: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }

        movieListView.setItems(movieList);
    }

    /**
     * Loads the details of the selected movie from the database.
     *
     * @param movie the title of the selected movie
     */
    private void loadMovieDetails(String movie) {
        String query = "SELECT title, summary, genre, poster FROM Movies WHERE title = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, movie);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    movieNameField.setText(resultSet.getString("title"));
                    descriptionField.setText(resultSet.getString("summary"));
                    genreComboBox.setValue(resultSet.getString("genre"));
                    String imageUrl = resultSet.getString("poster");
                    imageUrlField.setText(imageUrl);
                    updateImagePreview(imageUrl);
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error loading movie details: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
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
     * Updates the selected movie in the database.
     *
     * @throws SQLException if a database access error occurs
     */
    private void updateMovie() throws SQLException {
        String movieName = movieNameField.getText();
        String description = descriptionField.getText();
        String genre = genreComboBox.getValue();
        String imageUrl = imageUrlField.getText();

        if (movieName.isEmpty() || description.isEmpty() || genre.isEmpty() || imageUrl.isEmpty()) {
            showAlert("Validation Error", "All fields are required.", Alert.AlertType.ERROR);
            return;
        }

        String updateQuery = "UPDATE Movies SET title = ?, summary = ?, genre = ?, poster = ? WHERE title = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setString(1, movieName);
            statement.setString(2, description);
            statement.setString(3, genre);
            statement.setString(4, imageUrl);
            statement.setString(5, selectedMovie);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                showAlert("Update Movie", "Movie updated successfully.", Alert.AlertType.INFORMATION);
                loadMovies(); // Filmleri yeniden yükle
                movieListView.getSelectionModel().select(movieName); // Güncellenen filmi seçili hale getir
            } else {
                showAlert("Update Movie", "Failed to update movie. No matching movie found.", Alert.AlertType.ERROR);
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

    /**
     * Sets the database connection.
     *
     * @param connection the database connection
     */
    public void setConnection(Connection connection) {
    }
}