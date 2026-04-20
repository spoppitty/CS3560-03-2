package service;

import java.util.List;
import model.InventoryItem;

/**
 * Handles report generation.
 */
public class ReportService {

    /**
     * Generates a low stock report from the inventory list.
     *
     * @param inventoryItems inventory rows to inspect
     * @return items whose quantity is at or below reorder level
     */
    public List<InventoryItem> generateLowStockReport(List<InventoryItem> inventoryItems) {
        // TODO: return only items that are low in stock
        return null;
    }
}
