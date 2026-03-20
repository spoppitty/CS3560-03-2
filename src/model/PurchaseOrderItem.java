package model;

/**
 * Represents one line item in a purchase order.
 */
public class PurchaseOrderItem {
    private String orderItemID;
    private double costPerItem;
    private int orderQuantity;
    private Shipment shipment;

    public PurchaseOrderItem(String orderItemID, double costPerItem, int orderQuantity, Shipment shipment) {
        this.orderItemID = orderItemID;
        this.costPerItem = costPerItem;
        this.orderQuantity = orderQuantity;
        this.shipment = shipment;
    }

    public String getOrderItemID() {
        return orderItemID;
    }

    public double getCostPerItem() {
        return costPerItem;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setCostPerItem(double costPerItem) {
        this.costPerItem = costPerItem;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }
}