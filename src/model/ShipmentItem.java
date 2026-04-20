package model;

/**
 * Represents one line item in a shipment.
 */
public class ShipmentItem {
    /**
     * Unique ID for this shipment line item.
     */
    private String shipmentItemID;

    /**
     * Quantity received or shipped for this line item.
     */
    private int shipmentQuantity;

    /**
     * Creates a shipment line item.
     */
    public ShipmentItem(String shipmentItemID, int shipmentQuantity) {
        this.shipmentItemID = shipmentItemID;
        this.shipmentQuantity = shipmentQuantity;
    }

    /**
     * Returns the shipment item ID.
     */
    public String getShipmentItemID() {
        return shipmentItemID;
    }

    /**
     * Returns the shipment quantity.
     */
    public int getShipmentQuantity() {
        return shipmentQuantity;
    }

    /**
     * Updates the shipment quantity.
     */
    public void setShipmentQuantity(int shipmentQuantity) {
        this.shipmentQuantity = shipmentQuantity;
    }
}
