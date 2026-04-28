package service;

import java.time.LocalDate;
import model.InventoryItem;
import model.ProductItem;
import model.Sale;
import model.SaleItem;


/**
 * Handles sale-related use cases.
 */
public class SaleService {

    private final InventoryService inventoryService = new InventoryService();

    /**
     * Creates a new sale.
     *
     * @param saleID unique sale identifier
     * @param saleAmount sale total
     * @return newly created sale
     */
    public Sale createSale(String saleID, double saleAmount) {
        if (saleID == null || saleID.isBlank()) {
            throw new IllegalArgumentException("Sale ID is required.");
        }

        if (saleAmount < 0) {
            throw new IllegalArgumentException("Sale amount cannot be negative.");
        }

        return new Sale(saleID, saleAmount, LocalDate.now());
    }

    /**
     * Adds an item to a sale.
     *
     * @param sale sale to update
     * @param saleItem line item to add
     */
    public void addSaleItem(Sale sale, SaleItem saleItem) {
        if (sale == null) {
            throw new IllegalArgumentException("Sale is required.");
        }

        if (saleItem == null) {
            throw new IllegalArgumentException("Sale item is required.");
        }

        if (saleItem.getSaleItemQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }

        InventoryItem inventoryItem = saleItem.getInventoryItem();
        if (inventoryItem == null) {
            throw new IllegalArgumentException("Inventory item is required.");
        }

        if (saleItem.getSaleItemQuantity() > inventoryItem.getQuantityOnHand()) {
            throw new IllegalArgumentException("Not enough stock available.");
        }

        sale.addSaleItem(saleItem);
        sale.setSaleAmount(calculateSaleTotal(sale));
    }

    /**
     * Processes the sale and updates inventory.
     *
     * @param sale sale whose items should reduce inventory quantities
     */
    public void processSale(Sale sale) {
        if (sale == null) {
            throw new IllegalArgumentException("Sale is required.");
        }

        if (sale.getSaleItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty.");
        }

        for (SaleItem saleItem : sale.getSaleItems()) {
            InventoryItem inventoryItem = saleItem.getInventoryItem();
            int requested = saleItem.getSaleItemQuantity();
            int current = inventoryItem.getQuantityOnHand();

            if (requested > current) {
                throw new IllegalArgumentException(
                    "Not enough stock for inventory item " + inventoryItem.getInventoryID()
                );
            }

            int newQuantity = current - requested;
            inventoryService.updateInventoryQuantity(inventoryItem, newQuantity);
        }

        sale.setSaleAmount(calculateSaleTotal(sale));
    }

    /**
     * Calculates the sale.
     *
     * @param sale to calculate the total of the sale.
     */
    public double calculateSaleTotal(Sale sale) {
        double total = 0.0;

        for (SaleItem saleItem : sale.getSaleItems()) {
            ProductItem product = saleItem.getInventoryItem().getProductItem();
            total += product.getPricePerItem() * saleItem.getSaleItemQuantity();
        }

        return total;
    }
}
