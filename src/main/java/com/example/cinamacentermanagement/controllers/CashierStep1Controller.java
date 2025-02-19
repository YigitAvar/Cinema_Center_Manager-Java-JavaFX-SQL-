package com.example.cinamacentermanagement.controllers;

import com.example.cinamacentermanagement.dao.MovieDao;
import com.example.cinamacentermanagement.model.Movie;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * Controller class for the first step of the cashier process.
 */
public class CashierStep1Controller {

    @FXML
    private ComboBox<String> searchTypeComboBox;

    @FXML
    private TextField searchTextField;

    @FXML
    private ComboBox<String> genreComboBox;

    @FXML
    private GridPane moviesGridPane;

    private final MovieDao movieDao = new MovieDao();

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        // Populate search type dropdown
        searchTypeComboBox.getItems().addAll("Search", "Partial Title", "Full Title", "Genre");
        searchTypeComboBox.getSelectionModel().selectFirst();

        // Populate genre dropdown
        genreComboBox.getItems().addAll("Action", "Drama", "Comedy", "Thriller");

        // Load all movies on startup
        loadAllMovies();
    }

    /**
     * Handles the change of the search type combo box.
     */
    @FXML
    public void handleSearchTypeChange() {
        String selectedSearchType = searchTypeComboBox.getValue();
        if ("Genre".equals(selectedSearchType)) {
            searchTextField.setVisible(false);
            searchTextField.setManaged(false);
            genreComboBox.setVisible(true);
            genreComboBox.setManaged(true);
        } else {
            searchTextField.setVisible(true);
            searchTextField.setManaged(true);
            genreComboBox.setVisible(false);
            genreComboBox.setManaged(false);
        }
    }

    /**
     * Handles the search action based on the selected search type and query.
     */
    @FXML
    public void handleSearch() {
        String searchType = searchTypeComboBox.getValue();
        List<Movie> movies;

        if ("Genre".equals(searchType)) {
            String selectedGenre = genreComboBox.getValue();
            if (selectedGenre == null || selectedGenre.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a genre.");
                return;
            }
            movies = movieDao.searchByGenre(selectedGenre);
        } else {
            String query = searchTextField.getText();
            if (query == null || query.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a search query.");
                return;
            }

            if ("Partial Title".equals(searchType)) {
                movies = movieDao.searchByPartialTitle(query);
            } else { // Full Title
                movies = List.of(movieDao.searchByFullTitle(query));
            }
        }

        displayMovies(movies);
    }

    /**
     * Loads and displays all movies.
     */
    private void loadAllMovies() {
        List<Movie> movies = movieDao.getAllMovies();
        displayMovies(movies);
    }

    /**
     * Displays the given list of movies in the grid pane.
     *
     * @param movies the list of movies to display
     */
    private void displayMovies(List<Movie> movies) {
        moviesGridPane.getChildren().clear();

        int row = 0;
        int col = 0;

        for (Movie movie : movies) {
            VBox movieBox = createMovieBox(movie);
            moviesGridPane.add(movieBox, col, row);

            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }
    }

    /**
     * Creates a VBox containing the movie details.
     *
     * @param movie the movie to display
     * @return a VBox containing the movie details
     */
    private VBox createMovieBox(Movie movie) {
        ImageView posterView = new ImageView();
        try {
            // First try to get the image URL from the database
            if (movie.getPoster() != null && !movie.getPoster().isEmpty()) {
                posterView.setImage(new Image(movie.getPoster(), true));
            } else {
                posterView = new ImageView();
                try {

                    Image image = new Image("file:" + movie.getPoster());
                    
                
                    posterView.setImage(image);
                } catch (Exception e) {
                    // Load a default placeholder image if the poster URL is invalid
                    // posterView.setImage(new
                    // Image(getClass().getResource("/images/placeholder.png").toExternalForm()));
                }

            }
        } catch (Exception e) {

        }

        posterView.setFitWidth(180);
        posterView.setFitHeight(250);
        posterView.setPreserveRatio(true);

        Label titleLabel = new Label(movie.getTitle());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-alignment: center;");

        Label genreLabel = new Label("Genre: " + movie.getGenre());
        genreLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: gray; -fx-text-alignment: center;");

        Button viewButton = new Button("View");
        viewButton.setStyle(
                "-fx-background-color: #28a745; -fx-text-fill: white; -fx-border-radius: 10; -fx-background-radius: 10;");
        viewButton.setPrefWidth(153);
        viewButton.setOnAction(event -> openMovieDetailsModal(movie));

        VBox movieBox = new VBox(15, posterView, titleLabel, genreLabel, viewButton);
        movieBox.setStyle(
                "-fx-alignment: center; -fx-padding: 20; -fx-border-color: lightgray; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white;");
        return movieBox;
    }
    

    /**
     * Opens a modal window displaying the details of the selected movie.
     *
     * @param movie the movie to display
     */
    private void openMovieDetailsModal(Movie movie) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/cinamacentermanagement/fxml/movieDetailsModal.fxml"));
            Parent content = loader.load();

            MovieDetailsModalController controller = loader.getController();
            controller.setMovieDetails(movie);

            Stage stage = new Stage();
            stage.setScene(new Scene(content));
            stage.setWidth(750);
            stage.setHeight(650);

            // Use .show(), not .showAndWait()
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to load movie details.");
        }

    }

    /**
     * Shows the details of the selected movie in a new window.
     *
     * @param movie the movie to display
     */
    @FXML
    private void showMovieDetails(Movie movie) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/cinamacentermanagement/fxml/cashierStep2.fxml"));
            Parent root = loader.load();

            CashierStep2Controller step2Controller = loader.getController();
            step2Controller.setSelectedMovie(movie);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Select Day, Session, and Hall");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the action to search again by clearing the search fields and reloading all movies.
     *
     * @param event the action event
     */
    @FXML
    public void handleSearchAgain(ActionEvent event) {
        searchTextField.clear();
        genreComboBox.getSelectionModel().clearSelection();
        searchTypeComboBox.getSelectionModel().clearSelection();
        loadAllMovies();
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
     * Navigates to the home screen.
     */
    @FXML
    public void goHome() {
        System.out.println("Navigating to Home...");
    }

    /**
     * Handles the action to view tickets.
     */
    @FXML
    public void viewTickets() {
        System.out.println("Viewing Tickets...");
    }

    /**
     * Handles the select action.
     */
    @FXML
    public void handleSelect() {
        System.out.println("Select action triggered...");
    }

    /**
     * Handles the dispose action.
     */
    @FXML
    public void handleDispose() {
        System.out.println("Dispose action triggered...");
    }
}
