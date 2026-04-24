package ui;

import java.io.IOException;
import java.util.List;
// import javafx.application.Application;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Employee;
import model.InventoryItem;
import model.ProductItem;
import model.Supplier;
import repository.DatabaseException;
import service.InventoryService;
import service.SessionManager;
import report.generateLowStockPDF;
import java.io.File;

/**
 * JavaFX dashboard for the inventory subsystem.
 */
public class InventoryDashboardApp  {
    /**
     * Service used by the UI for all inventory operations.
     */
    private final InventoryService inventoryService = new InventoryService();

    /**
     * JavaFX list that backs the table; changing this list refreshes the table rows.
     */
    private final ObservableList<InventoryItem> inventoryRows = FXCollections.observableArrayList();

    /**
     * Main table that displays inventory rows from MySQL.
     */
    private TableView<InventoryItem> inventoryTable;

    /**
     * Search box in the header.
     */
    private TextField searchField;

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
    private TextArea descriptionArea;
    private TextField supplierIdField;
    private TextField supplierNameField;
    private TextField supplierEmailField;
    private TextField supplierContactField;
    private TextField supplierAddressField;
    private TextField colorField;
    private TextField sizeField;
    private TextField quantityField;
    private TextField reorderLevelField;

    /**
     * JavaFX entry point that builds the window and loads inventory from MySQL.
     *
     * @param stage primary application window
     */
    // @Override
    public Scene createScene(Stage stage, InventorySubsystemApp app) {
        BorderPane root = new BorderPane();
        root.setTop(createHeader(app));
        root.setCenter(createContent());
        root.setBottom(createFooter());
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f7f8fc, #eef2f7);");

        Scene scene = new Scene(root, 1400, 860);
        // stage.setTitle("Department Store Inventory Dashboard");
        // stage.setScene(scene);
        // stage.show();

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
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #17324d;");

        searchField = new TextField();
        searchField.setPromptText("Search by inventory ID, product, description, supplier, color, or size");
        searchField.setPrefWidth(460);
        searchField.setOnAction(event -> applySearch());

        Button searchButton = createPrimaryButton("Search");
        searchButton.setOnAction(event -> applySearch());

        Button resetButton = createSecondaryButton("Show All");
        resetButton.setOnAction(event -> {
            searchField.clear();
            showAllInventory("Showing all inventory items.");
        });

        Employee currentUser = SessionManager.getCurrentUser();

        Label userLabel = new Label(
            currentUser != null
                ? "Logged in: " + currentUser.getFirstName()
                : "Not logged in"
         );

        userLabel.setStyle("-fx-text-fill: #17324d; -fx-font-weight: bold;");

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

        HBox toolbar = new HBox(10, searchField, searchButton, resetButton, spacer, createSummaryCard("Items"), sessionBox);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        VBox header = new VBox(10, title, toolbar);
        header.setPadding(new Insets(20, 24, 18, 24));
        header.setStyle("-fx-background-color: white; -fx-border-color: #d7dfeb; -fx-border-width: 0 0 1 0;");
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
        name.setStyle("-fx-font-size: 13px; -fx-text-fill: #5e7389;");

        Label value = new Label("0");
        value.setStyle("-fx-font-size: 34px; -fx-font-weight: bold; -fx-text-fill: #17324d;");

        totalItemsValue = value;

        VBox card = new VBox(4, name, value);
        card.setPadding(new Insets(16, 18, 16, 18));
        card.setMinWidth(138);
        card.setMinHeight(108);
        card.setStyle("-fx-background-color: #f4f7fb; -fx-background-radius: 10; -fx-border-color: #d7dfeb;"
                + " -fx-border-radius: 10;");
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
        workspace.setPadding(new Insets(20));
        VBox.setVgrow(inventoryTable, Priority.ALWAYS);

        BorderPane inventoryTabLayout = new BorderPane();
        inventoryTabLayout.setCenter(workspace);
        ScrollPane formScrollPane = createFormScrollPane();
        inventoryTabLayout.setRight(formScrollPane);
        BorderPane.setMargin(formScrollPane, new Insets(20, 20, 20, 0));
        return inventoryTabLayout;
    }

    /**
     * Builds the toolbar of inventory actions.
     *
     * @return toolbar with add, update, remove, load, and clear buttons
     */
    private ToolBar createInventoryToolbar() {
        Button addButton = createPrimaryButton("Add Item");
        addButton.setOnAction(event -> addItem());

        Button updateButton = createSecondaryButton("Update Quantity");
        updateButton.setOnAction(event -> updateSelectedQuantity());

        Button pdfButton = createPrimaryButton("Export Low Stock PDF");
        pdfButton.setOnAction(e -> generateLowStockPdf());

        Button removeButton = createSecondaryButton("Remove Selected");
        removeButton.setOnAction(event -> removeSelectedItem());

        Button loadSelectedButton = createSecondaryButton("Load Into Form");
        loadSelectedButton.setOnAction(event -> loadSelectedIntoForm(inventoryTable.getSelectionModel().getSelectedItem()));

        Button clearButton = createSecondaryButton("Clear Form");
        clearButton.setOnAction(event -> clearForm());

        return new ToolBar(addButton, updateButton, pdfButton, removeButton, new Separator(), loadSelectedButton, clearButton);
    }

    /**
     * Builds the form used to add a new item or edit the selected item.
     *
     * @return form panel layout
     */
    private VBox createFormPanel() {
        Label title = new Label("Inventory Item Form");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #17324d;");

        Label description = new Label(
                "Use this form to add a new catalog item or update the quantity for an existing one.");
        description.setWrapText(true);
        description.setStyle("-fx-font-size: 13px; -fx-text-fill: #4f6479;");

        inventoryIdField = createTextField("INV-1004");
        productIdField = createTextField("PROD-1004");
        productNameField = createTextField("Wool Coat");
        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Brief product description");
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setWrapText(true);

        supplierIdField = createTextField("SUP-204");
        supplierNameField = createTextField("Bright Apparel");
        supplierEmailField = createTextField("buyer@supplier.com");
        supplierContactField = createTextField("Jordan Lee");
        supplierAddressField = createTextField("123 Supply Rd, Salt Lake City, UT");
        colorField = createTextField("Gray");
        sizeField = createTextField("M");
        quantityField = createTextField("25");
        reorderLevelField = createTextField("10");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.add(createFieldLabel("Inventory ID"), 0, 0);
        grid.add(inventoryIdField, 1, 0);
        grid.add(createFieldLabel("Product ID"), 0, 1);
        grid.add(productIdField, 1, 1);
        grid.add(createFieldLabel("Product Name"), 0, 2);
        grid.add(productNameField, 1, 2);
        grid.add(createFieldLabel("Description"), 0, 3);
        grid.add(descriptionArea, 1, 3);
        grid.add(createFieldLabel("Supplier ID"), 0, 4);
        grid.add(supplierIdField, 1, 4);
        grid.add(createFieldLabel("Supplier Name"), 0, 5);
        grid.add(supplierNameField, 1, 5);
        grid.add(createFieldLabel("Supplier Email"), 0, 6);
        grid.add(supplierEmailField, 1, 6);
        grid.add(createFieldLabel("Contact Name"), 0, 7);
        grid.add(supplierContactField, 1, 7);
        grid.add(createFieldLabel("Supplier Address"), 0, 8);
        grid.add(supplierAddressField, 1, 8);
        grid.add(createFieldLabel("Color"), 0, 9);
        grid.add(colorField, 1, 9);
        grid.add(createFieldLabel("Size"), 0, 10);
        grid.add(sizeField, 1, 10);
        grid.add(createFieldLabel("Quantity"), 0, 11);
        grid.add(quantityField, 1, 11);
        grid.add(createFieldLabel("Reorder Level"), 0, 12);
        grid.add(reorderLevelField, 1, 12);

        ColumnConstraintsBuilder.apply(grid);

        Button saveButton = createPrimaryButton("Save Item");
        saveButton.setMaxWidth(Double.MAX_VALUE);
        saveButton.setOnAction(event -> addItem());

        Button quantityButton = createSecondaryButton("Apply Quantity Update");
        quantityButton.setMaxWidth(Double.MAX_VALUE);
        quantityButton.setOnAction(event -> updateSelectedQuantity());

        Button clearButton = createSecondaryButton("Reset Form");
        clearButton.setMaxWidth(Double.MAX_VALUE);
        clearButton.setOnAction(event -> clearForm());

        VBox actions = new VBox(10, saveButton, quantityButton, clearButton);

        VBox panel = new VBox(14, title, description, grid, actions);
        panel.setPrefWidth(400);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 14; -fx-border-color: #d7dfeb;"
                + " -fx-border-radius: 14;");
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
        scrollPane.setPrefWidth(430);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0;");
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
        table.getColumns().add(createTextColumn("Inventory ID", item -> item.getInventoryID(), 105));
        table.getColumns().add(createTextColumn("Product ID", item -> item.getProductItem().getProductID(), 95));
        table.getColumns().add(createTextColumn("Product Name", item -> item.getProductItem().getProductName(), 150));
        table.getColumns().add(createTextColumn("Supplier", item -> item.getProductItem().getSupplier().getName(), 140));
        table.getColumns().add(createTextColumn("Color", InventoryItem::getColor, 90));
        table.getColumns().add(createTextColumn("Size", InventoryItem::getSize, 80));
        table.getColumns().add(createIntegerColumn("Quantity", InventoryItem::getQuantityOnHand, 90));
        table.getColumns().add(createIntegerColumn("Reorder", InventoryItem::getReorderLevel, 90));
        table.getColumns().add(createTextColumn("Status", item -> item.isLowStock() ? "Low Stock" : "Healthy", 110));
        table.setItems(inventoryRows);
        table.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldItem, newItem) -> loadSelectedIntoForm(newItem));
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
        table.setStyle("-fx-background-color: white; -fx-background-radius: 14; -fx-border-color: #d7dfeb;"
                + " -fx-border-radius: 14;");
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

    /**
     * Builds the footer where status and error messages are shown.
     *
     * @return footer layout
     */
    private HBox createFooter() {
        statusLabel = new Label("Ready.");
        statusLabel.setTextFill(Color.web("#4f6479"));

        HBox footer = new HBox(statusLabel);
        footer.setPadding(new Insets(10, 24, 14, 24));
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setStyle("-fx-background-color: white; -fx-border-color: #d7dfeb; -fx-border-width: 1 0 0 0;");
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
     * Reads the form, creates a new inventory item, and saves it through the service.
     */
    private void addItem() {
        try {
            InventoryItem inventoryItem = buildInventoryItemFromForm();
            inventoryService.addItem(inventoryItem.getProductItem(), inventoryItem);
            refreshTables(inventoryService.viewInventory());
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

            Supplier supplier = buildSupplierFromForm();
            ProductItem product = new ProductItem(requireValue(productIdField, "Product ID"),
                    requireValue(productNameField, "Product name"),
                    descriptionArea.getText().trim(), supplier);
            InventoryItem updatedItem = new InventoryItem(inventoryID,
                    requireValue(colorField, "Color"), requireValue(sizeField, "Size"),
                    newQuantity, reorderLevel, product);

            inventoryService.updateInventoryItem(updatedItem);

            refreshTables(inventoryService.viewInventory());
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
            setStatus(statusMessage, false);
        } catch (DatabaseException exception) {
            exception.printStackTrace();
            showDatabaseError("Unable to load inventory", exception);
        }
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
        descriptionArea.setText(product.getDescription());
        supplierIdField.setText(supplier.getSupplierID());
        supplierNameField.setText(supplier.getName());
        supplierEmailField.setText(supplier.getEmail());
        supplierContactField.setText(supplier.getContactName());
        supplierAddressField.setText(supplier.getAddress());
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
        descriptionArea.clear();
        supplierIdField.clear();
        supplierNameField.clear();
        supplierEmailField.clear();
        supplierContactField.clear();
        supplierAddressField.clear();
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
            ProductItem product = new ProductItem(requireValue(productIdField, "Product ID"),
                    requireValue(productNameField, "Product name"),
                    descriptionArea.getText().trim(), supplier);
            return new InventoryItem(requireValue(inventoryIdField, "Inventory ID"),
                    requireValue(colorField, "Color"), requireValue(sizeField, "Size"),
                    quantity, reorderLevel, product);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Quantity and reorder level must be whole numbers.");
        }
    }

    /**
     * Converts supplier form fields into a Supplier object.
     *
     * @return supplier from the form
     */
    private Supplier buildSupplierFromForm() {
        return new Supplier(requireValue(supplierIdField, "Supplier ID"), requireValue(supplierAddressField, "Supplier address"),
                requireValue(supplierEmailField, "Supplier email"), requireValue(supplierNameField, "Supplier name"),
                requireValue(supplierContactField, "Supplier contact"));
    }

    /**
     * Creates a bold label for a form field.
     *
     * @param text label text
     * @return styled label
     */
    private Label createFieldLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #334a60;");
        return label;
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
        button.setStyle("-fx-background-color: #1d6fdc; -fx-text-fill: white; -fx-font-weight: bold;"
                + " -fx-background-radius: 8;");
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
        button.setStyle("-fx-background-color: #eef3fb; -fx-text-fill: #17324d; -fx-font-weight: bold;"
                + " -fx-background-radius: 8; -fx-border-color: #cdd8e8; -fx-border-radius: 8;");
        return button;
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
     * Launches the JavaFX application.
     *
     * @param args command-line arguments passed by Java
     */
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

    /**
     * Keeps GridPane column setup out of the form-building code.
     */
    private static final class ColumnConstraintsBuilder {
        /**
         * Prevents creating helper objects because this class only has static behavior.
         */
        private ColumnConstraintsBuilder() {
        }

        /**
         * Applies consistent two-column sizing to the form grid.
         *
         * @param grid form grid to configure
         */
        private static void apply(GridPane grid) {
            javafx.scene.layout.ColumnConstraints first = new javafx.scene.layout.ColumnConstraints();
            first.setMinWidth(110);

            javafx.scene.layout.ColumnConstraints second = new javafx.scene.layout.ColumnConstraints();
            second.setHgrow(Priority.ALWAYS);

            grid.getColumnConstraints().addAll(first, second);
        }
    }
}
