
package com.example.cinamacentermanagement.controllers;

import com.example.cinamacentermanagement.dao.ProductDao;
import com.example.cinamacentermanagement.dao.ShoppingCartDao;
import com.example.cinamacentermanagement.database.DatabaseConnection;
import com.example.cinamacentermanagement.model.Movie;
import com.example.cinamacentermanagement.model.Product;
import com.example.cinamacentermanagement.model.Session;
import com.example.cinamacentermanagement.model.ShoppingCart;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

/**
 * Controller class for the cashier step 4 view.
 */
public class CashierStep4Controller {

    @FXML
    private TextField firstNameField, lastNameField, dateOfBirthField, emailField;

    @FXML
    private Button verifyDiscountButton;
    @FXML
    private Button approvePaymentButton;

    @FXML
    private TableView<ShoppingCart> cartTable;

    @FXML
    private TableColumn<ShoppingCart, String> itemColumn;

    @FXML
    private TableColumn<ShoppingCart, Integer> quantityColumn;

    @FXML
    private TableColumn<ShoppingCart, Double> priceColumn;

    @FXML
    private String email; // Customer ID associated with the shopping cart

    @FXML
    private Label totalLabel;

    @FXML
    private VBox beverageList;

    @FXML
    private VBox biscuitList;

    @FXML
    private VBox toyList;
    @FXML
    private GridPane productsGridPane;
    @FXML
    private VBox cartSummaryList;

    private int sessionId;
    private int customerId;
    private int customerAge;
    private final ShoppingCartDao cartDao = new ShoppingCartDao();
    private final ProductDao productDao = new ProductDao();

    private ObservableList<ShoppingCart> cartItems = FXCollections.observableArrayList();
    private double total = 0.0;
    private double discount = 0.0;

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        // Disable manual input for the DatePicker
        dateOfBirthPicker.setEditable(false);
    }

    /**
     * Sets up the data for the controller.
     *
     * @param cartItems the list of cart items
     * @param total     the total amount
     * @param sessionId the session ID
     */
    public void setupData(ObservableList<ShoppingCart> cartItems, double total, int sessionId) {
        this.cartItems = cartItems; // Include seats from Stage 3
        this.total = total; // Include the subtotal from Stage 3
        this.sessionId = sessionId; // Ensure session ID is preserved

        configureCartTable();
        loadAllProducts();
        updateTotal();
    }

    /**
     * Handles the approve payment button click event.
     */
    @FXML
    private void onApprovePaymentClicked() {
        try {
            double subtotal = 0.0;
            double tax = 0.0;

            if (customerId <= 0) {
                showAlert("Error", "Customer information is missing.");
                return;
            }

            if (customerAge < 0) {
                showAlert("Error", "Failed to retrieve customer age.");
                return;
            }

            boolean eligibleForDiscount = customerAge < 18 || customerAge > 60;

            // Calculate totals
            for (ShoppingCart item : cartItems) {
                double itemPrice = item.getPrice() * item.getQuantity();
                subtotal += itemPrice;

                if (item.getProductId() == null) { // It's a ticket
                    if (eligibleForDiscount) {
                        discount += itemPrice * 0.50; // 50% discount for eligible customers
                    }
                    double ticketPriceAfterDiscount = itemPrice - (eligibleForDiscount ? itemPrice * 0.50 : 0);
                    tax += ticketPriceAfterDiscount * 0.20; // 20% tax on discounted ticket price
                } else {
                    tax += itemPrice * 0.10; // 10% tax on products
                }
            }

            double totalPrice = subtotal - discount + tax;

            // Add ticket details to the Tickets table
            for (ShoppingCart item : cartItems) {
                if (item.getProductId() == null) { // Only save ticket items
                    boolean addedToTickets = saveTicketToDatabase(customerId, item.getSessionId(), totalPrice, discount, tax);
                    if (!addedToTickets) {
                        showAlert("Error", "Failed to save ticket details to the database.");
                        return;
                    }
                }
            }

            // Add to Revenue table
            boolean addedToRevenue = addToRevenueTable(totalPrice, tax);
            if (!addedToRevenue) {
                showAlert("Error", "Failed to record payment in revenue.");
                return;
            }

            // Show success message and transition to Stage 5
            showAlert("Payment Approved", "The payment was successfully processed!");

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinamacentermanagement/fxml/cashierStep5.fxml"));
                Parent root = loader.load();

                CashierStep5Controller controller = loader.getController();
                controller.initializeData(cartItems, discount, tax, totalPrice);

                Stage stage = (Stage) approvePaymentButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                showAlert("Error", "Failed to load the next stage. Please try again.");
                e.printStackTrace();
                return;
            }

            cartItems.clear();
            updateTotal();
            cartTable.getItems().clear();

        } catch (Exception e) {
            showAlert("Error", "An error occurred while processing the payment.");
            e.printStackTrace();
        }
    }

    /**
     * Saves ticket details to the Tickets table in the database.
     *
     * @param customerId The ID of the customer.
     * @param sessionId  The session ID associated with the ticket.
     * @param totalPrice The total price of the ticket including discount and tax.
     * @param discount   The discount applied to the ticket.
     * @param tax        The tax amount for the ticket.
     * @return true if the ticket was saved successfully, false otherwise.
     */
    private boolean saveTicketToDatabase(int customerId, int sessionId, double totalPrice, double discount, double tax) {
        String insertQuery = """
                    INSERT INTO Tickets (customer_id, session_id, total_price, discount, tax, date_of_purchase)
                    VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertQuery)) {

            statement.setInt(1, customerId);
            statement.setInt(2, sessionId);
            statement.setDouble(3, totalPrice);
            statement.setDouble(4, discount);
            statement.setDouble(5, tax);
            statement.setObject(6, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Adds the total revenue and tax to the Revenue table.
     *
     * @param totalRevenue The total revenue amount.
     * @param totalTax     The total tax amount.
     * @return true if the revenue was added successfully, false otherwise.
     */
    private boolean addToRevenueTable(double totalRevenue, double totalTax) {
        String query = "INSERT INTO Revenue (total_revenue, total_tax, date) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setDouble(1, totalRevenue); // Total price
            statement.setDouble(2, totalTax);     // Total tax
            statement.setDate(3, java.sql.Date.valueOf(LocalDate.now())); // Current date

            return statement.executeUpdate() > 0; // Returns true if the insertion was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Applies an age-based discount to the cart items.
     *
     * @param age The age of the customer.
     */
    public void applyAgeDiscount(int age) {
        // Check if the customer is eligible for a discount
        boolean isEligibleForDiscount = age < 18 || age > 60;

        for (ShoppingCart item : cartItems) {
            // Apply the discount only to cinema tickets (seat-related items)
            if (item.getProductId() == null && isEligibleForDiscount) {
                double oldPrice = item.getPrice();
                double discountedPrice = oldPrice * 0.50; // Apply a 50% discount
                item.setPrice(discountedPrice);
            }
        }

        // Recalculate the total after applying the discount
        updateTotal();
    }

    /**
     * Configures the cart table with item, quantity, and price columns.
     */
    private void configureCartTable() {
        itemColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getItemName()));
        quantityColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getQuantity()));
        priceColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getPrice()));

        cartTable.setItems(cartItems); // Ensure cartTable is linked to cartItems
    }

    /**
     * Populates the product lists for different categories.
     */
    private void populateProductLists() {
        populateCategory(beverageList, "Beverage");
        populateCategory(biscuitList, "Biscuit");
        populateCategory(toyList, "Toy");
    }

    /**
     * Populates a specific category with products.
     *
     * @param container The container to add the products to.
     * @param category  The category of products to populate.
     */
    private void populateCategory(VBox container, String category) {
        List<Product> products = productDao.getProductsByCategory(category);
        for (Product product : products) {
            HBox productCard = createProductCard(product);
            container.getChildren().add(productCard);
        }
    }

    /**
     * Creates a product card for a given product.
     *
     * @param product The product to create a card for.
     * @return The HBox containing the product card.
     */
    private HBox createProductCard(Product product) {
        ImageView productImage = new ImageView();
        try {
            System.out.println("Image URL: " + product.getImageUrl());
            // Use the URL from the database to load the image
            Image img = new Image(product.getImageUrl(), true); // true for background loading
            productImage.setImage(img);
        } catch (IllegalArgumentException e) {
            System.out.println("Failed to load remote image: " + product.getImageUrl());
            // Provide a fallback image
        }

        productImage.setFitWidth(180); // Match movie poster width
        productImage.setFitHeight(250); // Match movie poster height
        productImage.setPreserveRatio(true); // Preserve aspect ratio

        Label productName = new Label(product.getName());
        productName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-alignment: center;");

        Label productPrice = new Label(String.format("$%.2f", product.getPrice()));
        productPrice.setStyle("-fx-font-size: 14px; -fx-text-fill: gray; -fx-text-alignment: center;");

        Button addButton = new Button("Add");
        addButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-border-radius: 10; -fx-background-radius: 10;");
        addButton.setPrefWidth(153); // 85% of the product image width (180px * 0.85)
        addButton.setOnAction(event -> addProductToCart(product, addButton));

        HBox productCard = new HBox(15, productImage, productName, productPrice, addButton);
        productCard.setStyle("-fx-alignment: center; -fx-padding: 10; -fx-border-color: lightgray; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white;");

        return productCard;
    }

    /**
     * Loads all products and displays them in the grid pane.
     */
    private void loadAllProducts() {
        List<Product> products = productDao.getAllProducts();

        // Display all products in a single grid
        displayProducts(products, productsGridPane);
    }

    /**
     * Displays a list of products in a grid pane.
     *
     * @param products       The list of products to display.
     * @param targetGridPane The grid pane to display the products in.
     */
    private void displayProducts(List<Product> products, GridPane targetGridPane) {
        // Ensure the targetGridPane is not null
        if (targetGridPane == null) {
            throw new IllegalStateException("Target GridPane is null. Check your FXML fx:id mappings.");
        }

        targetGridPane.getChildren().clear(); // Clear existing content

        int row = 0, col = 0;
        for (Product product : products) {
            VBox productBox = createProductBox(product);
            targetGridPane.add(productBox, col, row);

            col++;
            if (col == 3) { // Change to match your desired column count
                col = 0;
                row++;
            }
        }
    }

    /**
     * Creates a product box for a given product.
     *
     * @param product The product to create a box for.
     * @return The VBox containing the product box.
     */
    private VBox createProductBox(Product product) {
        ImageView productImageView = new ImageView();
        try {
            String imageUrl = product.getImageUrl();
            System.out.println("Loading image: " + imageUrl); // Debug
            Image productImage = new Image(imageUrl, true); // true for background loading
            productImageView.setImage(productImage);
        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("Failed to load image: " + product.getImageUrl());
        }
        productImageView.setFitWidth(180);
        productImageView.setFitHeight(180);
        productImageView.setPreserveRatio(true);

        Label productName = new Label(product.getName());
        productName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        Label productPrice = new Label(String.format("$%.2f", product.getPrice()));
        productPrice.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");
        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        addToCartButton.setOnAction(event -> addProductToCart(product, addToCartButton));
        VBox productBox = new VBox(10, productImageView, productName, productPrice, addToCartButton);
        productBox.setStyle("-fx-alignment: center; -fx-padding: 10; -fx-border-color: lightgray; -fx-border-radius: 10;");
        return productBox;
    }

    /**
     * Adds a product to the cart.
     *
     * @param product   The product to add to the cart.
     * @param addButton The button that was clicked to add the product.
     */
    private void addProductToCart(Product product, Button addButton) {
        try {
            // Retrieve customer ID using email
            customerId = cartDao.getCustomerID(email);
            if (customerId == -1) {
                showAlert("Error", "Customer not found. Please verify the email.");
                return;
            }

            // Add or update the product in the database
            boolean added = cartDao.addToCart(
                    sessionId,                  // Session ID for movie seats
                    null,                       // Seat ID (not applicable for products)
                    product.getProductId(),     // Product ID
                    1,                          // Quantity
                    customerId,                 // Customer ID
                    product.getPrice()          // Price per unit
            );

            if (added) {
                // Refresh cart items from the database to ensure consistency
                updateCartItems();

                // Update the total price and UI
                updateTotal();
                cartTable.refresh(); // Refresh the UI table
                showAlert("Success", product.getName() + " added to cart successfully!");
            } else {
                showAlert("Error", "Failed to add " + product.getName() + " to cart.");
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred while adding the product to the cart.");
            e.printStackTrace();
        }
    }

    /**
     * Updates the cart items from the database.
     */
    private void updateCartItems() {
        cartItems.setAll(cartDao.getCartItems(customerId, sessionId)); // Fetch updated cart items
        updateTotal(); // Recalculate subtotal, discount, and tax
        cartTable.refresh(); // Refresh UI table
    }

    /**
     * Updates the total amount, discount, and tax.
     */
    private void updateTotal() {
        double subtotal = 0.0;
        double tax = 0.0;

        for (ShoppingCart item : cartItems) {
            double itemPrice = item.getPrice();
            subtotal += itemPrice * item.getQuantity(); // Consider quantity for each item

            if (item.getProductId() == null) { // It's a cinema ticket
                if (customerAge < 18 || customerAge > 60) { // Apply discount only if eligible
                    discount += (itemPrice * item.getQuantity()) * 0.50;
                    // Calculate 20% tax on discounted ticket price
                    tax += ((itemPrice * item.getQuantity()) - (itemPrice * item.getQuantity() * 0.50)) * 0.20;
                } else {
                    // Calculate 20% tax on full ticket price
                    tax += (itemPrice * item.getQuantity()) * 0.20;
                }
            } else {
                // Calculate 10% tax for products
                tax += (itemPrice * item.getQuantity()) * 0.10;
            }
        }

        // Calculate the final total
        double total = subtotal - discount + tax;

        // Update the total label
        totalLabel.setText(String.format(
                "Subtotal: $%.2f\nDiscount: $%.2f\nTax: $%.2f\nTotal: $%.2f",
                subtotal, discount, tax, total
        ));
    }
    
     
    @FXML
    private DatePicker dateOfBirthPicker;

    /**
     * Handles the verify discount button click event.
     */
    @FXML
    private void onVerifyDiscountClicked() {
        try {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            email = emailField.getText().trim();
            LocalDate dateOfBirth = dateOfBirthPicker.getValue();

            // Validate fields
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || dateOfBirth == null) {
                showAlert("Validation Error", "Please fill in all fields correctly.");
                return;
            }

            // Validate first and last name
            if (!isValidName(firstName) || !isValidName(lastName)) {
                showAlert("Validation Error", "Names should only contain letters, spaces, or special characters like '-' and '’'.");
                return;
            }

            // Validate email
            if (!isValidEmail(email)) {
                showAlert("Validation Error", "Invalid email format. Please enter a valid email address (e.g., example@domain.com).");
                return;
            }

            // Calculate age and check discount eligibility
            customerAge = Period.between(dateOfBirth, LocalDate.now()).getYears();

            discount = getAgeBasedDiscount(customerAge); // Discount percentage (e.g., 50%)
            System.out.println("DISCOUNT ====>>> " + discount);
            // Save customer information
            boolean saved = cartDao.saveCustomerInfoWithDiscount(firstName, lastName, email, customerAge);

            if (saved) {
                customerId = cartDao.getCustomerID(email); // Retrieve customer ID
                if (customerId == -1) {
                    showAlert("Error", "Customer not found. Please verify the email.");
                    return;
                }

                // Add all seats from cartItems to the shopping cart
                for (ShoppingCart item : cartItems) {
                    if (item.getProductId() == null) { // It's a seat (not a product)
                        double seatPrice = item.getPrice();
                        if (discount > 0) {
                            seatPrice = seatPrice * (1 - (discount / 100)); // Apply discount
                        }
                        item.setPrice(seatPrice);
                        // Add or update seat in the shopping cart
                        boolean added = cartDao.addToCart(
                            sessionId,
                            item.getSeatId(),
                            null,
                            item.getQuantity(),
                            customerId,
                            seatPrice
                        );

                        if (!added) {
                            showAlert("Error", "Failed to add seat to cart.");
                            return;
                        }
                    }
                }

                updateCartItems(); // Refresh cart items in UI
                // Notify about discount and refresh cart
                if (discount > 0) {
                    showAlert("Discount Applied", String.format("Customer is eligible for a %.0f%% discount.", discount));
                } else {
                    showAlert("No Discount", "Customer is not eligible for a discount.");
                }
            } else {
                showAlert("Error", "Failed to save customer details.");
            }
        } catch (Exception e) {
            showAlert("Error", "An unexpected error occurred. Please try again.");
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the ticket price for a given session ID.
     *
     * @param sessionId the session ID
     * @return the ticket price
     */
    public double getTicketPriceBySessionId(int sessionId) {
        String query = "SELECT ticket_price FROM Sessions WHERE session_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, sessionId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("ticket_price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0; // Return 0.0 if session ID is invalid or not found
    }

    /**
     * Retrieves the age-based discount for a given age.
     *
     * @param age the age of the customer
     * @return the discount percentage
     */
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

    /**
     * Displays an alert with the given title and content.
     *
     * @param title   the title of the alert
     * @param content the content of the alert
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Checks if a name starts with a special character.
     *
     * @param name the name to check
     * @return true if the name starts with a special character, false otherwise
     */
    private boolean startsWithSpecialCharacter(String name) {
        String specialCharRegex = "^[^a-zA-ZğüşöçıİĞÜŞÖÇ].*";
        return name.matches(specialCharRegex);
    }

    /**
     * Validates the format of an email address.
     *
     * @param email the email address to validate
     * @return true if the email address is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * Validates the format of a name.
     *
     * @param name the name to validate
     * @return true if the name is valid, false otherwise
     */
    private boolean isValidName(String name) {
        String nameRegex = "^[a-zA-ZğüşöçıİĞÜŞÖÇ’\\-\\s]+$";
        return name.matches(nameRegex);
    }

}