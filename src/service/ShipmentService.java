package service;

import java.time.LocalDate;
import java.util.List;
import model.InventoryItem;
import model.Shipment;
import model.ShipmentItem;
import model.ShipmentRecord;
import repository.ShipmentRepository;

/**
 * Handles shipment-related use cases.
 */
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;

    /**
     * Creates the service with the default MySQL-backed repository.
     */
    public ShipmentService() {
        this.shipmentRepository = new ShipmentRepository();
    }

    /**
     * Returns shipment history rows.
     *
     * @return open shipment rows
     */
    public List<ShipmentRecord> viewOpenShipments() {
        return shipmentRepository.findOpenShipments();
    }

    /**
     * Searches shipment history rows.
     *
     * @param keyword text to match against shipment, order, item, product, supplier, or status
     * @return matching open shipment rows, or all open rows when the keyword is blank
     */
    public List<ShipmentRecord> searchOpenShipments(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return viewOpenShipments();
        }

        return shipmentRepository.searchOpenShipments(keyword);
    }

    /**
     * Receives a shipment today and adds all shipment item quantities to inventory.
     *
     * @param shipmentID shipment to receive
     */
    public void receiveShipment(String shipmentID) {
        if (shipmentID == null || shipmentID.trim().isEmpty()) {
            throw new IllegalArgumentException("Shipment ID is required.");
        }

        if (!shipmentRepository.receiveShipment(shipmentID.trim(), LocalDate.now())) {
            throw new IllegalArgumentException("Shipment was not found or was already received.");
        }
    }

    /**
     * Receives a shipment and updates shipment information.
     *
     * @param shipment shipment that arrived
     * @param deliveryDate date the shipment was received
     */
    public void receiveShipment(Shipment shipment, LocalDate deliveryDate) {
        if (shipment == null) {
            throw new IllegalArgumentException("Shipment is required.");
        }
        LocalDate receivedDate = deliveryDate == null ? LocalDate.now() : deliveryDate;
        shipment.setShipmentStatus("Received");
        shipment.setDeliveryDate(receivedDate);
        shipmentRepository.receiveShipment(shipment.getShipmentID(), receivedDate);
    }

    /**
     * Adds a shipment item to a shipment.
     *
     * @param shipment shipment to update
     * @param shipmentItem line item to add
     */
    public void addShipmentItem(Shipment shipment, ShipmentItem shipmentItem) {
        if (shipment == null || shipmentItem == null) {
            throw new IllegalArgumentException("Shipment and shipment item are required.");
        }
        shipment.addShipmentItem(shipmentItem);
    }

    /**
     * Updates inventory quantity based on received shipment quantity.
     *
     * @param inventoryItem inventory item being replenished
     * @param receivedQuantity quantity received in the shipment
     */
    public void updateInventoryFromShipment(InventoryItem inventoryItem, int receivedQuantity) {
        if (inventoryItem == null) {
            throw new IllegalArgumentException("Inventory item is required.");
        }
        if (receivedQuantity < 0) {
            throw new IllegalArgumentException("Received quantity cannot be negative.");
        }
        inventoryItem.setQuantityOnHand(inventoryItem.getQuantityOnHand() + receivedQuantity);
    }

    /**
     * Clears low stock state after inventory is replenished.
     *
     * @param inventoryItem inventory item to check
     */
    public void clearLowStockIfNeeded(InventoryItem inventoryItem) {
        if (inventoryItem == null) {
            throw new IllegalArgumentException("Inventory item is required.");
        }
    }
}
