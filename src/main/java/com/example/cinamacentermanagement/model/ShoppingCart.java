package com.example.cinamacentermanagement.model;

public class ShoppingCart {
    private int cartId;
    private int sessionId;
    private Integer seatId; // Nullable for non-seat items
    private Integer productId; // Nullable for seat items
    private int quantity;
    private int customerId;
    private double price;
    private String seatName; // Derived property for seat items
    private String productName; // Derived property for product items

    // Constructor
    public ShoppingCart(int cartId, int sessionId, Integer seatId, Integer productId, int quantity, 
                        int customerId, double price, String seatName, String productName) {
        this.cartId = cartId;
        this.sessionId = sessionId;
        this.seatId = seatId;
        this.productId = productId;
        this.quantity = quantity;
        this.customerId = customerId;
        this.price = price;
        this.seatName = seatName;
        this.productName = productName;
    }

    // Getters and Setters
    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getSeatId() {
        return seatId;
    }

    public void setSeatId(Integer seatId) {
        this.seatId = seatId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSeatName() {
        return seatName;
    }

    public void setSeatName(String seatName) {
        this.seatName = seatName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public boolean isSeatItem() {
        return seatId != null;
    }

    // Method to determine item name
    public String getItemName() {
        if (seatId != null) {
            return seatName != null ? seatName : "Seat ID: " + seatId;
        } else if (productId != null) {
            return productName != null ? productName : "Product ID: " + productId;
        }
        return "Unknown Item";
    }

    // toString Method (optional, useful for debugging)
    @Override
    public String toString() {
        return "ShoppingCart{" +
                "cartId=" + cartId +
                ", sessionId=" + sessionId +
                ", seatId=" + seatId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", customerId=" + customerId +
                ", price=" + price +
                ", seatName='" + seatName + '\'' +
                ", productName='" + productName + '\'' +
                '}';
    }
}
