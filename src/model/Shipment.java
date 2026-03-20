package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a shipment.
 */
public class Shipment {
    private String shipmentID;
    private LocalDate shipmentDate;
    private LocalDate deliveryDate;
    private String shipmentStatus;
    private Supplier supplier;
    private List<ShipmentItem> shipmentItems;

    public Shipment(String shipmentID, LocalDate shipmentDate, LocalDate deliveryDate,
                    String shipmentStatus, Supplier supplier) {
        this.shipmentID = shipmentID;
        this.shipmentDate = shipmentDate;
        this.deliveryDate = deliveryDate;
        this.shipmentStatus = shipmentStatus;
        this.supplier = supplier;
        this.shipmentItems = new ArrayList<>();
    }

    public String getShipmentID() {
        return shipmentID;
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

    public Supplier getSupplier() {
        return supplier;
    }

    public List<ShipmentItem> getShipmentItems() {
        return shipmentItems;
    }

    public void setShipmentDate(LocalDate shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public void setShipmentStatus(String shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public void addShipmentItem(ShipmentItem item) {
        shipmentItems.add(item);
    }

    public void removeShipmentItem(ShipmentItem item) {
        shipmentItems.remove(item);
    }
}