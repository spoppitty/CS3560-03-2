package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a sale transaction.
 */
public class Sale {
    /**
     * Unique ID for the sale transaction.
     */
    private String saleID;

    /**
     * Total amount charged for the sale.
     */
    private double saleAmount;

    /**
     * Date the sale happened.
     */
    private LocalDate saleDate;

    /**
     * Line items included in the sale.
     */
    private List<SaleItem> saleItems;

    /**
     * Creates a sale with an empty line-item list.
     */
    public Sale(String saleID, double saleAmount, LocalDate saleDate) {
        this.saleID = saleID;
        this.saleAmount = saleAmount;
        this.saleDate = saleDate;
        this.saleItems = new ArrayList<>();
    }

    /**
     * Returns the sale ID.
     */
    public String getSaleID() {
        return saleID;
    }

    /**
     * Returns the sale total.
     */
    public double getSaleAmount() {
        return saleAmount;
    }

    /**
     * Returns the sale date.
     */
    public LocalDate getSaleDate() {
        return saleDate;
    }

    /**
     * Returns the sale line items.
     */
    public List<SaleItem> getSaleItems() {
        return saleItems;
    }

    /**
     * Updates the sale total.
     */
    public void setSaleAmount(double saleAmount) {
        this.saleAmount = saleAmount;
    }

    /**
     * Updates the sale date.
     */
    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }

    /**
     * Adds one line item to the sale.
     */
    public void addSaleItem(SaleItem saleItem) {
        saleItems.add(saleItem);
    }

    /**
     * Removes one line item from the sale.
     */
    public void removeSaleItem(SaleItem saleItem) {
        saleItems.remove(saleItem);
    }
}
