package com.example.cinamacentermanagement.dao;

import com.example.cinamacentermanagement.database.DatabaseConnection;
import com.example.cinamacentermanagement.model.ShoppingCart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class InvoiceDao {

    public void saveInvoice(List<ShoppingCart> cartItems, double total, double tax, String invoicePath, String ticketPath) {
        String query = "INSERT INTO Invoices (cart_id, invoice_date, total_amount, total_tax, invoice_pdf_path, ticket_pdf_path) " +
                       "VALUES (?, NOW(), ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            for (ShoppingCart item : cartItems) {
                statement.setInt(1, item.getCartId());
                statement.setDouble(2, total);
                statement.setDouble(3, tax);
                statement.setString(4, invoicePath);
                statement.setString(5, ticketPath);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
