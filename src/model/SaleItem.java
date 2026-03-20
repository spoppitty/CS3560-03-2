package model;

/**
 * Represents one line item in a sale.
 */
public class SaleItem {
    private String saleItemID;
    private int saleItemQuantity;
    private InventoryItem inventoryItem;

    public SaleItem(String saleItemID, int saleItemQuantity, InventoryItem inventoryItem) {
        this.saleItemID = saleItemID;
        this.saleItemQuantity = saleItemQuantity;
        this.inventoryItem = inventoryItem;
    }

    public String getSaleItemID() {
        return saleItemID;
    }

    public int getSaleItemQuantity() {
        return saleItemQuantity;
    }

    public InventoryItem getInventoryItem() {
        return inventoryItem;
    }

    public void setSaleItemQuantity(int saleItemQuantity) {
        this.saleItemQuantity = saleItemQuantity;
    }

    public void setInventoryItem(InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }
}