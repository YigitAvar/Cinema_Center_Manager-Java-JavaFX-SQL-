package com.example.cinamacentermanagement;

import com.example.cinamacentermanagement.model.Movie;
import javafx.beans.property.*;

/**
 * Represents a movie with its price details in the cinema center management system.
 */
public class MoviePrice extends Movie {
    private final IntegerProperty movieId;
    private final StringProperty title;
    private final StringProperty genre;
    private final StringProperty summary;
    private final DoubleProperty ticketPrice;
    private final StringProperty poster;

    /**
     * Constructs a new MoviePrice object with the specified details.
     *
     * @param movieId the ID of the movie
     * @param title the title of the movie
     * @param genre the genre of the movie
     * @param summary the summary of the movie
     * @param ticketPrice the ticket price of the movie
     * @param poster the URL of the movie poster
     */
    public MoviePrice(int movieId, String title, String genre, String summary, double ticketPrice, String poster) {
        this.movieId = new SimpleIntegerProperty(movieId);
        this.title = new SimpleStringProperty(title);
        this.genre = new SimpleStringProperty(genre);
        this.summary = new SimpleStringProperty(summary);
        this.ticketPrice = new SimpleDoubleProperty(ticketPrice);
        this.poster = new SimpleStringProperty(poster);
    }

    /**
     * Returns the ID of the movie.
     *
     * @return the movie ID
     */
    public int getMovieId() {
        return movieId.get();
    }

    /**
     * Returns the movie ID property.
     *
     * @return the movie ID property
     */
    public IntegerProperty movieIdProperty() {
        return movieId;
    }

    /**
     * Returns the title of the movie.
     *
     * @return the movie title
     */
    public String getTitle() {
        return title.get();
    }

    /**
     * Returns the title property.
     *
     * @return the title property
     */
    public StringProperty titleProperty() {
        return title;
    }

    /**
     * Returns the genre of the movie.
     *
     * @return the movie genre
     */
    public String getGenre() {
        return genre.get();
    }

    /**
     * Returns the genre property.
     *
     * @return the genre property
     */
    public StringProperty genreProperty() {
        return genre;
    }

    /**
     * Returns the summary of the movie.
     *
     * @return the movie summary
     */
    public String getSummary() {
        return summary.get();
    }

    /**
     * Returns the summary property.
     *
     * @return the summary property
     */
    public StringProperty summaryProperty() {
        return summary;
    }

    /**
     * Returns the ticket price of the movie.
     *
     * @return the ticket price
     */
    public double getTicketPrice() {
        return ticketPrice.get();
    }

    /**
     * Returns the ticket price property.
     *
     * @return the ticket price property
     */
    public DoubleProperty ticketPriceProperty() {
        return ticketPrice;
    }

    /**
     * Returns the URL of the movie poster.
     *
     * @return the poster URL
     */
    public String getPoster() {
        return poster.get();
    }

    /**
     * Returns the poster property.
     *
     * @return the poster property
     */
    public StringProperty posterProperty() {
        return poster;
    }
}