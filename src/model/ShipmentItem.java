package model;

/**
 * Represents one line item in a shipment.
 */
public class ShipmentItem {
    private String shipmentItemID;
    private int shipmentQuantity;

    public ShipmentItem(String shipmentItemID, int shipmentQuantity) {
        this.shipmentItemID = shipmentItemID;
        this.shipmentQuantity = shipmentQuantity;
    }

    public String getShipmentItemID() {
        return shipmentItemID;
    }

    public int getShipmentQuantity() {
        return shipmentQuantity;
    }

    public void setShipmentQuantity(int shipmentQuantity) {
        this.shipmentQuantity = shipmentQuantity;
    }
}