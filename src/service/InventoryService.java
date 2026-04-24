package service;

import java.util.List;
import model.InventoryItem;
import model.ProductItem;
import model.Supplier;
import repository.InventoryRepository;

/**
 * Handles inventory-related use cases.
 */
public class InventoryService {
    /**
     * Repository responsible for all MySQL reads and writes.
     */
    private final InventoryRepository inventoryRepository;

    /**
     * Creates the service with the default MySQL-backed repository.
     */
    public InventoryService() {
        this.inventoryRepository = new InventoryRepository();
    }

    /**
     * Validates and adds a new item to inventory.
     *
     * @param product product details connected to the inventory item
     * @param inventoryItem inventory row to save
     */
    public void addItem(ProductItem product, InventoryItem inventoryItem) {
        validateInventoryItem(inventoryItem);
        validateProduct(product);
        inventoryItem.setProductItem(product);

        if (inventoryRepository.existsByInventoryId(inventoryItem.getInventoryID())) {
            throw new IllegalArgumentException("Inventory item ID already exists.");
        }

        inventoryRepository.addItem(product, inventoryItem);
    }

    /**
     * Removes an item from inventory by its inventory ID.
     *
     * @param inventoryID unique ID of the inventory row to remove
     */
    public void removeItem(String inventoryID) {
        if (!inventoryRepository.removeItem(inventoryID)) {
            throw new IllegalArgumentException("Inventory item not found.");
        }
    }

    /**
     * Updates product item information without changing inventory fields.
     *
     * @param product product object to update
     * @param productName new product name
     * @param description new product description
     */
    public void updateItem(ProductItem product, String productName, String description) {
        validateProduct(product);
        requireValue(productName, "Product name");

        product.setProductName(productName);
        product.setDescription(description);
        inventoryRepository.updateProduct(product);
    }

    /**
     * Updates inventory quantity for an existing item.
     *
     * @param inventoryItem item whose quantity should change
     * @param newQuantity replacement quantity on hand
     */
    public void updateInventoryQuantity(InventoryItem inventoryItem, int newQuantity) {
        if (inventoryItem == null) {
            throw new IllegalArgumentException("Inventory item is required.");
        }
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }

        if (!inventoryRepository.updateInventoryQuantity(inventoryItem.getInventoryID(), newQuantity)) {
            throw new IllegalArgumentException("Inventory item not found.");
        }

        inventoryItem.setQuantityOnHand(newQuantity);
    }

    /**
     * Updates inventory, product, and supplier details for an existing item.
     *
     * @param inventoryItem updated inventory item with product and supplier attached
     */
    public void updateInventoryItem(InventoryItem inventoryItem) {
        validateInventoryItem(inventoryItem);
        validateProduct(inventoryItem.getProductItem());

        if (!inventoryRepository.updateInventoryItem(inventoryItem)) {
            throw new IllegalArgumentException("Inventory item not found.");
        }
    }

    /**
     * Returns all inventory items.
     *
     * @return all rows loaded from MySQL
     */
    public List<InventoryItem> viewInventory() {
        return inventoryRepository.findAll();
    }

    /**
     * Searches inventory items by keyword.
     *
     * @param keyword text to match against inventory, product, and supplier fields
     * @return matching inventory rows, or all rows when the keyword is blank
     */
    public List<InventoryItem> searchInventory(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return viewInventory();
        }

        return inventoryRepository.search(keyword);
    }

    /**
     * Checks whether an item is low stock.
     *
     * @param inventoryItem item to check
     * @return true when quantity is less than or equal to reorder level
     */
    public boolean flagLowStockItem(InventoryItem inventoryItem) {
        return inventoryItem != null && inventoryItem.isLowStock();
    }

    /**
     * Checks whether an item has enough stock to clear a low-stock warning.
     *
     * @param inventoryItem item to check
     * @return true when quantity is greater than reorder level
     */
    public boolean clearLowStockFlag(InventoryItem inventoryItem) {
        return inventoryItem != null
                && inventoryItem.getQuantityOnHand() > inventoryItem.getReorderLevel();
    }

    /**
     * Finds an inventory item by ID.
     *
     * @param inventoryID unique inventory ID to look up
     * @return matching item, or null when no row exists
     */
    public InventoryItem findItemById(String inventoryID) {
        return inventoryRepository.findByInventoryId(inventoryID);
    }

    /**
     * Counts all inventory items for the dashboard summary.
     *
     * @return total number of inventory rows
     */
    public int countInventoryItems() {
        return inventoryRepository.countInventoryItems();
    }

    public List<InventoryItem> getLowStockItems() {
        return inventoryRepository.findAll()
                .stream()
                .filter(InventoryItem::isLowStock)
                .toList();
    }

    /**
     * Validates required inventory fields before saving to the database.
     *
     * @param inventoryItem inventory item to validate
     */
    private void validateInventoryItem(InventoryItem inventoryItem) {
        if (inventoryItem == null) {
            throw new IllegalArgumentException("Inventory item is required.");
        }
        requireValue(inventoryItem.getInventoryID(), "Inventory ID");
        requireValue(inventoryItem.getColor(), "Color");
        requireValue(inventoryItem.getSize(), "Size");
        if (inventoryItem.getQuantityOnHand() < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        if (inventoryItem.getReorderLevel() < 0) {
            throw new IllegalArgumentException("Reorder level cannot be negative.");
        }
    }

    /**
     * Validates required product fields and its nested supplier.
     *
     * @param product product to validate
     */
    private void validateProduct(ProductItem product) {
        if (product == null) {
            throw new IllegalArgumentException("Product item is required.");
        }

        requireValue(product.getProductID(), "Product ID");
        requireValue(product.getProductName(), "Product name");
        validateSupplier(product.getSupplier());
    }

    /**
     * Validates required supplier fields before saving.
     *
     * @param supplier supplier to validate
     */
    private void validateSupplier(Supplier supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier is required.");
        }

        requireValue(supplier.getSupplierID(), "Supplier ID");
        requireValue(supplier.getAddress(), "Supplier address");
        requireValue(supplier.getEmail(), "Supplier email");
        requireValue(supplier.getName(), "Supplier name");
        requireValue(supplier.getContactName(), "Supplier contact");
    }

    /**
     * Ensures a text field is not null or blank.
     *
     * @param value value to check
     * @param fieldName user-facing field name for error messages
     */
    private void requireValue(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
    }
}
