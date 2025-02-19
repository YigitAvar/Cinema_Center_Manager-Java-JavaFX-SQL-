package com.example.cinamacentermanagement;

/**
 * Represents a discount in the cinema center management system.
 */
public class Discount {
    private int discountId;
    private int ageMin;
    private int ageMax;
    private double discountPercentage;

    /**
     * Constructs a new Discount object with the specified details.
     *
     * @param discountId the ID of the discount
     * @param ageMin the minimum age for the discount
     * @param ageMax the maximum age for the discount
     * @param discountPercentage the percentage of the discount
     */
    public Discount(int discountId, int ageMin, int ageMax, double discountPercentage) {
        this.discountId = discountId;
        this.ageMin = ageMin;
        this.ageMax = ageMax;
        this.discountPercentage = discountPercentage;
    }

    /**
     * Returns the ID of the discount.
     *
     * @return the discount ID
     */
    public int getDiscountId() {
        return discountId;
    }

    /**
     * Returns the minimum age for the discount.
     *
     * @return the minimum age
     */
    public int getAgeMin() {
        return ageMin;
    }

    /**
     * Returns the maximum age for the discount.
     *
     * @return the maximum age
     */
    public int getAgeMax() {
        return ageMax;
    }

    /**
     * Returns the percentage of the discount.
     *
     * @return the discount percentage
     */
    public double getDiscountPercentage() {
        return discountPercentage;
    }
}