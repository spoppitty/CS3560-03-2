package ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Employee;
import model.InventoryItem;
import model.ProductItem;
import model.ShipmentRecord;
import model.Supplier;
import report.generateLowStockPDF;
import repository.DatabaseException;
import service.InventoryService;
import service.SessionManager;
import service.ShipmentService;

/**
 * JavaFX dashboard for the inventory subsystem.
 */
public class InventoryDashboardApp {
    /**
     * Service used by the UI for all inventory operations.
     */
    private final InventoryService inventoryService = new InventoryService();

    /**
     * Service used by the UI for all shipment operations.
     */
    private final ShipmentService shipmentService = new ShipmentService();

    /**
     * JavaFX list that backs the table; changing this list refreshes the table rows.
     */
    private final ObservableList<InventoryItem> inventoryRows = FXCollections.observableArrayList();

    /**
     * JavaFX list that backs the shipment table.
     */
    private final ObservableList<ShipmentRecord> shipmentRows = FXCollections.observableArrayList();

    /**
     * Main table that displays inventory rows from MySQL.
     */
    private TableView<InventoryItem> inventoryTable;

    /**
     * Main table that displays open shipment rows from MySQL.
     */
    private TableView<ShipmentRecord> shipmentTable;

    /**
     * Search box in the header.
     */
    private TextField searchField;

    /**
     * Tabs for switching between inventory and shipment workflows.
     */
    private TabPane mainTabs;

    /**
     * Footer label for success and error messages.
     */
    private Label statusLabel;

    /**
     * Header summary value showing total inventory count.
     */
    private Label totalItemsValue;

    /**
     * Form fields used to create or edit inventory, product, and supplier data.
     */
    private TextField inventoryIdField;
    private TextField productIdField;
    private TextField productNameField;
    private TextField pricePerItemField;
    private TextArea descriptionArea;
    private ComboBox<Supplier> supplierComboBox;
    private final ObservableList<Supplier> supplierOptions = FXCollections.observableArrayList();
    private final ObservableList<Supplier> supplierRows = FXCollections.observableArrayList();
    private TableView<Supplier> supplierTable;
    private TextField supplierSearchField;
    private TextField supplierIdFormField;
    private TextField supplierNameFormField;
    private TextField supplierEmailFormField;
    private TextField supplierContactFormField;
    private TextField supplierAddressFormField;
    private TextField colorField;
    private TextField sizeField;
    private TextField quantityField;
    private TextField reorderLevelField;

    private static final String INVENTORY_SEARCH_PROMPT =
            "Search by product name, ID, supplier, color, or size";
    private static final String SHIPMENT_SEARCH_PROMPT =
            "Search by shipment, order, inventory, product, supplier, or status";
    private static final String SUPPLIER_SEARCH_PROMPT =
            "Search by supplier ID, name, email, contact, or address";
    private static final String STYLESHEET = "/ui/inventory-dashboard.css";

    /**
     * JavaFX entry point that builds the window and loads inventory from MySQL.
     *
     * @param stage primary application window
     */
    // @Override
    public Scene createScene(Stage stage, InventorySubsystemApp app) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setTop(createHeader(app));
        root.setCenter(createContent());
        root.setBottom(createFooter());

        Scene scene = new Scene(root, 1400, 860);
        applyStylesheet(scene);
        // stage.setTitle("Department Store Inventory Dashboard");
        // stage.setScene(scene);
        // stage.show();

        refreshSuppliers();
        showAllInventory("Loaded inventory from database.");
        return scene;
    }

    /**
     * Builds the top header with title, search controls, and item count.
     *
     * @return header layout
     */
    private VBox createHeader(InventorySubsystemApp app) {
        Label title = new Label("Department Store Inventory Dashboard");
        title.getStyleClass().add("app-title");

        searchField = new TextField();
        searchField.setPromptText(INVENTORY_SEARCH_PROMPT);
        searchField.getStyleClass().add("search-field");
        searchField.setPrefWidth(620);
        searchField.setOnAction(event -> applyActiveSearch());

        Button searchButton = createPrimaryButton("Search");
        searchButton.setOnAction(event -> applyActiveSearch());

        Button resetButton = createSecondaryButton("Show All");
        resetButton.setOnAction(event -> {
            searchField.clear();
            showAllActiveRows();
        });

        Employee currentUser = SessionManager.getCurrentUser();

        Label userLabel = new Label(
            currentUser != null
                ? "Logged in: " + currentUser.getFirstName()
                : "Not logged in"
         );

        userLabel.getStyleClass().add("user-label");

        Button logoutButton = createSecondaryButton("Logout");
        logoutButton.setOnAction(e -> {
            SessionManager.logout();
            app.showLogin();
        });
        logoutButton.setDisable(currentUser == null);

        VBox sessionBox = new VBox(6, userLabel, logoutButton);
        sessionBox.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox searchBar = new HBox(12, searchField, searchButton, resetButton);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        HBox headerTop = new HBox(16, title, spacer, createSummaryCard("Items"), sessionBox);
        headerTop.setAlignment(Pos.CENTER_LEFT);

        HBox toolbar = new HBox(searchBar);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(searchBar, Priority.ALWAYS);

        VBox header = new VBox(18, headerTop, toolbar);
        header.getStyleClass().add("app-header");
        return header;
    }

    /**
     * Builds the small summary card showing the total number of inventory rows.
     *
     * @param label label displayed above the count
     * @return summary card layout
     */
    private VBox createSummaryCard(String label) {
        Label name = new Label(label);
        name.getStyleClass().add("summary-label");

        Label value = new Label("0");
        value.getStyleClass().add("summary-value");

        totalItemsValue = value;

        VBox card = new VBox(4, name, value);
        card.setPadding(new Insets(16, 18, 16, 18));
        card.setMinWidth(112);
        card.setMinHeight(86);
        card.getStyleClass().add("summary-card");
        return card;
    }

    /**
     * Builds the center content area with the table and right-side form.
     *
     * @return content layout
     */
    private BorderPane createContent() {
        inventoryTable = buildInventoryTable();

        VBox workspace = new VBox(16, createInventoryToolbar(), inventoryTable);
        workspace.getStyleClass().add("content-workspace");
        VBox.setVgrow(inventoryTable, Priority.ALWAYS);

        BorderPane inventoryTabLayout = new BorderPane();
        inventoryTabLayout.setCenter(workspace);
        ScrollPane formScrollPane = createFormScrollPane();
        inventoryTabLayout.setRight(formScrollPane);
        BorderPane.setMargin(formScrollPane, new Insets(20, 20, 20, 0));

        shipmentTable = buildShipmentTable();
        BorderPane shipmentsTabLayout = createShipmentsContent();

        Tab inventoryTab = new Tab("Inventory", inventoryTabLayout);
        inventoryTab.setClosable(false);
        Tab suppliersTab = new Tab("Suppliers", createSuppliersContent());
        suppliersTab.setClosable(false);
        Tab shipmentsTab = new Tab("Shipments", shipmentsTabLayout);
        shipmentsTab.setClosable(false);

        mainTabs = new TabPane(inventoryTab, suppliersTab, shipmentsTab);
        mainTabs.getStyleClass().add("main-tabs");
        mainTabs.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            searchField.clear();
            updateSearchPrompt();
            showAllActiveRows();
        });
        BorderPane content = new BorderPane(mainTabs);
        return content;
    }

    /**
     * Builds the shipments tab with search, receive action, and open shipment table.
     *
     * @return shipments layout
     */
    private BorderPane createShipmentsContent() {
        Button receiveButton = createPrimaryButton("Confirm Received");
        receiveButton.setOnAction(event -> receiveSelectedShipment());

        ToolBar shipmentToolbar = new ToolBar(receiveButton);
        shipmentToolbar.getStyleClass().add("action-toolbar");

        VBox workspace = new VBox(16, shipmentToolbar, shipmentTable);
        workspace.getStyleClass().add("content-workspace");
        VBox.setVgrow(shipmentTable, Priority.ALWAYS);

        BorderPane layout = new BorderPane(workspace);
        return layout;
    }

    /**
     * Builds the toolbar of inventory actions.
     *
     * @return toolbar with add, update, report, reorder, and remove buttons
     */
    private ToolBar createInventoryToolbar() {
        Button addButton = createPrimaryButton("Add Item");
        addButton.setOnAction(event -> addItem());

        Button updateButton = createPrimaryButton("Update Quantity");
        updateButton.setOnAction(event -> updateSelectedQuantity());

        Button pdfButton = createPrimaryButton("Report Low Stock PDF");
        pdfButton.setOnAction(e -> generateLowStockPdf());

        Button removeButton = createPrimaryButton("Remove Selected");
        removeButton.setOnAction(event -> removeSelectedItem());

        Button reorderButton = createPrimaryButton("Reorder");
        reorderButton.setOnAction(event -> reorderSelectedItem());

        ToolBar toolbar = new ToolBar(addButton, updateButton, pdfButton, reorderButton, removeButton);
        toolbar.getStyleClass().add("action-toolbar");
        return toolbar;
    }

    /**
     * Builds the form used to add a new item or edit the selected item.
     *
     * @return form panel layout
     */
    private VBox createFormPanel() {
        Label title = new Label("Inventory Item Form");
        title.getStyleClass().add("panel-title");

        Button clearHeaderButton = createSecondaryButton("Clear Form");
        clearHeaderButton.setOnAction(event -> clearForm());

        Region titleSpacer = new Region();
        HBox.setHgrow(titleSpacer, Priority.ALWAYS);

        HBox titleRow = new HBox(12, title, titleSpacer, clearHeaderButton);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label description = new Label(
                "Use this form to add a new catalog item or update the quantity for an existing one.");
        description.setWrapText(true);
        description.getStyleClass().add("panel-description");

        inventoryIdField = createTextField("INV-1004");
        productIdField = createTextField("PROD-1004");
        productNameField = createTextField("Wool Coat");
        pricePerItemField = createTextField("79.99");
        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Brief product description");
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setWrapText(true);
        descriptionArea.setMaxWidth(Double.MAX_VALUE);

        supplierComboBox = new ComboBox<>(supplierOptions);
        supplierComboBox.setPromptText("Select a supplier");
        supplierComboBox.setMaxWidth(Double.MAX_VALUE);

        Button manageSuppliersButton = createSecondaryButton("Manage Suppliers");
        manageSuppliersButton.setOnAction(event -> mainTabs.getSelectionModel().select(1));
        colorField = createTextField("Gray");
        sizeField = createTextField("M");
        quantityField = createTextField("25");
        reorderLevelField = createTextField("10");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("form-grid");
        grid.setHgap(12);
        grid.setVgap(14);
        applyEqualColumns(grid, 3);
        grid.add(createField("Inventory ID", inventoryIdField), 0, 0);
        grid.add(createField("Product ID", productIdField), 1, 0);
        grid.add(createField("Price", pricePerItemField), 2, 0);
        grid.add(createField("Product Name", productNameField), 0, 1, 3, 1);
        grid.add(createField("Description", descriptionArea), 0, 2, 3, 1);

        HBox supplierRow = new HBox(10, supplierComboBox, manageSuppliersButton);
        supplierRow.setAlignment(Pos.CENTER_LEFT);
        supplierComboBox.setPrefWidth(250);
        manageSuppliersButton.setMinWidth(160);
        HBox.setHgrow(supplierComboBox, Priority.ALWAYS);
        grid.add(createField("Supplier", supplierRow), 0, 3, 3, 1);
        grid.add(createField("Color", colorField), 0, 4);
        grid.add(createField("Size", sizeField), 1, 4, 2, 1);
        grid.add(createField("Quantity", quantityField), 0, 5);
        grid.add(createField("Reorder Level", reorderLevelField), 1, 5, 2, 1);

        Button saveButton = createPrimaryButton("Save Item");
        saveButton.setMaxWidth(Double.MAX_VALUE);
        saveButton.setOnAction(event -> addItem());

        Button quantityButton = createSecondaryButton("Apply Quantity Update");
        quantityButton.setMaxWidth(Double.MAX_VALUE);
        quantityButton.setOnAction(event -> updateSelectedQuantity());

        VBox actions = new VBox(10, saveButton, quantityButton);
        actions.getStyleClass().add("form-actions");

        VBox panel = new VBox(14, titleRow, description, grid, actions);
        panel.setPrefWidth(400);
        panel.getStyleClass().add("side-panel");
        return panel;
    }

    /**
     * Wraps the form in a scroll pane so all fields remain reachable on smaller windows.
     *
     * @return scrollable form panel
     */
    private ScrollPane createFormScrollPane() {
        VBox formPanel = createFormPanel();

        ScrollPane scrollPane = new ScrollPane(formPanel);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPannable(true);
        scrollPane.setPrefWidth(440);
        scrollPane.getStyleClass().add("side-panel-scroll");
        scrollPane.viewportBoundsProperty().addListener((observable, oldBounds, newBounds) ->
                formPanel.setPrefWidth(Math.max(400, newBounds.getWidth() - 2)));
        return scrollPane;
    }

    /**
     * Builds and configures the inventory table columns.
     *
     * @return configured inventory table
     */
    private TableView<InventoryItem> buildInventoryTable() {
        TableView<InventoryItem> table = createBaseTable();
        table.getColumns().add(createTextColumn("Inventory ID", item -> item.getInventoryID(), 102));
        table.getColumns().add(createTextColumn("Product ID", item -> item.getProductItem().getProductID(), 115));
        table.getColumns().add(createTextColumn("Product Name", item -> item.getProductItem().getProductName(), 135));
        table.getColumns().add(createTextColumn("Price", item -> formatCurrency(item.getProductItem().getPricePerItem()), 90));
        table.getColumns().add(createTextColumn("Supplier", item -> item.getProductItem().getSupplier().getName(), 140));
        table.getColumns().add(createTextColumn("Color", InventoryItem::getColor, 75));
        table.getColumns().add(createTextColumn("Size", InventoryItem::getSize, 80));
        table.getColumns().add(createIntegerColumn("Quantity", InventoryItem::getQuantityOnHand, 90));
        table.getColumns().add(createIntegerColumn("Reorder", InventoryItem::getReorderLevel, 90));
        table.getColumns().add(createInventoryStatusColumn());
        table.setItems(inventoryRows);
        table.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldItem, newItem) -> loadSelectedIntoForm(newItem));
        return table;
    }

    /**
     * Builds and configures the open shipment table columns.
     *
     * @return configured shipment table
     */
    private TableView<ShipmentRecord> buildShipmentTable() {
        TableView<ShipmentRecord> table = new TableView<>();
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No shipments to display."));
        table.getStyleClass().add("data-table");
        table.getColumns().add(createShipmentTextColumn("Shipment ID", ShipmentRecord::getShipmentID, 120));
        table.getColumns().add(createShipmentTextColumn("Order ID", ShipmentRecord::getPurchaseOrderID, 120));
        table.getColumns().add(createShipmentTextColumn("Inventory ID", ShipmentRecord::getInventoryID, 120));
        table.getColumns().add(createShipmentTextColumn("Product", ShipmentRecord::getProductName, 180));
        table.getColumns().add(createShipmentTextColumn("Supplier", ShipmentRecord::getSupplierName, 170));
        table.getColumns().add(createShipmentTextColumn("Purchased", item -> formatDate(item.getPurchaseDate()), 110));
        table.getColumns().add(createShipmentTextColumn("Shipped", item -> formatDate(item.getShipmentDate()), 110));
        table.getColumns().add(createShipmentIntegerColumn("Quantity", ShipmentRecord::getShipmentQuantity, 90));
        table.getColumns().add(createShipmentTextColumn("Unit Price", item -> formatCurrency(item.getPricePerItem()), 100));
        table.getColumns().add(createShipmentTextColumn("Total Price", item -> formatCurrency(item.getTotalPrice()), 110));
        table.getColumns().add(createShipmentStatusColumn());
        table.setItems(shipmentRows);
        return table;
    }

    /**
     * Creates common table settings shared by the inventory table.
     *
     * @return base table instance
     */
    private TableView<InventoryItem> createBaseTable() {
        TableView<InventoryItem> table = new TableView<>();
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);   //CONSTRAINED_RESIZE_POLICY Had to Change
        table.setPlaceholder(new Label("No inventory items to display."));
        table.getStyleClass().add("data-table");
        return table;
    }

    /**
     * Creates a table column that displays string data from an InventoryItem.
     *
     * @param title column title
     * @param provider function that extracts the value from a row item
     * @param width preferred column width
     * @return configured text column
     */
    private TableColumn<InventoryItem, String> createTextColumn(String title, ValueProvider<String> provider, double width) {
        TableColumn<InventoryItem, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(provider.get(cellData.getValue())));
        column.setPrefWidth(width);
        return column;
    }

    /**
     * Creates a table column that displays integer data from an InventoryItem.
     *
     * @param title column title
     * @param provider function that extracts the value from a row item
     * @param width preferred column width
     * @return configured number column
     */
    private TableColumn<InventoryItem, Number> createIntegerColumn(String title, IntValueProvider provider, double width) {
        TableColumn<InventoryItem, Number> column = new TableColumn<>(title);
        column.setCellValueFactory(cellData -> new ReadOnlyIntegerWrapper(provider.get(cellData.getValue())));
        column.setPrefWidth(width);
        return column;
    }

    private TableColumn<InventoryItem, String> createInventoryStatusColumn() {
        TableColumn<InventoryItem, String> column =
                createTextColumn("Status", item -> item.isLowStock() ? "Low Stock" : "Healthy", 128);
        column.setCellFactory(tableColumn -> new javafx.scene.control.TableCell<>() {
            private final Label badge = new Label();

            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                badge.getStyleClass().removeAll("status-badge", "status-healthy", "status-low", "status-shipped");
                setText(null);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    badge.setText(status);
                    badge.getStyleClass().addAll("status-badge",
                            "Low Stock".equals(status) ? "status-low" : "status-healthy");
                    setGraphic(badge);
                }
            }
        });
        return column;
    }

    /**
     * Creates a shipment table text column.
     */
    private TableColumn<ShipmentRecord, String> createShipmentTextColumn(String title,
            ShipmentValueProvider<String> provider, double width) {
        TableColumn<ShipmentRecord, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(provider.get(cellData.getValue())));
        column.setPrefWidth(width);
        return column;
    }

    /**
     * Creates a shipment table integer column.
     */
    private TableColumn<ShipmentRecord, Number> createShipmentIntegerColumn(String title,
            ShipmentIntValueProvider provider, double width) {
        TableColumn<ShipmentRecord, Number> column = new TableColumn<>(title);
        column.setCellValueFactory(cellData -> new ReadOnlyIntegerWrapper(provider.get(cellData.getValue())));
        column.setPrefWidth(width);
        return column;
    }

    private TableColumn<ShipmentRecord, String> createShipmentStatusColumn() {
        TableColumn<ShipmentRecord, String> column = createShipmentTextColumn("Status", ShipmentRecord::getShipmentStatus, 110);
        column.setCellFactory(tableColumn -> new javafx.scene.control.TableCell<>() {
            private final Label badge = new Label();

            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                badge.getStyleClass().removeAll("status-badge", "status-healthy", "status-low", "status-shipped");
                setText(null);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    badge.setText(status);
                    badge.getStyleClass().addAll("status-badge", "status-shipped");
                    setGraphic(badge);
                }
            }
        });
        return column;
    }

    /**
     * Builds the footer where status and error messages are shown.
     *
     * @return footer layout
     */
    private HBox createFooter() {
        statusLabel = new Label("Ready.");
        statusLabel.setTextFill(Color.web("#4f6479"));

        HBox footer = new HBox(statusLabel);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.getStyleClass().add("app-footer");
        return footer;
    }

    /**
     * Runs a keyword search and refreshes the table with the matching rows.
     */
    private void applySearch() {
        try {
            List<InventoryItem> results = inventoryService.searchInventory(searchField.getText());
            refreshTables(results);
            setStatus("Search returned " + results.size() + " item(s).", false);
        } catch (DatabaseException exception) {
            showDatabaseError("Unable to search inventory", exception);
        }
    }

    /**
     * Runs the header search against whichever tab is active.
     */
    private void applyActiveSearch() {
        if (isShipmentsTabActive()) {
            applyShipmentSearch();
        } else if (isSuppliersTabActive()) {
            applySupplierSearch();
        } else {
            applySearch();
        }
    }

    /**
     * Runs a keyword search and refreshes the shipment table with matching open shipments.
     */
    private void applyShipmentSearch() {
        try {
            List<ShipmentRecord> results = shipmentService.searchOpenShipments(searchField.getText());
            shipmentRows.setAll(results);
            setStatus("Shipment search returned " + results.size() + " row(s).", false);
        } catch (DatabaseException exception) {
            showDatabaseError("Unable to search shipments", exception);
        }
    }

    /**
     * Reloads all rows for whichever tab is active.
     */
    private void showAllActiveRows() {
        if (isShipmentsTabActive()) {
            showOpenShipments("Showing shipment history.");
        } else if (isSuppliersTabActive()) {
            showAllSuppliers("Showing all suppliers.");
        } else {
            showAllInventory("Showing all inventory items.");
        }
    }

    /**
     * Create supplier tab.
     */
    private BorderPane createSuppliersContent() {
        supplierTable = buildSupplierTable();

        VBox workspace = new VBox(16, createSupplierToolbar(), supplierTable);
        workspace.getStyleClass().add("content-workspace");
        VBox.setVgrow(supplierTable, Priority.ALWAYS);

        BorderPane layout = new BorderPane();
        layout.setCenter(workspace);

        ScrollPane supplierFormScrollPane = createSupplierFormScrollPane();
        layout.setRight(supplierFormScrollPane);
        BorderPane.setMargin(supplierFormScrollPane, new Insets(20, 20, 20, 0));

        return layout;
    }

    /**
     * Create supplier tab's buttons.
     */
    private ToolBar createSupplierToolbar() {
        Button addButton = createPrimaryButton("Add Supplier");
        addButton.setOnAction(event -> addSupplier());

        Button updateButton = createPrimaryButton("Update Supplier");
        updateButton.setOnAction(event -> updateSelectedSupplier());

        ToolBar toolbar = new ToolBar(addButton, updateButton);
        toolbar.getStyleClass().add("action-toolbar");
        return toolbar;
    }

    /**
     * Create supplier list.
     */
    private TableView<Supplier> buildSupplierTable() {
        TableView<Supplier> table = new TableView<>();
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No suppliers to display."));
        table.getStyleClass().add("data-table");

        TableColumn<Supplier, String> idColumn = new TableColumn<>("Supplier ID");
        idColumn.setCellValueFactory(cell ->
            new ReadOnlyStringWrapper(cell.getValue().getSupplierID()));

        TableColumn<Supplier, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cell ->
            new ReadOnlyStringWrapper(cell.getValue().getName()));

        TableColumn<Supplier, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(cell ->
            new ReadOnlyStringWrapper(cell.getValue().getEmail()));

        TableColumn<Supplier, String> contactColumn = new TableColumn<>("Contact");
        contactColumn.setCellValueFactory(cell ->
            new ReadOnlyStringWrapper(cell.getValue().getContactName()));

        TableColumn<Supplier, String> addressColumn = new TableColumn<>("Address");
        addressColumn.setCellValueFactory(cell ->
            new ReadOnlyStringWrapper(cell.getValue().getAddress()));

        table.getColumns().addAll(idColumn, nameColumn, emailColumn, contactColumn, addressColumn);
        table.setItems(supplierRows);

        table.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldSupplier, newSupplier) -> loadSupplierIntoForm(newSupplier));

        return table;
    }

    /**
     * Create add supplier form.
     */
    private VBox createSupplierFormPanel() {
        Label title = new Label("Supplier Form");
        title.getStyleClass().add("panel-title");

        Button clearHeaderButton = createSecondaryButton("Clear Form");
        clearHeaderButton.setOnAction(event -> clearSupplierForm());

        Region titleSpacer = new Region();
        HBox.setHgrow(titleSpacer, Priority.ALWAYS);

        HBox titleRow = new HBox(12, title, titleSpacer, clearHeaderButton);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label description = new Label(
            "Use this form to add a new supplier or update an existing supplier.");
        description.setWrapText(true);
        description.getStyleClass().add("panel-description");

        supplierIdFormField = createTextField("SUP-204");
        supplierNameFormField = createTextField("Bright Apparel");
        supplierEmailFormField = createTextField("buyer@supplier.com");
        supplierContactFormField = createTextField("Jordan Lee");
        supplierAddressFormField = createTextField("123 Supply Rd, Salt Lake City, UT");

        VBox fields = new VBox(14,
                createField("Supplier ID", supplierIdFormField),
                createField("Supplier Name", supplierNameFormField),
                createField("Supplier Email", supplierEmailFormField),
                createField("Contact Name", supplierContactFormField),
                createField("Supplier Address", supplierAddressFormField));

        Button addButton = createPrimaryButton("Save Supplier");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(event -> addSupplier());

        Button updateButton = createSecondaryButton("Apply Update");
        updateButton.setMaxWidth(Double.MAX_VALUE);
        updateButton.setOnAction(event -> updateSelectedSupplier());

        VBox actions = new VBox(10, addButton, updateButton);
        actions.getStyleClass().add("form-actions");

        VBox panel = new VBox(14, titleRow, description, fields, actions);
        panel.setPrefWidth(400);
        panel.getStyleClass().add("side-panel");

        return panel;
    }

    private ScrollPane createSupplierFormScrollPane() {
        VBox formPanel = createSupplierFormPanel();
        ScrollPane scrollPane = new ScrollPane(formPanel);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPannable(true);
        scrollPane.setPrefWidth(420);
        scrollPane.getStyleClass().add("side-panel-scroll");
        scrollPane.viewportBoundsProperty().addListener(
            (observable, oldBounds, newBounds) ->
                formPanel.setPrefWidth(Math.max(400, newBounds.getWidth() - 2)));
        return scrollPane;
    }

    /**
     * Refresh populating updated and new supplier info globally.
     */
    private void refreshSuppliers() {
        try {
            List<Supplier> suppliers = inventoryService.viewSuppliers();
            supplierRows.setAll(suppliers);
            supplierOptions.setAll(suppliers);
        } catch (DatabaseException exception) {
            showDatabaseError("Unable to load suppliers", exception);
        }
    }

    /**
     * Show all suppliers.
     */
    private void showAllSuppliers(String statusMessage) {
        try {
            List<Supplier> suppliers = inventoryService.viewSuppliers();
            supplierRows.setAll(suppliers);
            supplierOptions.setAll(suppliers);

            if (statusMessage != null) {
                setStatus(statusMessage, false);
            }
        } catch (DatabaseException exception) {
            showDatabaseError("Unable to load suppliers", exception);
        }
    }

    /**
     * Supplier search.
     */
    private void applySupplierSearch() {
        try {
            List<Supplier> results = inventoryService.searchSuppliers(searchField.getText());
            supplierRows.setAll(results);
            setStatus("Supplier search returned " + results.size() + " row(s).", false);
        } catch (DatabaseException exception) {
            showDatabaseError("Unable to search suppliers", exception);
        }
    }

    /**
     * Create supplier from form data.
     */
    private Supplier buildSupplierFromSupplierForm() {
        return new Supplier(
            requireValue(supplierIdFormField, "Supplier ID"),
            requireValue(supplierAddressFormField, "Supplier address"),
            requireValue(supplierEmailFormField, "Supplier email"),
            requireValue(supplierNameFormField, "Supplier name"),
            requireValue(supplierContactFormField, "Supplier contact")
        );
    }

    /**
     * Add new supplier.
     */
    private void addSupplier() {
        try {
            Supplier supplier = buildSupplierFromSupplierForm();
            inventoryService.addSupplier(supplier);
            refreshSuppliers();
            selectSupplierById(supplier.getSupplierID());
            supplierComboBox.getSelectionModel().select(
                supplierOptions.stream()
                    .filter(s -> s.getSupplierID().equals(supplier.getSupplierID()))
                    .findFirst()
                    .orElse(null)
            );
            setStatus("Added supplier " + supplier.getSupplierID() + ".", false);
            clearSupplierForm();
        } catch (IllegalArgumentException exception) {
            showError("Unable to add supplier", exception.getMessage());
        } catch (DatabaseException exception) {
            showDatabaseError("Unable to add supplier", exception);
        }
    }

    /**
     * Update existing supplier.
     */
    private void updateSelectedSupplier() {
        Supplier selectedSupplier = supplierTable.getSelectionModel().getSelectedItem();
        if (selectedSupplier == null) {
            showError("No supplier selected", "Choose a supplier before updating it.");
            return;
        }

        try {
            Supplier updatedSupplier = buildSupplierFromSupplierForm();

            if (!updatedSupplier.getSupplierID().equals(selectedSupplier.getSupplierID())) {
                throw new IllegalArgumentException("Supplier ID cannot be changed while updating.");
            }

            inventoryService.updateSupplier(updatedSupplier);
            refreshSuppliers();
            selectSupplierById(updatedSupplier.getSupplierID());
            supplierComboBox.getSelectionModel().select(
                supplierOptions.stream()
                    .filter(s -> s.getSupplierID().equals(updatedSupplier.getSupplierID()))
                    .findFirst()
                    .orElse(null)
            );
            setStatus("Updated supplier " + updatedSupplier.getSupplierID() + ".", false);
        } catch (IllegalArgumentException exception) {
            showError("Unable to update supplier", exception.getMessage());
        } catch (DatabaseException exception) {
            showDatabaseError("Unable to update supplier", exception);
        }
    }

    /**
     * Populate existing supplier into form.
     */
    private void loadSupplierIntoForm(Supplier supplier) {
        if (supplier == null) {
            return;
        }

        supplierIdFormField.setText(supplier.getSupplierID());
        supplierNameFormField.setText(supplier.getName());
        supplierEmailFormField.setText(supplier.getEmail());
        supplierContactFormField.setText(supplier.getContactName());
        supplierAddressFormField.setText(supplier.getAddress());
    }

    /**
     * Clear supplier form.
     */
    private void clearSupplierForm() {
        supplierIdFormField.clear();
        supplierNameFormField.clear();
        supplierEmailFormField.clear();
        supplierContactFormField.clear();
        supplierAddressFormField.clear();

        if (supplierTable != null) {
            supplierTable.getSelectionModel().clearSelection();
        }
    }

    /**
     * Select supplier.
     */
    private void selectSupplierById(String supplierID) {
        for (Supplier supplier : supplierRows) {
            if (supplier.getSupplierID().equals(supplierID)) {
                supplierTable.getSelectionModel().select(supplier);
                return;
            }
        }
    }

    /**
     * Confirms that the selected shipment was received and updates inventory quantities.
     */
    private void receiveSelectedShipment() {
        ShipmentRecord selectedShipment = shipmentTable.getSelectionModel().getSelectedItem();

        if (selectedShipment == null) {
            showError("No shipment selected", "Choose a shipment before confirming receipt.");
            return;
        }

        try {
            shipmentService.receiveShipment(selectedShipment.getShipmentID());
            refreshTables(inventoryService.viewInventory());
            showOpenShipments("Received shipment " + selectedShipment.getShipmentID()
                    + " and updated inventory quantities.");
        } catch (IllegalArgumentException exception) {
            showError("Unable to receive shipment", exception.getMessage());
        } catch (DatabaseException exception) {
            showDatabaseError("Unable to receive shipment", exception);
        }
    }

    /**
     * Reads the form, creates a new inventory item, and saves it through the service.
     */
    private void addItem() {
        try {
            InventoryItem inventoryItem = buildInventoryItemFromForm();
            inventoryService.addItem(inventoryItem.getProductItem(), inventoryItem);
            refreshTables(inventoryService.viewInventory());
            refreshSuppliers();
            setStatus("Added inventory item " + inventoryItem.getInventoryID() + ".", false);
            clearForm();
        } catch (IllegalArgumentException exception) {
            showError("Unable to add inventory item", exception.getMessage());
        } catch (DatabaseException exception) {
            showDatabaseError("Unable to add inventory item", exception);
        }
    }

    /**
     * Updates the selected inventory item using values currently typed in the form.
     */
    private void updateSelectedQuantity() {
        InventoryItem selectedItem = inventoryTable.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            showError("No item selected", "Choose an item from the inventory table before updating quantity.");
            return;
        }

        try {
            int newQuantity = Integer.parseInt(quantityField.getText().trim());
            int reorderLevel = Integer.parseInt(reorderLevelField.getText().trim());

            if (newQuantity < 0 || reorderLevel < 0) {
                throw new IllegalArgumentException("Quantity and reorder level cannot be negative.");
            }

            String inventoryID = requireValue(inventoryIdField, "Inventory ID");
            if (!inventoryID.equals(selectedItem.getInventoryID())) {
                throw new IllegalArgumentException("Inventory ID cannot be changed while updating an item.");
            }

            Supplier supplier = supplierComboBox.getValue();
                if (supplier == null) {
                    throw new IllegalArgumentException("Supplier is required.");
                }
            double pricePerItem = parsePricePerItem();
            ProductItem product = new ProductItem(requireValue(productIdField, "Product ID"),
                    requireValue(productNameField, "Product name"),
                    descriptionArea.getText().trim(), pricePerItem, supplier);
            InventoryItem updatedItem = new InventoryItem(inventoryID,
                    requireValue(colorField, "Color"), requireValue(sizeField, "Size"),
                    newQuantity, reorderLevel, product);

            inventoryService.updateInventoryItem(updatedItem);

            refreshTables(inventoryService.viewInventory());
            refreshSuppliers();
            selectItemByInventoryId(inventoryID);
            setStatus("Updated inventory item " + inventoryID + ".", false);
        } catch (NumberFormatException exception) {
            showError("Invalid number", "Quantity and reorder level must be whole numbers.");
        } catch (IllegalArgumentException exception) {
            showError("Unable to update item", exception.getMessage());
        } catch (DatabaseException exception) {
            showDatabaseError("Unable to update item", exception);
        }
    }


    /**
     * Removes the currently selected inventory item.
     */
    private void removeSelectedItem() {
        InventoryItem selectedItem = inventoryTable.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            showError("No item selected", "Choose an item from the inventory table before removing it.");
            return;
        }

        try {
            inventoryService.removeItem(selectedItem.getInventoryID());
            refreshTables(inventoryService.viewInventory());
            refreshSuppliers();
            clearForm();
            setStatus("Removed inventory item " + selectedItem.getInventoryID() + ".", false);
        } catch (IllegalArgumentException exception) {
            showError("Unable to remove item", exception.getMessage());
        } catch (DatabaseException exception) {
            showDatabaseError("Unable to remove item", exception);
        }
    }

    /**
     * Replaces table rows and updates the total item count.
     *
     * @param visibleItems rows that should appear in the table
     */
    private void refreshTables(List<InventoryItem> visibleItems) {
        inventoryRows.setAll(visibleItems);
        totalItemsValue.setText(String.valueOf(inventoryService.countInventoryItems()));
    }

    /**
     * Loads every inventory item from the service and shows a status message.
     *
     * @param statusMessage message shown after a successful load
     */
    private void showAllInventory(String statusMessage) {
        try {
            refreshTables(inventoryService.viewInventory());
            showOpenShipments(null);
            setStatus(statusMessage, false);
        } catch (DatabaseException exception) {
            exception.printStackTrace();
            showDatabaseError("Unable to load inventory", exception);
        }
    }

    /**
     * Loads shipment history from the service and optionally shows a status message.
     *
     * @param statusMessage message shown after a successful load, or null to leave footer unchanged
     */
    private void showOpenShipments(String statusMessage) {
        try {
            shipmentRows.setAll(shipmentService.viewOpenShipments());
            if (statusMessage != null) {
                setStatus(statusMessage, false);
            }
        } catch (DatabaseException exception) {
            showDatabaseError("Unable to load shipments", exception);
        }
    }

    /**
     * Updates the shared header search prompt to match the active tab.
     */
    private void updateSearchPrompt() {
        if (isShipmentsTabActive()) {
            searchField.setPromptText(SHIPMENT_SEARCH_PROMPT);
        } else if (isSuppliersTabActive()) {
            searchField.setPromptText(SUPPLIER_SEARCH_PROMPT);
        } else {
            searchField.setPromptText(INVENTORY_SEARCH_PROMPT);
        }
    }

    /**
     * Checks whether the supplier tab is currently selected.
     */
    private boolean isSuppliersTabActive() {
        return mainTabs != null && mainTabs.getSelectionModel().getSelectedIndex() == 1;
    }

    /**
     * Checks whether the shipment tab is currently selected.
     */
    private boolean isShipmentsTabActive() {
        return mainTabs != null && mainTabs.getSelectionModel().getSelectedIndex() == 2;
    }

    /**
     * Selects a table row after reloading from the database.
     *
     * @param inventoryID inventory ID to select
     */
    private void selectItemByInventoryId(String inventoryID) {
        for (InventoryItem item : inventoryRows) {
            if (item.getInventoryID().equals(inventoryID)) {
                inventoryTable.getSelectionModel().select(item);
                return;
            }
        }
    }

    /**
     * Copies the selected table row into the form fields.
     *
     * @param inventoryItem selected row, or null when nothing is selected
     */
    private void loadSelectedIntoForm(InventoryItem inventoryItem) {
        if (inventoryItem == null) {
            return;
        }

        ProductItem product = inventoryItem.getProductItem();
        Supplier supplier = product.getSupplier();

        inventoryIdField.setText(inventoryItem.getInventoryID());
        productIdField.setText(product.getProductID());
        productNameField.setText(product.getProductName());
        pricePerItemField.setText(String.format("%.2f", product.getPricePerItem()));
        descriptionArea.setText(product.getDescription());
        supplierComboBox.getSelectionModel().select(
            supplierOptions.stream()
                .filter(s -> s.getSupplierID().equals(supplier.getSupplierID()))
                .findFirst()
                .orElse(supplier)
        );
        colorField.setText(inventoryItem.getColor());
        sizeField.setText(inventoryItem.getSize());
        quantityField.setText(String.valueOf(inventoryItem.getQuantityOnHand()));
        reorderLevelField.setText(String.valueOf(inventoryItem.getReorderLevel()));
    }

    /**
     * Clears every form field and removes the current table selection.
     */
    private void clearForm() {
        inventoryIdField.clear();
        productIdField.clear();
        productNameField.clear();
        pricePerItemField.clear();
        descriptionArea.clear();
        supplierComboBox.getSelectionModel().clearSelection();
        colorField.clear();
        sizeField.clear();
        quantityField.clear();
        reorderLevelField.clear();
        inventoryTable.getSelectionModel().clearSelection();
    }

    /**
     * Converts form field values into an InventoryItem object.
     *
     * @return inventory item ready to be validated and saved
     */
    private InventoryItem buildInventoryItemFromForm() {
        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());
            int reorderLevel = Integer.parseInt(reorderLevelField.getText().trim());

            if (quantity < 0 || reorderLevel < 0) {
                throw new IllegalArgumentException("Quantity and reorder level cannot be negative.");
            }

            Supplier supplier = buildSupplierFromForm();
            double pricePerItem = parsePricePerItem();

            ProductItem product = new ProductItem(
                requireValue(productIdField, "Product ID"),
                requireValue(productNameField, "Product name"),
                descriptionArea.getText().trim(),
                pricePerItem,
                supplier
            );

            return new InventoryItem(
                requireValue(inventoryIdField, "Inventory ID"),
                requireValue(colorField, "Color"),
                requireValue(sizeField, "Size"),
                quantity,
                reorderLevel,
                product
            );
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Quantity and reorder level must be whole numbers.");
        }
    }
    
    private void reorderSelectedItem() {
        InventoryItem selectedItem = inventoryTable.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            showError("No item selected", "Select an item to reorder.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reorder Item");
        dialog.setHeaderText("Enter quantity to reorder:");
        dialog.setContentText("Quantity:");

        dialog.showAndWait().ifPresent(input -> {
            try {
                int quantity = Integer.parseInt(input);

                if (quantity <= 0) {
                    throw new IllegalArgumentException("Quantity must be greater than 0.");
                }

                ProductItem product = selectedItem.getProductItem();
                Supplier supplier = product.getSupplier();
                double totalPrice = product.getPricePerItem() * quantity;

                Alert receipt = new Alert(Alert.AlertType.CONFIRMATION);
                receipt.setTitle("Confirm Reorder");
                receipt.setHeaderText("Confirm purchase order");
                receipt.setContentText(
                        "Inventory ID: " + selectedItem.getInventoryID()
                                + "\nProduct ID: " + product.getProductID()
                                + "\nProduct Name: " + product.getProductName()
                                + "\nSize: " + selectedItem.getSize()
                                + "\nColor: " + selectedItem.getColor()
                                + "\nQuantity Purchasing: " + quantity
                                + "\nSupplier: " + supplier.getName()
                                + "\nTotal Price: " + formatCurrency(totalPrice)
                                + "\n\nDo you want to confirm this order?"
                );

                receipt.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        String shipmentId = shipmentService.createShipment(
                                selectedItem.getInventoryID(),
                                quantity
                        );

                        setStatus("Order placed. Shipment ID: " + shipmentId, false);

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Order Placed");
                        alert.setHeaderText("Your order has been placed.");
                        alert.setContentText("Shipment ID: " + shipmentId);
                        alert.showAndWait();

                        showOpenShipments("New shipment created.");
                    }
                });

            } catch (NumberFormatException e) {
                showError("Invalid input", "Please enter a valid number.");
            } catch (Exception e) {
                showError("Error", e.getMessage());
            }
        });
    }

    /**
     * Resolves the supplier for the current inventory form.
     * Prefers the selected supplier from the combo box and falls back to
     * the supplier detail fields when needed.
     *
     * @return supplier to attach to the product
     */
    private Supplier buildSupplierFromForm() {
        Supplier selectedSupplier = supplierComboBox == null ? null : supplierComboBox.getValue();
        if (selectedSupplier != null) {
            return selectedSupplier;
        }

        return new Supplier(
                requireValue(supplierIdFormField, "Supplier ID"),
                requireValue(supplierAddressFormField, "Supplier address"),
                requireValue(supplierEmailFormField, "Supplier email"),
                requireValue(supplierNameFormField, "Supplier name"),
                requireValue(supplierContactFormField, "Supplier contact"));
    }

    /**
     * Creates a bold label for a form field.
     *
     * @param text label text
     * @return styled label
     */
    private Label createFieldLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("field-label");
        return label;
    }

    private VBox createField(String labelText, Control control) {
        control.setMaxWidth(Double.MAX_VALUE);
        VBox field = new VBox(6, createFieldLabel(labelText), control);
        field.getStyleClass().add("field-stack");
        return field;
    }

    private VBox createField(String labelText, HBox controlRow) {
        controlRow.setMaxWidth(Double.MAX_VALUE);
        VBox field = new VBox(6, createFieldLabel(labelText), controlRow);
        field.getStyleClass().add("field-stack");
        return field;
    }

    /**
     * Creates a text field with placeholder text.
     *
     * @param prompt placeholder shown before the user types
     * @return text field
     */
    private TextField createTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setMaxWidth(Double.MAX_VALUE);
        return field;
    }

    /**
     * Reads and validates a required text field.
     *
     * @param field field to read
     * @param fieldName user-facing field name for error messages
     * @return trimmed field value
     */
    private String requireValue(TextField field, String fieldName) {
        String value = field.getText().trim();

        if (value.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }

        return value;
    }

    /**
     * Creates a primary action button.
     *
     * @param text button label
     * @return styled button
     */
    private Button createPrimaryButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("primary-button");
        return button;
    }

    /**
     * Creates a secondary action button.
     *
     * @param text button label
     * @return styled button
     */
    private Button createSecondaryButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("secondary-button");
        return button;
    }

    private void applyStylesheet(Scene scene) {
        URL stylesheet = getClass().getResource(STYLESHEET);
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }
    }

    private void applyEqualColumns(GridPane grid, int count) {
        grid.getColumnConstraints().clear();
        for (int i = 0; i < count; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(100.0 / count);
            column.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(column);
        }
    }

    /**
     * Updates the footer status message and color.
     *
     * @param message message to display
     * @param error true when the message should use the error color
     */
    private void setStatus(String message, boolean error) {
        statusLabel.setText(message);
        statusLabel.setTextFill(error ? Color.web("#b42318") : Color.web("#4f6479"));
    }

    /**
     * Shows an error dialog and mirrors the error in the footer.
     *
     * @param title dialog title
     * @param message error message
     */
    private void showError(String title, String message) {
        setStatus(message, true);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void generateLowStockPdf() {
    try {
        List<InventoryItem> lowStock = inventoryService.getLowStockItems();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Low Stock Report");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

                File file = fileChooser.showSaveDialog(inventoryTable.getScene().getWindow());

        if (file != null) {
            generateLowStockPDF.generateLowStockReport(lowStock, file.getAbsolutePath());
            setStatus("Low stock PDF generated.", false);
        }

    } catch (IOException e) {
        showError("PDF Error", "Failed to generate PDF report.");
    }
}

    /**
     * Shows a database-specific error with setup guidance.
     *
     * @param title dialog title
     * @param exception database exception from the repository layer
     */
    private void showDatabaseError(String title, DatabaseException exception) {
        showError(title, exception.getMessage()
                + "\n\nCheck that MySQL is running and database/schema.sql has been loaded.");
    }

    /**
     * Parses the product price field.
     */
    private double parsePricePerItem() {
        try {
            double pricePerItem = Double.parseDouble(pricePerItemField.getText().trim());
            if (pricePerItem < 0) {
                throw new IllegalArgumentException("Price per item cannot be negative.");
            }
            return pricePerItem;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Price per item must be a valid number.");
        }
    }

    /**
     * Formats nullable dates for table display.
     */
    private String formatDate(java.time.LocalDate date) {
        return date == null ? "" : date.toString();
    }

    /**
     * Formats money values for table display.
     */
    private String formatCurrency(double amount) {
        return String.format("$%.2f", amount);
    }


    // public static void main(String[] args) {
    //     launch(args);
    // }

    @FunctionalInterface
    private interface ValueProvider<T> {
        /**
         * Extracts a value from an inventory item for a table column.
         *
         * @param item table row item
         * @return value displayed in the table cell
         */
        T get(InventoryItem item);
    }

    @FunctionalInterface
    private interface IntValueProvider {
        /**
         * Extracts an integer value from an inventory item for a table column.
         *
         * @param item table row item
         * @return number displayed in the table cell
         */
        int get(InventoryItem item);
    }

    @FunctionalInterface
    private interface ShipmentValueProvider<T> {
        T get(ShipmentRecord item);
    }

    @FunctionalInterface
    private interface ShipmentIntValueProvider {
        int get(ShipmentRecord item);
    }

}
