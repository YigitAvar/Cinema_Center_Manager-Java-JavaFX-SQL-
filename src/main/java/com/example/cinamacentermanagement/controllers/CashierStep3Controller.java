package com.example.cinamacentermanagement.controllers;

import com.example.cinamacentermanagement.dao.SeatDao;
import com.example.cinamacentermanagement.dao.SessionDao;
import com.example.cinamacentermanagement.dao.ShoppingCartDao;
import com.example.cinamacentermanagement.model.Seat;
import com.example.cinamacentermanagement.model.Session;
import com.example.cinamacentermanagement.model.ShoppingCart;
import com.example.cinamacentermanagement.model.PricingUtil;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Controller class for the third step of the cashier process.
 */
public class CashierStep3Controller {
    private Session selectedSession;

    // Seat TableView
    @FXML
    private TableView<Seat> seatTable;
    @FXML
    private TableColumn<Seat, String> rowColumn;
    @FXML
    private TableColumn<Seat, String> seatColumn;
    @FXML
    private TableColumn<Seat, Boolean> occupiedColumn;

    // Shopping Cart TableView
    @FXML
    private TableView<ShoppingCart> cartTable;
    @FXML
    private TableColumn<ShoppingCart, String> itemColumn;
    @FXML
    private TableColumn<ShoppingCart, Integer> quantityColumn;
    @FXML
    private TableColumn<ShoppingCart, Double> priceColumn;

    @FXML
    private Label totalLabel;

    private final SeatDao seatDao = new SeatDao();
    private final ShoppingCartDao cartDao = new ShoppingCartDao();
    private final SessionDao sessionDao = new SessionDao();

    private ObservableList<ShoppingCart> cartItems = FXCollections.observableArrayList();
    private double total = 0.0;

    private int sessionId;

    /**
     * This custom "initialize" method is never automatically called by FXML.
     * If you want to pass sessionId/customerId/customerAge from the previous
     * stage, you must call this method manually, e.g.:
     *    controller.initialize(sessionId, customerId, customerAge);
     *
     * @param sessionId the session ID
     */
    public void initialize(int sessionId) {
        this.sessionId = sessionId;
        populateSeats();
        configureTables();
    }

    /**
     * JavaFX automatically calls "initialize()" with NO arguments after loading the FXML
     * (if you define it). If you need to do some initial setup code (like configuring
     * the tables), define that here.
     */
    @FXML
    public void initialize() {
        configureTables();
    }

    /**
     * Called from Step 2 controller after user selects a session.
     *
     * @param session the selected session
     */
    public void setSelectedSession(Session session) {
        this.selectedSession = session;
        this.sessionId = session.getSessionId();
        populateSeats();
    }

    /**
     * Populates the seat table with seats for the selected session.
     */
    private void populateSeats() {
        if (selectedSession != null) {
            List<Seat> seats = seatDao.getSeatsByHallId(selectedSession.getHallId());
            seatTable.setItems(FXCollections.observableArrayList(seats));
        }
    }

    /**
     * Configures the table columns for displaying seat and cart details.
     */
    private void configureTables() {
        seatTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        rowColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper("Row " + cellData.getValue().getRowNumber()));
        seatColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper("Seat " + cellData.getValue().getSeatNumber()));
        occupiedColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().isOccupied()));

        seatTable.setRowFactory(tv -> new TableRow<Seat>() {
            @Override
            protected void updateItem(Seat seat, boolean empty) {
                super.updateItem(seat, empty);
                if (seat == null || empty) {
                    setStyle("");
                } else if (seat.isOccupied()) {
                    setStyle("-fx-background-color: lightcoral;");
                } else {
                    setStyle("-fx-background-color: lightgreen;");
                }
            }
        });

        itemColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getItemName()));
        quantityColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getQuantity()));
        priceColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getPrice()));
    }

    /**
     * Handles the event when a seat is selected from the table.
     *
     * @param event the mouse event
     */
    @FXML
    private void onSeatSelected(MouseEvent event) {
        ObservableList<Seat> selectedSeats = seatTable.getSelectionModel().getSelectedItems();
        if (selectedSeats.isEmpty()) {
            showAlert("No Seats Selected", "Please select one or more valid seats.");
            return;
        }

        for (Seat seat : selectedSeats) {
            if (!seatDao.checkSeatAvailability(seat.getSeatId())) {
                showAlert("Seat Occupied",
                    "Seat " + seat.getRowNumber() + "-" + seat.getSeatNumber() + " is already occupied. Skipping."
                );
                continue;
            }

            seatDao.updateSeatOccupation(seat.getSeatId(), true);
            seat.setOccupied(true);

            double basePrice = sessionDao.getTicketPriceBySessionId(sessionId);

            String seatName = "Row " + seat.getRowNumber() + " Seat " + seat.getSeatNumber();
            ShoppingCart seatCartItem = new ShoppingCart(
                0,
                sessionId,
                seat.getSeatId(),
                null,
                1,
                0,
                basePrice,
                seatName,
                null
            );
            cartItems.add(seatCartItem);

            total += basePrice;
        }

        updateTotal();
        populateSeats();
    }

    /**
     * Updates the total price label.
     */
    private void updateTotal() {
        double subtotal = cartItems.stream().mapToDouble(ShoppingCart::getPrice).sum();
        double finalTotal = subtotal + (subtotal * 0.10);
        totalLabel.setText(String.format("Subtotal: $%.2f | Total: $%.2f", subtotal, finalTotal));
    }

    /**
     * Shows an alert dialog with the specified title and message.
     *
     * @param title   the title of the alert
     * @param message the content of the alert
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Handles the event when the "Proceed to Payment" button is clicked.
     */
    @FXML
    private void onProceedToPayment() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/cinamacentermanagement/fxml/cashierStep4.fxml")
            );
            Parent root = loader.load();

            CashierStep4Controller controller = loader.getController();
            controller.setupData(cartItems, total, sessionId);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Stage 4: Discounts & Additional Products");
            stage.show();

            ((Stage) totalLabel.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the event when the "Back to Session Selection" button is clicked.
     */
    @FXML
    private void onBackToSessionSelection() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/cinamacentermanagement/views/cashierStep2.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) totalLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}