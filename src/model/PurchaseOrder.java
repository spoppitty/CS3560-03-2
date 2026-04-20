package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a purchase order.
 */
public class PurchaseOrder {
    /**
     * Unique ID for the purchase order.
     */
    private String purchaseOrderID;

    /**
     * Total amount for all ordered items.
     */
    private double orderAmount;

    /**
     * Current order status, such as pending or received.
     */
    private String orderStatus;

    /**
     * Date the order was created.
     */
    private LocalDate orderDate;

    /**
     * Account that created or owns the order.
     */
    private Account account;

    /**
     * Line items included in the order.
     */
    private List<PurchaseOrderItem> orderItems;

    /**
     * Creates a purchase order with an empty line-item list.
     */
    public PurchaseOrder(String purchaseOrderID, double orderAmount, String orderStatus,
                         LocalDate orderDate, Account account) {
        this.purchaseOrderID = purchaseOrderID;
        this.orderAmount = orderAmount;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.account = account;
        this.orderItems = new ArrayList<>();
    }

    /**
     * Returns the purchase order ID.
     */
    public String getPurchaseOrderID() {
        return purchaseOrderID;
    }

    /**
     * Returns the total order amount.
     */
    public double getOrderAmount() {
        return orderAmount;
    }

    /**
     * Returns the order status.
     */
    public String getOrderStatus() {
        return orderStatus;
    }

    /**
     * Returns the order date.
     */
    public LocalDate getOrderDate() {
        return orderDate;
    }

    /**
     * Returns the account connected to the order.
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Returns the order line items.
     */
    public List<PurchaseOrderItem> getOrderItems() {
        return orderItems;
    }

    /**
     * Updates the total order amount.
     */
    public void setOrderAmount(double orderAmount) {
        this.orderAmount = orderAmount;
    }

    /**
     * Updates the order status.
     */
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    /**
     * Updates the order date.
     */
    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    /**
     * Updates the account connected to the order.
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Adds one line item to the order.
     */
    public void addOrderItem(PurchaseOrderItem item) {
        orderItems.add(item);
    }

    /**
     * Removes one line item from the order.
     */
    public void removeOrderItem(PurchaseOrderItem item) {
        orderItems.remove(item);
    }
}
