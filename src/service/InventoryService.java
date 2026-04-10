package service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.InventoryItem;
import model.ProductItem;

/**
 * Handles inventory-related use cases.
 */
public class InventoryService {
    private final List<InventoryItem> inventoryItems;

    public InventoryService() {
        this.inventoryItems = new ArrayList<>();
    }

    /**
     * Adds a new item to inventory.
     */
    public void addItem(ProductItem product, InventoryItem inventoryItem) {
        validateInventoryItem(inventoryItem);
        inventoryItem.setProductItem(product);

        if (findByInventoryId(inventoryItem.getInventoryID()) != null) {
            throw new IllegalArgumentException("Inventory item ID already exists.");
        }

        inventoryItems.add(inventoryItem);
    }

    /**
     * Removes an item from inventory.
     */
    public void removeItem(String inventoryID) {
        InventoryItem inventoryItem = findByInventoryId(inventoryID);

        if (inventoryItem == null) {
            throw new IllegalArgumentException("Inventory item not found.");
        }

        inventoryItems.remove(inventoryItem);
    }

    /**
     * Updates product item information.
     */
    public void updateItem(ProductItem product, String productName, String description) {
        if (product == null) {
            throw new IllegalArgumentException("Product item is required.");
        }

        product.setProductName(productName);
        product.setDescription(description);
    }

    /**
     * Updates inventory quantity for an existing item.
     */
    public void updateInventoryQuantity(InventoryItem inventoryItem, int newQuantity) {
        if (inventoryItem == null) {
            throw new IllegalArgumentException("Inventory item is required.");
        }
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }

        inventoryItem.setQuantityOnHand(newQuantity);
    }

    /**
     * Returns all inventory items.
     */
    public List<InventoryItem> viewInventory() {
        return Collections.unmodifiableList(new ArrayList<>(inventoryItems));
    }

    /**
     * Searches inventory items by keyword.
     */
    public List<InventoryItem> searchInventory(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return viewInventory();
        }

        String normalizedKeyword = keyword.trim().toLowerCase();
        List<InventoryItem> matches = new ArrayList<>();

        for (InventoryItem item : inventoryItems) {
            ProductItem product = item.getProductItem();
            String supplierName = "";

            if (product != null && product.getSupplier() != null) {
                supplierName = product.getSupplier().getName();
            }

            if (contains(item.getInventoryID(), normalizedKeyword)
                    || contains(item.getColor(), normalizedKeyword)
                    || contains(item.getSize(), normalizedKeyword)
                    || (product != null && contains(product.getProductID(), normalizedKeyword))
                    || (product != null && contains(product.getProductName(), normalizedKeyword))
                    || (product != null && contains(product.getDescription(), normalizedKeyword))
                    || contains(supplierName, normalizedKeyword)) {
                matches.add(item);
            }
        }

        return matches;
    }

    /**
     * Flags an item as low stock if quantity is below reorder level.
     */
    public boolean flagLowStockItem(InventoryItem inventoryItem) {
        return inventoryItem != null && inventoryItem.isLowStock();
    }

    /**
     * Clears the low stock flag when quantity is sufficient.
     */
    public boolean clearLowStockFlag(InventoryItem inventoryItem) {
        return inventoryItem != null
                && inventoryItem.getQuantityOnHand() > inventoryItem.getReorderLevel();
    }

    public InventoryItem findItemById(String inventoryID) {
        return findByInventoryId(inventoryID);
    }

    private InventoryItem findByInventoryId(String inventoryID) {
        if (inventoryID == null) {
            return null;
        }

        for (InventoryItem item : inventoryItems) {
            if (inventoryID.equalsIgnoreCase(item.getInventoryID())) {
                return item;
            }
        }

        return null;
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private void validateInventoryItem(InventoryItem inventoryItem) {
        if (inventoryItem == null) {
            throw new IllegalArgumentException("Inventory item is required.");
        }
        if (inventoryItem.getInventoryID() == null || inventoryItem.getInventoryID().trim().isEmpty()) {
            throw new IllegalArgumentException("Inventory ID is required.");
        }
        if (inventoryItem.getQuantityOnHand() < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        if (inventoryItem.getReorderLevel() < 0) {
            throw new IllegalArgumentException("Reorder level cannot be negative.");
        }
    }
}
