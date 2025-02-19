package com.example.cinamacentermanagement.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

import com.example.cinamacentermanagement.model.Movie;


public class MovieDetailsModalController {

    @FXML
    private ImageView moviePoster;

    @FXML
    private Button approveButton;

    @FXML
    private TextArea movieSummary;

    @FXML
    private StackPane modalContainer;
    @FXML
    private Button cancelButton;  // Must match the fx:id in the FXML


    @FXML
    private Button modalCloseButton;

    @FXML
    private javafx.scene.control.Label movieTitle;

    @FXML
    private javafx.scene.control.Label movieGenre;

    private boolean approved = false;

    private Movie currentMovie; // <-- Store it here

    public void setMovieDetails(Movie movie) {
        this.currentMovie = movie;    // Save it for later use
        // Then populate the fields/UI:
        movieTitle.setText(movie.getTitle());
        movieGenre.setText("Genre: " + movie.getGenre());
        movieSummary.setText(movie.getSummary());

        try {
            moviePoster.setImage(new Image(movie.getPoster()));
        } catch (Exception e) {
            moviePoster.setImage(new Image("/images/default-poster.png")); 
        }
    }

    
    public boolean isApproved() {
        return approved;
    }



    @FXML
    private void handleClose() {
        Stage stage = (Stage) modalContainer.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleApprove() {
        // Open Step2 right here (non-modal)
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/cinamacentermanagement/fxml/cashierStep2.fxml")
            );
            Parent root = loader.load();
    
            CashierStep2Controller step2Controller = loader.getController();
            step2Controller.setSelectedMovie(currentMovie);
    
            Stage step2Stage = new Stage();
            step2Stage.setScene(new Scene(root));
            step2Stage.setTitle("Select Day, Session, and Hall");
            step2Stage.show(); // no .showAndWait(), no initModality()
    
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        // Now close *this* window if you like:
        Stage modalStage = (Stage) approveButton.getScene().getWindow();
        modalStage.close();
    }
    


    // This runs when user clicks "Cancel"
    @FXML
    private void onCancel() {
        approved = false;
        ((Stage) cancelButton.getScene().getWindow()).close();
    }
}