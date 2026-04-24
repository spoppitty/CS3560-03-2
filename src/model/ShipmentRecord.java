package model;

import java.time.LocalDate;

/**
 * Flat shipment row for the dashboard's shipment table.
 */
public class ShipmentRecord {
    private final String shipmentID;
    private final String purchaseOrderID;
    private final String inventoryID;
    private final String productName;
    private final String supplierName;
    private final LocalDate purchaseDate;
    private final LocalDate shipmentDate;
    private final LocalDate deliveryDate;
    private final String shipmentStatus;
    private final int shipmentQuantity;
    private final double pricePerItem;
    private final double totalPrice;

    /**
     * Creates a dashboard-friendly shipment row.
     */
    public ShipmentRecord(String shipmentID, String purchaseOrderID, String inventoryID, String productName,
            String supplierName, LocalDate purchaseDate, LocalDate shipmentDate, LocalDate deliveryDate,
            String shipmentStatus, int shipmentQuantity, double pricePerItem, double totalPrice) {
        this.shipmentID = shipmentID;
        this.purchaseOrderID = purchaseOrderID;
        this.inventoryID = inventoryID;
        this.productName = productName;
        this.supplierName = supplierName;
        this.purchaseDate = purchaseDate;
        this.shipmentDate = shipmentDate;
        this.deliveryDate = deliveryDate;
        this.shipmentStatus = shipmentStatus;
        this.shipmentQuantity = shipmentQuantity;
        this.pricePerItem = pricePerItem;
        this.totalPrice = totalPrice;
    }

    public String getShipmentID() {
        return shipmentID;
    }

    public String getPurchaseOrderID() {
        return purchaseOrderID;
    }

    public String getInventoryID() {
        return inventoryID;
    }

    public String getProductName() {
        return productName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public LocalDate getShipmentDate() {
        return shipmentDate;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public String getShipmentStatus() {
        return shipmentStatus;
    }

    public int getShipmentQuantity() {
        return shipmentQuantity;
    }

    public double getPricePerItem() {
        return pricePerItem;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}
