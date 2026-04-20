package model;

/**
 * Represents inventory information for a product.
 */
public class InventoryItem {
    /**
     * Unique ID for the inventory row.
     */
    private String inventoryID;

    /**
     * Color variation of the product.
     */
    private String color;

    /**
     * Size variation of the product.
     */
    private String size;

    /**
     * Current quantity available in stock.
     */
    private int quantityOnHand;

    /**
     * Quantity threshold where the item should be considered low stock.
     */
    private int reorderLevel;

    /**
     * Product details connected to this inventory row.
     */
    private ProductItem productItem;

    /**
     * Creates an inventory row for one product variation.
     */
    public InventoryItem(String inventoryID, String color, String size,
                         int quantityOnHand, int reorderLevel, ProductItem productItem) {
        this.inventoryID = inventoryID;
        this.color = color;
        this.size = size;
        this.quantityOnHand = quantityOnHand;
        this.reorderLevel = reorderLevel;
        this.productItem = productItem;
    }

    /**
     * Returns the inventory ID.
     */
    public String getInventoryID() {
        return inventoryID;
    }

    /**
     * Returns the product color.
     */
    public String getColor() {
        return color;
    }

    /**
     * Returns the product size.
     */
    public String getSize() {
        return size;
    }

    /**
     * Returns the current quantity on hand.
     */
    public int getQuantityOnHand() {
        return quantityOnHand;
    }

    /**
     * Returns the reorder threshold.
     */
    public int getReorderLevel() {
        return reorderLevel;
    }

    /**
     * Returns the product connected to this inventory row.
     */
    public ProductItem getProductItem() {
        return productItem;
    }

    /**
     * Updates the product color.
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Updates the product size.
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * Replaces the current quantity on hand.
     */
    public void setQuantityOnHand(int quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    /**
     * Updates the reorder threshold.
     */
    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    /**
     * Connects this inventory row to a product.
     */
    public void setProductItem(ProductItem productItem) {
        this.productItem = productItem;
    }

    /**
     * Adds stock to the current quantity.
     */
    public void increaseQuantity(int amount) {
        this.quantityOnHand += amount;
    }

    /**
     * Removes stock from the current quantity.
     */
    public void decreaseQuantity(int amount) {
        this.quantityOnHand -= amount;
    }

    /**
     * Checks whether the item has reached its reorder threshold.
     */
    public boolean isLowStock() {
        return quantityOnHand <= reorderLevel;
    }
}
