package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a sale transaction.
 */
public class Sale {
    private String saleID;
    private double saleAmount;
    private LocalDate saleDate;
    private List<SaleItem> saleItems;

    public Sale(String saleID, double saleAmount, LocalDate saleDate) {
        this.saleID = saleID;
        this.saleAmount = saleAmount;
        this.saleDate = saleDate;
        this.saleItems = new ArrayList<>();
    }

    public String getSaleID() {
        return saleID;
    }

    public double getSaleAmount() {
        return saleAmount;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public List<SaleItem> getSaleItems() {
        return saleItems;
    }

    public void setSaleAmount(double saleAmount) {
        this.saleAmount = saleAmount;
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }

    public void addSaleItem(SaleItem saleItem) {
        saleItems.add(saleItem);
    }

    public void removeSaleItem(SaleItem saleItem) {
        saleItems.remove(saleItem);
    }
}