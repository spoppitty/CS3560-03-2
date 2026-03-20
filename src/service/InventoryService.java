package service;

import java.util.List;
import model.InventoryItem;
import model.ProductItem;

/**
 * Handles inventory-related use cases.
 */
public class InventoryService {

    /**
     * Adds a new item to inventory.
     */
    public void addItem(ProductItem product, InventoryItem inventoryItem) {
        // TODO: add item to inventory
    }

    /**
     * Removes an item from inventory.
     */
    public void removeItem(String inventoryID) {
        // TODO: remove item from inventory
    }

    /**
     * Updates product item information.
     */
    public void updateItem(ProductItem product, String productName, String description) {
        // TODO: update product details
    }

    /**
     * Updates inventory quantity for an existing item.
     */
    public void updateInventoryQuantity(InventoryItem inventoryItem, int newQuantity) {
        // TODO: update quantity on hand
    }

    /**
     * Returns all inventory items.
     */
    public List<InventoryItem> viewInventory() {
        // TODO: return all inventory items
        return null;
    }

    /**
     * Searches inventory items by keyword.
     */
    public List<InventoryItem> searchInventory(String keyword) {
        // TODO: search inventory by keyword
        return null;
    }

    /**
     * Flags an item as low stock if quantity is below reorder level.
     */
    public boolean flagLowStockItem(InventoryItem inventoryItem) {
        // TODO: determine whether item should be flagged
        return false;
    }

    /**
     * Clears the low stock flag when quantity is sufficient.
     */
    public boolean clearLowStockFlag(InventoryItem inventoryItem) {
        // TODO: determine whether low stock flag should be cleared
        return false;
    }
}