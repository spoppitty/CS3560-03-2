package model;

/**
 * Represents one line item in a sale.
 */
public class SaleItem {
    /**
     * Unique ID for this sale line item.
     */
    private String saleItemID;

    /**
     * Quantity sold for this line item.
     */
    private int saleItemQuantity;

    /**
     * Inventory item being sold.
     */
    private InventoryItem inventoryItem;

    /**
     * Creates a line item for a sale.
     */
    public SaleItem(String saleItemID, int saleItemQuantity, InventoryItem inventoryItem) {
        this.saleItemID = saleItemID;
        this.saleItemQuantity = saleItemQuantity;
        this.inventoryItem = inventoryItem;
    }

    /**
     * Returns the sale item ID.
     */
    public String getSaleItemID() {
        return saleItemID;
    }

    /**
     * Returns the quantity sold.
     */
    public int getSaleItemQuantity() {
        return saleItemQuantity;
    }

    /**
     * Returns the inventory item being sold.
     */
    public InventoryItem getInventoryItem() {
        return inventoryItem;
    }

    /**
     * Updates the quantity sold.
     */
    public void setSaleItemQuantity(int saleItemQuantity) {
        this.saleItemQuantity = saleItemQuantity;
    }

    /**
     * Updates which inventory item is being sold.
     */
    public void setInventoryItem(InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }
}
