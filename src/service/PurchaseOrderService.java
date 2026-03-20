package service;

import model.Account;
import model.PurchaseOrder;
import model.PurchaseOrderItem;

/**
 * Handles purchase-order-related use cases.
 */
public class PurchaseOrderService {

    /**
     * Creates a new purchase order.
     */
    public PurchaseOrder createPurchaseOrder(Account account) {
        // TODO: create and return a new purchase order
        return null;
    }

    /**
     * Adds an item to a purchase order.
     */
    public void addPurchaseOrderItem(PurchaseOrder order, PurchaseOrderItem item) {
        // TODO: add order item to purchase order
    }

    /**
     * Updates the status of a purchase order.
     */
    public void updatePurchaseOrderStatus(PurchaseOrder order, String status) {
        // TODO: update order status
    }

    /**
     * Calculates the total amount of the purchase order.
     */
    public double calculateOrderAmount(PurchaseOrder order) {
        // TODO: calculate total order amount
        return 0.0;
    }
}