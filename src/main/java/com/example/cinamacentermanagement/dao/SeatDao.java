package com.example.cinamacentermanagement.dao;

import com.example.cinamacentermanagement.database.DatabaseConnection;
import com.example.cinamacentermanagement.model.Seat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatDao {

    public List<Seat> getSeatsByHallId(int hallId) {
        List<Seat> seats = new ArrayList<>();
        String query = "SELECT * FROM Seats WHERE hall_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, hallId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Seat seat = new Seat(
                    resultSet.getInt("seat_id"),
                    resultSet.getInt("hall_id"),
                    resultSet.getInt("row_num"),
                    resultSet.getInt("seat_number"),
                    resultSet.getBoolean("is_occupied")
                );
                seats.add(seat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seats;
    }

    public boolean updateSeatOccupation(int seatId, boolean isOccupied) {
        String query = "UPDATE Seats SET is_occupied = ? WHERE seat_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setBoolean(1, isOccupied);
            statement.setInt(2, seatId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * Check if a seat is available (not occupied).
     *
     * @param seatId The ID of the seat to check.
     * @return true if the seat is available, false otherwise.
     */
    public boolean checkSeatAvailability(int seatId) {
        String query = "SELECT is_occupied FROM Seats WHERE seat_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, seatId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return !resultSet.getBoolean("is_occupied");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Assume unavailable if any error occurs
    }
}
