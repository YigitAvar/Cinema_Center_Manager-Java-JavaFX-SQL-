package com.example.cinamacentermanagement.model;

import java.sql.*;

import com.example.cinamacentermanagement.database.DatabaseConnection;

public class PricingUtil {

    public static double calculatePrice(double basePrice, int age) {
        double discountPercentage = getDiscountPercentage(age);
        double discountedPrice = basePrice * (1 - (discountPercentage / 100));
        double tax = discountedPrice * 0.10; // 10% tax
        return discountedPrice + tax;
    }

    private static double getDiscountPercentage(int age) {
        String query = "SELECT discount_percentage FROM AgeBasedDiscounts WHERE ? BETWEEN age_min AND age_max";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, age);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getDouble("discount_percentage");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0; // No discount if no range is matched
    }
}
