package com.example.cinamacentermanagement.controllers;

import com.example.cinamacentermanagement.MoviePrice;
import com.example.cinamacentermanagement.database.DatabaseConnection;
import com.example.cinamacentermanagement.model.Movie;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import java.sql.*;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Controller class for managing movie pricing operations.
 */
public class PricingController {

    @FXML
    private TableView<Movie> moviesTable;
    @FXML
    private TableColumn<Movie, Integer> movieIdColumn;
    @FXML
    private TableColumn<Movie, String> titleColumn; // Add this line
    @FXML
    private TableColumn<Movie, String> genreColumn;
    @FXML
    private TableColumn<Movie, String> summaryColumn;
    @FXML
    private TableColumn<Movie, Double> ticketPriceColumn;
    @FXML
    private ImageView posterImageView;
    @FXML
    private Button viewMoviesButton;
    @FXML
    private Button updateTicketPricesButton;

    private ObservableList<Movie> movieList = FXCollections.observableArrayList();
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
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        movieIdColumn.setCellValueFactory(new PropertyValueFactory<>("movieId"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title")); // Corrected line
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        summaryColumn.setCellValueFactory(new PropertyValueFactory<>("summary"));
        ticketPriceColumn.setCellValueFactory(new PropertyValueFactory<>("ticketPrice"));

        moviesTable.setItems(movieList);
        moviesTable.setOnMouseClicked(this::handleMovieSelection);

        try {
            connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            showAlert("Database Connection Failed", "Unable to connect to the database.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }

        viewMoviesButton.setOnAction(_ -> {
            try {
                loadMovies();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to load movies.", Alert.AlertType.ERROR);
            }
        });

        updateTicketPricesButton.setOnAction(_ -> {
            try {
                updateTicketPrices();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to update ticket prices.", Alert.AlertType.ERROR);
            }
        });
    }

    /**
     * Loads movies from the database and displays them in the table view.
     *
     * @throws SQLException if a database access error occurs
     */
    private void loadMovies() throws SQLException {

        checkConnection();
        movieList.clear();

        try {
            Statement statement = connection.createStatement();
            String query = "SELECT movie_id, title, genre, summary, ticket_price, poster FROM Movies";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                MoviePrice movie = new MoviePrice(
                        resultSet.getInt("movie_id"),
                        resultSet.getString("title"),
                        resultSet.getString("genre"),
                        resultSet.getString("summary"),
                        resultSet.getDouble("ticket_price"),
                        resultSet.getString("poster")
                );
                movieList.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the selection of a movie from the table view and displays its poster.
     *
     * @param event the mouse event
     */
    private void handleMovieSelection(MouseEvent event) {
        Movie selectedMovie = moviesTable.getSelectionModel().getSelectedItem();
        if (selectedMovie != null) {
            Image posterImage = new Image(selectedMovie.getPoster());
            posterImageView.setImage(posterImage);
        }
    }

    /**
     * Updates the ticket prices for a selected movie.
     *
     * @throws SQLException if a database access error occurs
     */
    private void updateTicketPrices() throws SQLException {
        checkConnection();

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Update Ticket Prices");
        dialog.setHeaderText("Enter Movie Name to Update Price");
        dialog.setContentText("Movie Name:");

        dialog.showAndWait().ifPresent(title -> {
            try {
                // Check if the movie exists
                String checkQuery = "SELECT * FROM Movies WHERE title = ?";
                PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setString(1, title);
                ResultSet resultSet = checkStatement.executeQuery();

                if (!resultSet.next()) {
                    showAlert("Movie Not Found", "The movie \"" + title + "\" does not exist.", Alert.AlertType.ERROR);
                    return;
                }

                TextInputDialog priceDialog = new TextInputDialog();
                priceDialog.setTitle("Update Ticket Prices");
                priceDialog.setHeaderText("Enter New Price for " + title);
                priceDialog.setContentText("New Ticket Price:");

                priceDialog.showAndWait().ifPresent(price -> {
                    try {
                        double newPrice = Double.parseDouble(price);
                        if (newPrice < 0) {
                            showAlert("Validation Error", "Ticket price must can't be negative.", Alert.AlertType.ERROR);
                            return;
                        }
                        if (newPrice == 0) {
                            showAlert("Validation Error", "Ticket price can't be zero.", Alert.AlertType.ERROR);
                            return;
                        }

                        String updateQuery = "UPDATE Movies SET ticket_price = ? WHERE title = ?";
                        PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                        preparedStatement.setDouble(1, newPrice);
                        preparedStatement.setString(2, title);

                        int rowsUpdated = preparedStatement.executeUpdate();
                        if (rowsUpdated > 0) {
                            showAlert("Pricing Update", "Ticket price for \"" + title + "\" updated successfully.", Alert.AlertType.INFORMATION);
                        } else {
                            showAlert("Pricing Update", "Failed to update ticket price.", Alert.AlertType.ERROR);
                        }
                    } catch (SQLException | NumberFormatException e) {
                        e.printStackTrace();
                        showAlert("Error", "Failed to update ticket prices.", Alert.AlertType.ERROR);
                    }
                });
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to check movie existence.", Alert.AlertType.ERROR);
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