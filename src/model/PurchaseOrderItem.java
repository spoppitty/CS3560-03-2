package model;

/**
 * Represents one line item in a purchase order.
 */
public class PurchaseOrderItem {
    /**
     * Unique ID for this order line item.
     */
    private String orderItemID;

    /**
     * Cost for a single unit of the item.
     */
    private double costPerItem;

    /**
     * Quantity ordered for this line item.
     */
    private int orderQuantity;

    /**
     * Shipment associated with this ordered item.
     */
    private Shipment shipment;

    /**
     * Creates a purchase order line item.
     */
    public PurchaseOrderItem(String orderItemID, double costPerItem, int orderQuantity, Shipment shipment) {
        this.orderItemID = orderItemID;
        this.costPerItem = costPerItem;
        this.orderQuantity = orderQuantity;
        this.shipment = shipment;
    }

    /**
     * Returns the order item ID.
     */
    public String getOrderItemID() {
        return orderItemID;
    }

    /**
     * Returns the cost per unit.
     */
    public double getCostPerItem() {
        return costPerItem;
    }

    /**
     * Returns the ordered quantity.
     */
    public int getOrderQuantity() {
        return orderQuantity;
    }

    /**
     * Returns the shipment connected to this order item.
     */
    public Shipment getShipment() {
        return shipment;
    }

    /**
     * Updates the cost per unit.
     */
    public void setCostPerItem(double costPerItem) {
        this.costPerItem = costPerItem;
    }

    /**
     * Updates the ordered quantity.
     */
    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    /**
     * Updates the shipment connected to this order item.
     */
    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }
}
