package com.example.cinamacentermanagement.dao;

import com.example.cinamacentermanagement.database.DatabaseConnection;
import com.example.cinamacentermanagement.model.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDao {

    public List<Movie> getAllMovies(){
        List<Movie> movies = new ArrayList<>();
        String query = "SELECT * FROM Movies";
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Movie movie = new Movie();
                movie.setMovieId(resultSet.getInt("movie_id"));
                movie.setTitle(resultSet.getString("title"));
                movie.setGenre(resultSet.getString("genre"));
                movie.setPoster(resultSet.getString("poster"));
                movie.setSummary(resultSet.getString("summary"));
                movies.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movies;
    }
    public List<String> getAllGenres() {
        List<String> genres = new ArrayList<>();
        String query = "SELECT DISTINCT genre FROM Movies";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                genres.add(resultSet.getString("genre"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return genres;
    }

    public List<Movie> searchByGenreAndQuery(String genre, String query) {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM Movies WHERE genre = ? AND (title LIKE ? OR summary LIKE ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, genre);
            statement.setString(2, "%" + query + "%");
            statement.setString(3, "%" + query + "%");

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Movie movie = new Movie();
                movie.setMovieId(resultSet.getInt("movie_id"));
                movie.setTitle(resultSet.getString("title"));
                movie.setGenre(resultSet.getString("genre"));
                movie.setPoster(resultSet.getString("poster"));
                movie.setSummary(resultSet.getString("summary"));
                movies.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }
    public List<Movie> searchByGenre(String genre) {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM Movies WHERE genre = ?";
    
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
    
            statement.setString(1, genre);
    
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Movie movie = new Movie();
                movie.setMovieId(resultSet.getInt("movie_id"));
                movie.setTitle(resultSet.getString("title"));
                movie.setGenre(resultSet.getString("genre"));
                movie.setPoster(resultSet.getString("poster"));

                movie.setSummary(resultSet.getString("summary"));
                movies.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    public List<Movie> searchByPartialTitle(String partialTitle) {
        List<Movie> movies = new ArrayList<>();
        String query = "SELECT * FROM Movies WHERE title LIKE ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, "%" + partialTitle + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Movie movie = new Movie();
                movie.setMovieId(resultSet.getInt("movie_id"));
                movie.setTitle(resultSet.getString("title"));
                movie.setPoster(resultSet.getString("poster"));
                movie.setGenre(resultSet.getString("genre"));
                movie.setSummary(resultSet.getString("summary"));
                movies.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    public Movie searchByFullTitle(String title) {
        Movie movie = null;
        String query = "SELECT * FROM Movies WHERE title = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, title);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                movie = new Movie();
                movie.setMovieId(resultSet.getInt("movie_id"));
                movie.setTitle(resultSet.getString("title"));
                movie.setPoster(resultSet.getString("poster"));
                movie.setGenre(resultSet.getString("genre"));
                movie.setSummary(resultSet.getString("summary"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movie;
    }

    
}
