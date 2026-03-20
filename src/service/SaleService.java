package service;

import model.Sale;
import model.SaleItem;

/**
 * Handles sale-related use cases.
 */
public class SaleService {

    /**
     * Creates a new sale.
     */
    public Sale createSale(String saleID, double saleAmount) {
        // TODO: create and return a sale object
        return null;
    }

    /**
     * Adds an item to a sale.
     */
    public void addSaleItem(Sale sale, SaleItem saleItem) {
        // TODO: add item to sale
    }

    /**
     * Processes the sale and updates inventory.
     */
    public void processSale(Sale sale) {
        // TODO: reduce inventory quantities based on sale items
    }
}