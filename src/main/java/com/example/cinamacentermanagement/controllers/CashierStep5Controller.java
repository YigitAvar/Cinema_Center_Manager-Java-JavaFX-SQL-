package com.example.cinamacentermanagement.controllers;

import com.example.cinamacentermanagement.database.DatabaseConnection;
import com.example.cinamacentermanagement.model.ShoppingCart;
import com.example.cinamacentermanagement.model.TicketDetails;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.print.PrinterJob;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.io.File;
import java.io.IOException;

/**
 * Controller class for the cashier step 5 view.
 */
public class CashierStep5Controller {

    @FXML
    private TableView<ShoppingCart> cartTable;

    @FXML
    private TableColumn<ShoppingCart, String> itemColumn;

    @FXML
    private TableColumn<ShoppingCart, Integer> quantityColumn;

    @FXML
    private TableColumn<ShoppingCart, Double> priceColumn;

    @FXML
    private TableView<TicketDetails> ticketTable; // Table for displaying ticket details

    @FXML
    private Label totalLabel;

    @FXML
    private Button confirmButton;

    @FXML
    private Button downloadTicketButton;

    @FXML
    private TableColumn<TicketDetails, String> ticketMovieColumn;

    @FXML
    private TableColumn<TicketDetails, String> ticketSeatColumn;

    @FXML
    private TableColumn<TicketDetails, Double> ticketPriceColumn;

    @FXML
    private TableView<ShoppingCart> invoiceTable;

    @FXML
    private Label invoiceSummaryLabel; // Add this to show subtotal, tax, and total.

    @FXML
    private Button downloadInvoiceButton;

    private ObservableList<ShoppingCart> cartItems;

    private double totalPrice;
    private double totalTax;
    private double discount;

    /**
     * Generates an invoice PDF and displays it in a new stage.
     *
     * @param stage the stage to display the invoice
     */
    private void generateInvoicePDF(Stage stage) {
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        String htmlContent = generateInvoiceHTML();
    
        // Debug: Print HTML content to verify it's generated correctly
        System.out.println("Generated Invoice HTML:\n" + htmlContent);
    
        webEngine.loadContent(htmlContent);
    
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == javafx.concurrent.Worker.State.SUCCEEDED) {
                // Ensure the content is fully loaded
                System.out.println("Invoice HTML loaded successfully.");
    
                Scene scene = new Scene(webView, 800, 600);
                stage.setScene(scene);
                stage.show();
    
                PrinterJob printerJob = PrinterJob.createPrinterJob();
                if (printerJob == null) {
                    showErrorMessage("Could not initialize the Printer Job.");
                    return;
                }
    
                if (printerJob.showPrintDialog(stage)) {
                    webView.getEngine().print(printerJob); // Print the content
                    printerJob.endJob();
                    showSuccessMessage("Invoice PDF saved successfully.");
                } else {
                    showErrorMessage("Printing was canceled.");
                }
            }
        });
    }
    
    
    private String generateTicketHTML() {
        StringBuilder html = new StringBuilder();
        html.append("<html>")
            .append("<head>")
            .append("<style>")
            .append("body { font-family: Arial, sans-serif; margin: 20px; }")
            .append("h1 { font-size: 24px; font-weight: bold; text-align: center; }")
            .append("p { font-size: 14px; text-align: center; }")
            .append("table { width: 80%; margin: auto; border-collapse: collapse; margin-top: 20px; }")
            .append("th, td { border: 1px solid #ddd; padding: 8px; text-align: center; }")
            .append("th { background-color: #f2f2f2; font-weight: bold; }")
            .append("</style>")
            .append("</head>")
            .append("<body>")
            .append("<h1>Cinema Ticket</h1>")
            .append("<p>Thank you for visiting our cinema!</p>")
            .append("<table>")
            .append("<tr>")
            .append("<th>Movie</th>")
            .append("<th>Hall</th>")
            .append("<th>Seat</th>")
            .append("<th>Price</th>")
            .append("</tr>");
        ObservableList<TicketDetails> ticketDetailsList = getTicketDetailsList();
        for (TicketDetails ticket : ticketDetailsList) {
            html.append("<tr>")
                .append("<td>").append(ticket.getMovieName()).append("</td>")
                .append("<td>").append(ticket.getHallName()).append("</td>")
                .append("<td>").append(ticket.getSeatInfo()).append("</td>")
                .append("<td>").append(String.format("$%.2f", ticket.getPrice())).append("</td>")
                .append("</tr>");
        }
        html.append("</table>")
            .append("<p style='margin-top: 20px;'>Enjoy your movie!</p>")
            .append("</body>")
            .append("</html>");
        return html.toString();
    }

    /**
     * Generates a ticket PDF and displays it in a new stage.
     *
     * @param stage the stage to display the ticket
     */
    private void generateTicketPDF(Stage stage) {
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        String htmlContent = generateTicketHTML();
        webEngine.loadContent(htmlContent);
        Scene scene = new Scene(webView, 600, 400);
        stage.setScene(scene);
        stage.show();
        PrinterJob printerJob = PrinterJob.createPrinterJob();
        if (printerJob == null) {
            showErrorMessage("Could not initialize the Printer Job.");
            return;
        }
        if (printerJob.showPrintDialog(stage)) {
            webEngine.print(printerJob);
            printerJob.endJob();
            showSuccessMessage("Ticket PDF saved successfully.");
        } else {
            showErrorMessage("Printing was canceled.");
        }
    }

    /**
     * Generates the HTML content for the invoice.
     *
     * @return the HTML content as a string
     */
    private String generateInvoiceHTML() {
        StringBuilder html = new StringBuilder();
        html.append("<html>")
            .append("<head>")
            .append("<style>")
            .append("body { font-family: Arial, sans-serif; margin: 20px; }")
            .append("h1 { font-size: 24px; font-weight: bold; }")
            .append("table { width: 100%; border-collapse: collapse; margin-top: 20px; }")
            .append("th, td { border: 1px solid #ddd; padding: 8px; }")
            .append("th { background-color: #f2f2f2; text-align: left; }")
            .append("</style>")
            .append("</head>")
            .append("<body>")
            .append("<h1>Cinema Invoice</h1>")
            .append("<p>Thank you for your purchase!</p>")
            .append("<table>")
            .append("<tr>")
            .append("<th>Item</th>")
            .append("<th>Quantity</th>")
            .append("<th>Price</th>")
            .append("</tr>");
        double subtotal = 0.0;
        double tax = 0.0;
        for (ShoppingCart item : cartItems) {
            double itemPrice = item.getPrice() * item.getQuantity();
            if (item.getProductId() == null) {
                double discountedPrice = itemPrice * (1 - discount);
                discount += itemPrice - discountedPrice;
                itemPrice = discountedPrice;
                tax += itemPrice * 0.20;
            } else {
                tax += itemPrice * 0.10;
            }
            subtotal += itemPrice;
            html.append("<tr>")
                .append("<td>").append(item.getProductId() == null ? "Ticket - " + item.getSeatName() : item.getProductName()).append("</td>")
                .append("<td>").append(item.getQuantity()).append("</td>")
                .append("<td>").append(String.format("$%.2f", itemPrice)).append("</td>")
                .append("</tr>");
        }
        html.append("</table>")
            .append("<p><strong>Subtotal:</strong> $").append(String.format("%.2f", subtotal)).append("</p>")
            .append("<p><strong>Discount:</strong> $").append(String.format("%.2f", discount)).append("</p>")
            .append("<p><strong>Tax:</strong> $").append(String.format("%.2f", tax)).append("</p>")
            .append("<p><strong>Total:</strong> $").append(String.format("%.2f", totalPrice)).append("</p>")
            .append("</body>")
            .append("</html>");
        return html.toString();
    }

    /**
     * Initializes the data for the controller.
     *
     * @param cartItems the list of cart items
     * @param discount the discount amount
     * @param tax the tax amount
     * @param totalPrice the total price
     */
    public void initializeData(ObservableList<ShoppingCart> cartItems, double discount, double tax, double totalPrice) {
        System.out.println("Initializing data...");
        System.out.println("Cart Items: " + cartItems.size());
        for (ShoppingCart item : cartItems) {
            System.out.println(item);
        }
        this.cartItems = cartItems;
        this.discount = discount;
        this.totalTax = tax;
        this.totalPrice = totalPrice;
        displayTicketDetails();
        displayInvoiceDetails();
    }

    /**
     * Displays the ticket details in the table.
     */
    private void displayTicketDetails() {
        ObservableList<TicketDetails> ticketDetailsList = getTicketDetailsList();
        ticketMovieColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getMovieName()));
        ticketSeatColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getSeatInfo()));
        ticketPriceColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getPrice()));
        TableColumn<TicketDetails, String> hallColumn = new TableColumn<>("Hall");
        hallColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getHallName()));
        ticketTable.getColumns().add(hallColumn);
        ticketTable.setItems(ticketDetailsList);
    }

    /**
     * Retrieves the list of ticket details.
     *
     * @return the list of ticket details
     */
    private ObservableList<TicketDetails> getTicketDetailsList() {
        ObservableList<TicketDetails> ticketDetailsList = FXCollections.observableArrayList();
        for (ShoppingCart item : cartItems) {
            if (item.getProductId() == null) {
                int sessionId = item.getSessionId();
                TicketDetails ticketDetails = fetchTicketDetails(sessionId, item.getSeatId());
                if (ticketDetails == null) {
                    System.out.println("No ticket details found.");
                }
                if (ticketDetails != null) {
                    ticketDetailsList.add(ticketDetails);
                }
            }
        }
        return ticketDetailsList;
    }

    /**
     * Fetches the ticket details for a given session and seat.
     *
     * @param sessionId the session ID
     * @param seatId the seat ID
     * @return the ticket details
     */
    private TicketDetails fetchTicketDetails(int sessionId, Integer seatId) {
        String query = """
            SELECT m.title, h.name AS hall_name, s.row_num, s.seat_number, m.ticket_price
            FROM Sessions ss
            INNER JOIN Movies m ON ss.movie_id = m.movie_id
            INNER JOIN Halls h ON ss.hall_id = h.hall_id
            LEFT JOIN Seats s ON s.seat_id = ?
            WHERE ss.session_id = ?
        """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, seatId);
            statement.setInt(2, sessionId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String movieName = resultSet.getString("title");
                String hallName = resultSet.getString("hall_name");
                String seatInfo = seatId != null
                    ? "Row " + resultSet.getInt("row_num") + " Seat " + resultSet.getInt("seat_number")
                    : "N/A";
                double price = resultSet.getDouble("ticket_price");
                return new TicketDetails(movieName, hallName, seatInfo, price);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Displays the invoice details in the table.
     */
    private void displayInvoiceDetails() {
        itemColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
            cellData.getValue().getProductId() == null
            ? "Ticket - "
            : cellData.getValue().getItemName()));
        quantityColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getQuantity()));
        priceColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getPrice()));
        invoiceTable.setItems(FXCollections.observableArrayList(cartItems));
        double subtotal = 0.0;
        double tax = 0.0;
        for (ShoppingCart item : cartItems) {
            double itemPrice = item.getPrice() * item.getQuantity();
            subtotal += itemPrice;
            if (item.getProductId() == null) {
                tax += (itemPrice * 0.20);
            } else {
                tax += (itemPrice * 0.10);
            }
        }
        double total = subtotal + tax;
        invoiceSummaryLabel.setText(String.format("Subtotal: $%.2f | Tax: $%.2f | Total: $%.2f", subtotal, tax, total));
    }

    /**
     * Configures the cart table with item, quantity, and price columns.
     */
    private void configureCartTable() {
        itemColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getItemName()));
        quantityColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getQuantity()));
        priceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getPrice()));
        cartTable.setItems(cartItems);
    }

    /**
     * Updates the summary label with the total price and tax.
     */
    private void updateSummary() {
        totalLabel.setText(String.format("Total: $%.2f | Tax: $%.2f", totalPrice, totalTax));
    }

    /**
     * Handles the finish button click event.
     */
    @FXML
    private void onFinish() {
        try {
            Stage stage = new Stage();
            generateInvoicePDF(stage);
            generateTicketPDF(stage);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Failed to save documents. Please try again.");
        }
    }

    /**
     * Displays a success message.
     *
     * @param message the message to display
     */
    private void showSuccessMessage(String message) {
        System.out.println(message);
    }

    /**
     * Displays an error message.
     *
     * @param message the message to display
     */
    private void showErrorMessage(String message) {
        System.out.println(message);
    }
}