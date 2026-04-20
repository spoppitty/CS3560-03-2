package model;

/**
 * Represents a product item in the system.
 */
public class ProductItem {
    /**
     * Unique ID for the product.
     */
    private String productID;

    /**
     * Product name displayed in the dashboard.
     */
    private String productName;

    /**
     * Longer product description.
     */
    private String description;

    /**
     * Supplier that provides this product.
     */
    private Supplier supplier;

    /**
     * Creates a product with supplier information.
     */
    public ProductItem(String productID, String productName, String description, Supplier supplier) {
        this.productID = productID;
        this.productName = productName;
        this.description = description;
        this.supplier = supplier;
    }

    /**
     * Returns the product ID.
     */
    public String getProductID() {
        return productID;
    }

    /**
     * Returns the product name.
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Returns the product description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the product supplier.
     */
    public Supplier getSupplier() {
        return supplier;
    }

    /**
     * Updates the product name.
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * Updates the product description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Updates the product supplier.
     */
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
}
