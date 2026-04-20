package service;

import java.time.LocalDate;
import model.InventoryItem;
import model.Shipment;
import model.ShipmentItem;

/**
 * Handles shipment-related use cases.
 */
public class ShipmentService {

    /**
     * Receives a shipment and updates shipment information.
     *
     * @param shipment shipment that arrived
     * @param deliveryDate date the shipment was received
     */
    public void receiveShipment(Shipment shipment, LocalDate deliveryDate) {
        // TODO: mark shipment as received and set delivery date
    }

    /**
     * Adds a shipment item to a shipment.
     *
     * @param shipment shipment to update
     * @param shipmentItem line item to add
     */
    public void addShipmentItem(Shipment shipment, ShipmentItem shipmentItem) {
        // TODO: add shipment item to shipment
    }

    /**
     * Updates inventory quantity based on received shipment quantity.
     *
     * @param inventoryItem inventory item being replenished
     * @param receivedQuantity quantity received in the shipment
     */
    public void updateInventoryFromShipment(InventoryItem inventoryItem, int receivedQuantity) {
        // TODO: increase inventory quantity
    }

    /**
     * Clears low stock state after inventory is replenished.
     *
     * @param inventoryItem inventory item to check
     */
    public void clearLowStockIfNeeded(InventoryItem inventoryItem) {
        // TODO: clear low stock flag if quantity is above reorder level
    }
}
