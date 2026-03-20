package model;

/**
 * Represents inventory information for a product.
 */
public class InventoryItem {
    private String inventoryID;
    private String color;
    private String size;
    private int quantityOnHand;
    private int reorderLevel;
    private ProductItem productItem;

    public InventoryItem(String inventoryID, String color, String size,
                         int quantityOnHand, int reorderLevel, ProductItem productItem) {
        this.inventoryID = inventoryID;
        this.color = color;
        this.size = size;
        this.quantityOnHand = quantityOnHand;
        this.reorderLevel = reorderLevel;
        this.productItem = productItem;
    }

    public String getInventoryID() {
        return inventoryID;
    }

    public String getColor() {
        return color;
    }

    public String getSize() {
        return size;
    }

    public int getQuantityOnHand() {
        return quantityOnHand;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public ProductItem getProductItem() {
        return productItem;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setQuantityOnHand(int quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public void setProductItem(ProductItem productItem) {
        this.productItem = productItem;
    }

    public void increaseQuantity(int amount) {
        this.quantityOnHand += amount;
    }

    public void decreaseQuantity(int amount) {
        this.quantityOnHand -= amount;
    }

    public boolean isLowStock() {
        return quantityOnHand <= reorderLevel;
    }
}