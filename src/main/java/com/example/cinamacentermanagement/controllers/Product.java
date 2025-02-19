package com.example.cinamacentermanagement.controllers;

/**
 * Represents a product in the cinema center management system.
 */
public class Product {
    private int productId;
    private String name;
    private double price;
    private String category;
    private String description;
    private int stockQuantity;
    private String imageUrl;
    private double tax;

    /**
     * Constructs a new Product object with the specified details.
     *
     * @param productId the ID of the product
     * @param name the name of the product
     * @param price the price of the product
     * @param category the category of the product
     * @param description the description of the product
     * @param stock the stock quantity of the product
     * @param image the URL of the product image
     * @param tax the tax applied to the product
     */
    public Product(int productId, String name, double price, String category, String description, int stock, String image, double tax) {

        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stockQuantity = stock;
        this.description = description;
        this.imageUrl = image;
        this.tax = tax;
        this.category = category;

    }

    /**
     * Returns the ID of the product.
     *
     * @return the product ID
     */
    public int getProductId() {
        return productId;
    }

    /**
     * Returns the name of the product.
     *
     * @return the product name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the price of the product.
     *
     * @return the product price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Returns the stock quantity of the product.
     *
     * @return the stock quantity
     */
    public int getStockQuantity() {
        return stockQuantity;
    }

    /**
     * Returns the description of the product.
     *
     * @return the product description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the URL of the product image.
     *
     * @return the image URL
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Returns the category of the product.
     *
     * @return the product category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category of the product.
     *
     * @param category the new category of the product
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Returns the tax applied to the product.
     *
     * @return the product tax
     */
    public double getTax() {
        return tax;
    }

    /**
     * Sets the tax applied to the product.
     *
     * @param tax the new tax of the product
     */
    public void setTax(double tax) {
        this.tax = tax;
    }
}