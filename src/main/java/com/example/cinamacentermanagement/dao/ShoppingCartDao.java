package com.example.cinamacentermanagement.dao;


import com.example.cinamacentermanagement.database.DatabaseConnection;
import com.example.cinamacentermanagement.model.ShoppingCart;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCartDao {

    public boolean addToCart(Integer sessionId, Integer seatId, Integer productId, int quantity, int customerId, double pricePerUnit) {
        String selectQuery = """
            SELECT quantity, price FROM ShoppingCart 
            WHERE session_id = ? AND seat_id <=> ? AND product_id <=> ? AND customer_id = ?
        """;
        String updateQuery = """
            UPDATE ShoppingCart 
            SET quantity = quantity + ?, price = price + ?
            WHERE session_id = ? AND seat_id <=> ? AND product_id <=> ? AND customer_id = ?
        """;
        String insertQuery = """
            INSERT INTO ShoppingCart (session_id, seat_id, product_id, quantity, customer_id, price)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
    
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Check if the item already exists
            try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
                selectStmt.setObject(1, sessionId);
                selectStmt.setObject(2, seatId); // Nullable seat ID
                selectStmt.setObject(3, productId); // Nullable product ID
                selectStmt.setInt(4, customerId);
    
                ResultSet resultSet = selectStmt.executeQuery();
                if (resultSet.next()) {
                    // Item exists, update quantity and price
                    int existingQuantity = resultSet.getInt("quantity");
                    double existingPrice = resultSet.getDouble("price");
    
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, quantity);
                        updateStmt.setDouble(2, pricePerUnit * quantity);
                        updateStmt.setObject(3, sessionId);
                        updateStmt.setObject(4, seatId);
                        updateStmt.setObject(5, productId);
                        updateStmt.setInt(6, customerId);
    
                        return updateStmt.executeUpdate() > 0;
                    }
                } else {
                    // Item does not exist, insert new row
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                        insertStmt.setObject(1, sessionId);
                        insertStmt.setObject(2, seatId);
                        insertStmt.setObject(3, productId);
                        insertStmt.setInt(4, quantity);
                        insertStmt.setInt(5, customerId);
                        insertStmt.setDouble(6, pricePerUnit * quantity);
    
                        return insertStmt.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
          
    public int getCustomerID(String email) {
        String query = "SELECT customer_id FROM Customers WHERE email = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("customer_id"); // Return the customer_id if found
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return null if no customer is found
    }
    private double getAgeBasedDiscount(int age) {
    String query = "SELECT discount_percentage FROM AgeBasedDiscounts WHERE age_min <= ? AND age_max >= ?";

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement stmt = connection.prepareStatement(query)) {

        stmt.setInt(1, age);
        stmt.setInt(2, age);
        ResultSet resultSet = stmt.executeQuery();

        if (resultSet.next()) {
            return resultSet.getDouble("discount_percentage");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return 0.0;
}

   public boolean saveCustomerInfoWithDiscount(String firstName, String lastName, String email, int age) {
    String checkCustomerQuery = "SELECT customer_id, age FROM Customers WHERE email = ?";
    String insertQuery = "INSERT INTO Customers (first_name, last_name, email, age) VALUES (?, ?, ?, ?)";
    String updateQuery = "UPDATE Customers SET age = ? WHERE customer_id = ?";
    
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement checkStmt = connection.prepareStatement(checkCustomerQuery)) {
        checkStmt.setString(1, email);
        ResultSet customerResultSet = checkStmt.executeQuery();

        if (customerResultSet.next()) {
            // Customer exists, check the age
            int customerId = customerResultSet.getInt("customer_id");
            int existingAge = customerResultSet.getInt("age");


            if (existingAge != age) {
                // Update customer's  age if it differs
                try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                    updateStmt.setInt(1, age);
                    updateStmt.setInt(2, customerId);
                    return updateStmt.executeUpdate() > 0;
                }
            }
            return true;
           
        } else {
            // Insert new customer if they don't exist
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                insertStmt.setString(1, firstName);
                insertStmt.setString(2, lastName);
                insertStmt.setString(3, email);
                insertStmt.setInt(4, age);
                return insertStmt.executeUpdate() > 0;
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}

    
    



public List<ShoppingCart> getCartItems(int customerId, int sessionId) {
    List<ShoppingCart> cartItems = new ArrayList<>();
    String query = "SELECT c.cart_id, c.session_id, c.seat_id, c.product_id, c.quantity, c.price, " +
                   "       CONCAT('Row ', s.row_num, ' Seat ', s.seat_number) AS seat_name, " +
                   "       p.name AS product_name " +
                   "FROM ShoppingCart c " +
                   "LEFT JOIN Seats s ON c.seat_id = s.seat_id " +
                   "LEFT JOIN Products p ON c.product_id = p.product_id " +
                   "WHERE c.customer_id = ? AND c.session_id = ?";

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(query)) {

        // Set parameters for the query
        statement.setInt(1, customerId);
        statement.setInt(2, sessionId); // Fix: Add the missing session_id parameter

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            Integer seatId = resultSet.getObject("seat_id") != null ? resultSet.getInt("seat_id") : null;
            Integer productId = resultSet.getObject("product_id") != null ? resultSet.getInt("product_id") : null;

            ShoppingCart cartItem = new ShoppingCart(
                resultSet.getInt("cart_id"),
                resultSet.getInt("session_id"),
                seatId,
                productId,
                resultSet.getInt("quantity"),
                customerId,
                resultSet.getDouble("price"),
                resultSet.getString("seat_name"), // Seat name from query
                resultSet.getString("product_name") // Product name from query
            );
            cartItems.add(cartItem);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return cartItems;
}
   public boolean addOrUpdateProductInCart(Integer sessionId, Integer seatId, Integer productId, int quantity, int customerId, double pricePerUnit) {
        String query = """
            INSERT INTO ShoppingCart (session_id, seat_id, product_id, quantity, customer_id, price)
            VALUES (?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE 
                quantity = quantity + VALUES(quantity),
                price = price + VALUES(price)
        """;
    
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
    
            statement.setObject(1, sessionId); // Nullable session ID
            statement.setObject(2, seatId);    // Nullable seat ID
            statement.setObject(3, productId);// Nullable product ID
            statement.setInt(4, quantity);    // Quantity to add
            statement.setInt(5, customerId); // Customer ID
            statement.setDouble(6, pricePerUnit * quantity); // Total price for this addition
    
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
}
