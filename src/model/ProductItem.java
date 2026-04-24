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
     * Price for one unit of the product.
     */
    private double pricePerItem;

    /**
     * Supplier that provides this product.
     */
    private Supplier supplier;

    /**
     * Creates a product with supplier information.
     */
    public ProductItem(String productID, String productName, String description, Supplier supplier) {
        this(productID, productName, description, 0.0, supplier);
    }

    /**
     * Creates a product with supplier information and a unit price.
     */
    public ProductItem(String productID, String productName, String description, double pricePerItem,
            Supplier supplier) {
        this.productID = productID;
        this.productName = productName;
        this.description = description;
        this.pricePerItem = pricePerItem;
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
     * Returns the price for one unit of this product.
     */
    public double getPricePerItem() {
        return pricePerItem;
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
     * Updates the price for one unit of this product.
     */
    public void setPricePerItem(double pricePerItem) {
        this.pricePerItem = pricePerItem;
    }

    /**
     * Updates the product supplier.
     */
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
}
