package ui;

import java.util.UUID;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.InventoryItem;
import model.ProductItem;
import model.Sale;
import model.SaleItem;
import service.InventoryService;
import service.SaleService;
import service.SessionManager;

/**
 * JavaFX dashboard for the SALES subsystem.
 */
public class SalesDashboard {

    private static final String INVENTORY_STYLESHEET = "/ui/inventory-dashboard.css";
    private static final String SALES_STYLESHEET = "/ui/sales-dashboard.css";

    /**
     * Service used by the UI for all inventory operations.
     */
    private final InventoryService inventoryService = new InventoryService();

    /**
     * Service used by the UI for all sale operations.
     */
    private final SaleService saleService = new SaleService();

    /**
     * JavaFX list that backs the table; changing this list refreshes the table rows.
     */
    private final ObservableList<InventoryItem> inventoryRows = FXCollections.observableArrayList();

    /**
     * JavaFX list that backs the cart rows.
     */
    private final ObservableList<SaleItem> cartRows = FXCollections.observableArrayList();

    /**
     * Main table that displays inventory rows from MySQL.
     */
    private TableView<InventoryItem> inventoryTable;

    /**
     * Main table that displays the current cart items.
     */
    private TableView<SaleItem> cartTable;

    /**
     * Input field for the quantity to add to the cart.
     */
    private TextField quantityField;

    /**
     * Label for total amount being purchased.
     */
    private Label totalLabel;

    /**
     * Footer label for success and error messages.
     */
    private Label statusLabel;

    /**
     * Sale currently being placed in the cart.
     */
    private Sale currentSale;

    /**
     * Builds the sales scene and loads the current invenotry data.
     * 
     * @param stage primary application window
     * @param app an app controller used for scene navigation
     * @return sales scene
     */
    public Scene createScene(Stage stage, InventorySubsystemApp app) {
        currentSale = createNewSale();

        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.getStyleClass().add("sales-root");
        root.setTop(createHeader(app));
        root.setCenter(createContent());
        root.setBottom(createFooter());

        refreshInventory();
        refreshCart();

        Scene scene = new Scene(root, 1200, 760);
        scene.getStylesheets().add(getClass().getResource(INVENTORY_STYLESHEET).toExternalForm());
        scene.getStylesheets().add(getClass().getResource(SALES_STYLESHEET).toExternalForm());
        return scene;
    }

    /**
     *Builds the header with title, spacer, userLabel, and backButton.
     * 
     * @param app an app controller used to return to inventory dashbaord
     * @return header layout
     */
    private VBox createHeader(InventorySubsystemApp app) {
        Label title = new Label("Sales Dashboard");
        title.getStyleClass().add("app-title");

        String userText = SessionManager.getCurrentUser() != null
                ? "Logged in: " + SessionManager.getCurrentUser().getFirstName()
                : "Not logged in";
        Label userLabel = new Label(userText);
        userLabel.getStyleClass().add("user-label");

        Button backButton = new Button("Back to Inventory");
        backButton.getStyleClass().add("secondary-button");
        backButton.setOnAction(event -> app.showInventory(SessionManager.getCurrentUser()));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox headerRow = new HBox(16, title, spacer, userLabel, backButton);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        VBox header = new VBox(headerRow);
        header.getStyleClass().add("app-header");
        return header;
    }

    /**
     * Builds invenotry table and cart panel.
     * 
     * @return content and cart layout
     */
    private HBox createContent() {
        inventoryTable = buildInventoryTable();
        cartTable = buildCartTable();

        Label inventoryLabel = new Label("Available Inventory");
        inventoryLabel.getStyleClass().add("sales-section-title");

        Label cartLabel = new Label("Cart");
        cartLabel.getStyleClass().add("sales-section-title");

        totalLabel = new Label("Total: $0.00");
        totalLabel.getStyleClass().add("sales-total");

        VBox inventoryPane = new VBox(12, inventoryLabel, inventoryTable, createAddToCartControls());
        inventoryPane.getStyleClass().add("sales-panel");
        VBox.setVgrow(inventoryTable, Priority.ALWAYS);

        VBox cartPane = new VBox(12, cartLabel, cartTable, totalLabel, createCartActions());
        cartPane.getStyleClass().add("sales-panel");
        VBox.setVgrow(cartTable, Priority.ALWAYS);

        HBox content = new HBox(20, inventoryPane, cartPane);
        content.getStyleClass().add("content-workspace");
        HBox.setHgrow(inventoryPane, Priority.ALWAYS);
        HBox.setHgrow(cartPane, Priority.ALWAYS);
        return content;
    }

    /**
     * Creates controls for adding itmes and quantity amount.
     * 
     * @return quantity and adding items
     */
    private HBox createAddToCartControls() {
        quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        quantityField.setPrefWidth(120);
        quantityField.getStyleClass().add("text-field");

        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.getStyleClass().add("primary-button");
        addToCartButton.setOnAction(event -> addSelectedItemToCart());

        HBox controls = new HBox(10, new Label("Quantity"), quantityField, addToCartButton);
        controls.getStyleClass().add("sales-controls");
        controls.setAlignment(Pos.CENTER_LEFT);
        return controls;
    }

    /**
     * Builds cart action buttons for purchasing or clearing the current sale.
     * 
     * @return cart action row
     */
    private HBox createCartActions() {
        Button purchaseButton = new Button("Purchase");
        purchaseButton.getStyleClass().add("primary-button");
        purchaseButton.setOnAction(event -> purchaseCart());

        Button clearCartButton = new Button("Clear Cart");
        clearCartButton.getStyleClass().add("secondary-button");
        clearCartButton.setOnAction(event -> clearCart());

        HBox actions = new HBox(10, purchaseButton, clearCartButton);
        actions.getStyleClass().add("sales-controls");
        actions.setAlignment(Pos.CENTER_LEFT);
        return actions;
    }

    /**
     * Builds the footer that displays sales status messages.
     * 
     * @return footer layout
     */
    private VBox createFooter() {
        statusLabel = new Label("Select an inventory item, enter a quantity, and add it to the cart.");
        statusLabel.getStyleClass().add("sales-status");
        VBox footer = new VBox(statusLabel);
        footer.setPadding(new Insets(0, 20, 20, 20));
        return footer;
    }

    /**
     * Builds the inventory displaying item, price, and quantity.
     * 
     * @return inventory table
     */
    private TableView<InventoryItem> buildInventoryTable() {
        TableView<InventoryItem> table = new TableView<>(inventoryRows);
        table.getStyleClass().add("data-table");

        TableColumn<InventoryItem, String> inventoryIdColumn = new TableColumn<>("Inventory ID");
        inventoryIdColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getInventoryID()));

        TableColumn<InventoryItem, String> productColumn = new TableColumn<>("Product");
        productColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getProductItem().getProductName()));

        TableColumn<InventoryItem, Number> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(cell ->
                new ReadOnlyDoubleWrapper(cell.getValue().getProductItem().getPricePerItem()));

        TableColumn<InventoryItem, Number> stockColumn = new TableColumn<>("In Stock");
        stockColumn.setCellValueFactory(cell ->
                new ReadOnlyIntegerWrapper(cell.getValue().getQuantityOnHand()));

        table.getColumns().addAll(inventoryIdColumn, productColumn, priceColumn, stockColumn);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    /**
     * Builds the cart table.
     * 
     * @return cart table
     */
    private TableView<SaleItem> buildCartTable() {
        TableView<SaleItem> table = new TableView<>(cartRows);
        table.getStyleClass().add("data-table");

        TableColumn<SaleItem, String> productColumn = new TableColumn<>("Product");
        productColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getInventoryItem().getProductItem().getProductName()));

        TableColumn<SaleItem, Number> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(cell ->
                new ReadOnlyIntegerWrapper(cell.getValue().getSaleItemQuantity()));

        TableColumn<SaleItem, Number> cartPriceColumn = new TableColumn<>("Price");
        cartPriceColumn.setCellValueFactory(cell -> {
            ProductItem product = cell.getValue().getInventoryItem().getProductItem();
            double cartPrice = product.getPricePerItem() * cell.getValue().getSaleItemQuantity();
            return new ReadOnlyDoubleWrapper(cartPrice);
        });

        table.getColumns().addAll(productColumn, quantityColumn, cartPriceColumn);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private void addSelectedItemToCart() {
        InventoryItem selectedItem = inventoryTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            statusLabel.setText("Choose an inventory item first.");
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());
            SaleItem saleItem = new SaleItem(
                    "SI-" + UUID.randomUUID().toString().substring(0, 8),
                    quantity,
                    selectedItem
            );

            saleService.addSaleItem(currentSale, saleItem);
            refreshCart();
            quantityField.clear();
            statusLabel.setText("Added to cart: " + selectedItem.getProductItem().getProductName());
        } catch (NumberFormatException exception) {
            statusLabel.setText("Enter a valid whole-number quantity.");
        } catch (IllegalArgumentException exception) {
            statusLabel.setText(exception.getMessage());
        }
    }

    private void purchaseCart() {
        try {
            double total = saleService.calculateSaleTotal(currentSale);
            saleService.processSale(currentSale);
            statusLabel.setText(String.format("Purchase complete. Total: $%.2f", total));

            currentSale = createNewSale();
            refreshInventory();
            refreshCart();
            quantityField.clear();
        } catch (IllegalArgumentException exception) {
            statusLabel.setText(exception.getMessage());
        }
    }

    private void clearCart() {
        currentSale = createNewSale();
        refreshCart();
        quantityField.clear();
        statusLabel.setText("Cart cleared.");
    }

    private void refreshInventory() {
        inventoryRows.setAll(inventoryService.viewInventory());
    }

    private void refreshCart() {
        cartRows.setAll(currentSale.getSaleItems());
        totalLabel.setText(String.format("Total: $%.2f", saleService.calculateSaleTotal(currentSale)));
    }

    private Sale createNewSale() {
        return saleService.createSale("SALE-" + UUID.randomUUID().toString().substring(0, 8), 0.0);
    }
}
