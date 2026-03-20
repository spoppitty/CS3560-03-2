package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a purchase order.
 */
public class PurchaseOrder {
    private String purchaseOrderID;
    private double orderAmount;
    private String orderStatus;
    private LocalDate orderDate;
    private Account account;
    private List<PurchaseOrderItem> orderItems;

    public PurchaseOrder(String purchaseOrderID, double orderAmount, String orderStatus,
                         LocalDate orderDate, Account account) {
        this.purchaseOrderID = purchaseOrderID;
        this.orderAmount = orderAmount;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.account = account;
        this.orderItems = new ArrayList<>();
    }

    public String getPurchaseOrderID() {
        return purchaseOrderID;
    }

    public double getOrderAmount() {
        return orderAmount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public Account getAccount() {
        return account;
    }

    public List<PurchaseOrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderAmount(double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void addOrderItem(PurchaseOrderItem item) {
        orderItems.add(item);
    }

    public void removeOrderItem(PurchaseOrderItem item) {
        orderItems.remove(item);
    }
}