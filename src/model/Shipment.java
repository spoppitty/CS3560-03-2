package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a shipment.
 */
public class Shipment {
    /**
     * Unique ID for the shipment.
     */
    private String shipmentID;

    /**
     * Date the shipment was created or shipped.
     */
    private LocalDate shipmentDate;

    /**
     * Date the shipment arrived.
     */
    private LocalDate deliveryDate;

    /**
     * Current shipment status.
     */
    private String shipmentStatus;

    /**
     * Supplier sending the shipment.
     */
    private Supplier supplier;

    /**
     * Line items included in the shipment.
     */
    private List<ShipmentItem> shipmentItems;

    /**
     * Creates a shipment with an empty line-item list.
     */
    public Shipment(String shipmentID, LocalDate shipmentDate, LocalDate deliveryDate,
                    String shipmentStatus, Supplier supplier) {
        this.shipmentID = shipmentID;
        this.shipmentDate = shipmentDate;
        this.deliveryDate = deliveryDate;
        this.shipmentStatus = shipmentStatus;
        this.supplier = supplier;
        this.shipmentItems = new ArrayList<>();
    }

    /**
     * Returns the shipment ID.
     */
    public String getShipmentID() {
        return shipmentID;
    }

    /**
     * Returns the shipment date.
     */
    public LocalDate getShipmentDate() {
        return shipmentDate;
    }

    /**
     * Returns the delivery date.
     */
    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    /**
     * Returns the shipment status.
     */
    public String getShipmentStatus() {
        return shipmentStatus;
    }

    /**
     * Returns the supplier sending the shipment.
     */
    public Supplier getSupplier() {
        return supplier;
    }

    /**
     * Returns the shipment line items.
     */
    public List<ShipmentItem> getShipmentItems() {
        return shipmentItems;
    }

    /**
     * Updates the shipment date.
     */
    public void setShipmentDate(LocalDate shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    /**
     * Updates the delivery date.
     */
    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    /**
     * Updates the shipment status.
     */
    public void setShipmentStatus(String shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }

    /**
     * Updates the supplier sending the shipment.
     */
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    /**
     * Adds one line item to the shipment.
     */
    public void addShipmentItem(ShipmentItem item) {
        shipmentItems.add(item);
    }

    /**
     * Removes one line item from the shipment.
     */
    public void removeShipmentItem(ShipmentItem item) {
        shipmentItems.remove(item);
    }
}
