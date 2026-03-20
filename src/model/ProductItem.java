package model;

/**
 * Represents a product item in the system.
 */
public class ProductItem {
    private String productID;
    private String productName;
    private String description;
    private Supplier supplier;

    public ProductItem(String productID, String productName, String description, Supplier supplier) {
        this.productID = productID;
        this.productName = productName;
        this.description = description;
        this.supplier = supplier;
    }

    public String getProductID() {
        return productID;
    }

    public String getProductName() {
        return productName;
    }

    public String getDescription() {
        return description;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
}