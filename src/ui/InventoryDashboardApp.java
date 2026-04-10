package ui;

import java.util.List;
import javafx.application.Application;
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
import javafx.stage.Stage;
import model.InventoryItem;
import model.ProductItem;
import model.Supplier;
import service.InventoryService;

/**
 * JavaFX dashboard for the inventory subsystem.
 */
public class InventoryDashboardApp extends Application {
    private final InventoryService inventoryService = new InventoryService();

    private final ObservableList<InventoryItem> inventoryRows = FXCollections.observableArrayList();

    private TableView<InventoryItem> inventoryTable;
    private TextField searchField;
    private Label statusLabel;
    private Label totalItemsValue;

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

    @Override
    public void start(Stage stage) {
        seedInventory();

        BorderPane root = new BorderPane();
        root.setTop(createHeader());
        root.setCenter(createContent());
        root.setBottom(createFooter());
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f7f8fc, #eef2f7);");

        refreshTables(inventoryService.viewInventory());

        Scene scene = new Scene(root, 1400, 860);
        stage.setTitle("Department Store Inventory Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createHeader() {
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
            refreshTables(inventoryService.viewInventory());
            setStatus("Showing all inventory items.", false);
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox toolbar = new HBox(10, searchField, searchButton, resetButton, spacer, createSummaryCard("Items"));
        toolbar.setAlignment(Pos.CENTER_LEFT);

        VBox header = new VBox(10, title, toolbar);
        header.setPadding(new Insets(20, 24, 18, 24));
        header.setStyle("-fx-background-color: white; -fx-border-color: #d7dfeb; -fx-border-width: 0 0 1 0;");
        return header;
    }

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

    private ToolBar createInventoryToolbar() {
        Button addButton = createPrimaryButton("Add Item");
        addButton.setOnAction(event -> addItem());

        Button updateButton = createSecondaryButton("Update Quantity");
        updateButton.setOnAction(event -> updateSelectedQuantity());

        Button removeButton = createSecondaryButton("Remove Selected");
        removeButton.setOnAction(event -> removeSelectedItem());

        Button loadSelectedButton = createSecondaryButton("Load Into Form");
        loadSelectedButton.setOnAction(event -> loadSelectedIntoForm(inventoryTable.getSelectionModel().getSelectedItem()));

        Button clearButton = createSecondaryButton("Clear Form");
        clearButton.setOnAction(event -> clearForm());

        return new ToolBar(addButton, updateButton, removeButton, new Separator(), loadSelectedButton, clearButton);
    }

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

    private TableView<InventoryItem> createBaseTable() {
        TableView<InventoryItem> table = new TableView<>();
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No inventory items to display."));
        table.setStyle("-fx-background-color: white; -fx-background-radius: 14; -fx-border-color: #d7dfeb;"
                + " -fx-border-radius: 14;");
        return table;
    }

    private TableColumn<InventoryItem, String> createTextColumn(String title, ValueProvider<String> provider, double width) {
        TableColumn<InventoryItem, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(provider.get(cellData.getValue())));
        column.setPrefWidth(width);
        return column;
    }

    private TableColumn<InventoryItem, Number> createIntegerColumn(String title, IntValueProvider provider, double width) {
        TableColumn<InventoryItem, Number> column = new TableColumn<>(title);
        column.setCellValueFactory(cellData -> new ReadOnlyIntegerWrapper(provider.get(cellData.getValue())));
        column.setPrefWidth(width);
        return column;
    }

    private HBox createFooter() {
        statusLabel = new Label("Ready.");
        statusLabel.setTextFill(Color.web("#4f6479"));

        HBox footer = new HBox(statusLabel);
        footer.setPadding(new Insets(10, 24, 14, 24));
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setStyle("-fx-background-color: white; -fx-border-color: #d7dfeb; -fx-border-width: 1 0 0 0;");
        return footer;
    }

    private void applySearch() {
        List<InventoryItem> results = inventoryService.searchInventory(searchField.getText());
        refreshTables(results);
        setStatus("Search returned " + results.size() + " item(s).", false);
    }

    private void addItem() {
        try {
            InventoryItem inventoryItem = buildInventoryItemFromForm();
            inventoryService.addItem(inventoryItem.getProductItem(), inventoryItem);
            refreshTables(inventoryService.viewInventory());
            inventoryTable.getSelectionModel().select(inventoryItem);
            setStatus("Added inventory item " + inventoryItem.getInventoryID() + ".", false);
            clearForm();
        } catch (IllegalArgumentException exception) {
            showError("Unable to add inventory item", exception.getMessage());
        }
    }

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

            inventoryService.updateInventoryQuantity(selectedItem, newQuantity);
            inventoryService.updateItem(selectedItem.getProductItem(), requireValue(productNameField, "Product name"),
                    descriptionArea.getText().trim());
            selectedItem.setColor(requireValue(colorField, "Color"));
            selectedItem.setSize(requireValue(sizeField, "Size"));
            selectedItem.setReorderLevel(reorderLevel);
            selectedItem.getProductItem().setSupplier(buildSupplierFromForm());

            refreshTables(inventoryService.viewInventory());
            inventoryTable.getSelectionModel().select(selectedItem);
            setStatus("Updated quantity for " + selectedItem.getInventoryID() + ".", false);
        } catch (NumberFormatException exception) {
            showError("Invalid number", "Quantity and reorder level must be whole numbers.");
        } catch (IllegalArgumentException exception) {
            showError("Unable to update item", exception.getMessage());
        }
    }

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
        }
    }

    private void refreshTables(List<InventoryItem> visibleItems) {
        inventoryRows.setAll(visibleItems);
        totalItemsValue.setText(String.valueOf(inventoryService.viewInventory().size()));
    }

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

    private Supplier buildSupplierFromForm() {
        return new Supplier(requireValue(supplierIdField, "Supplier ID"), requireValue(supplierAddressField, "Supplier address"),
                requireValue(supplierEmailField, "Supplier email"), requireValue(supplierNameField, "Supplier name"),
                requireValue(supplierContactField, "Supplier contact"));
    }

    private void seedInventory() {
        if (!inventoryService.viewInventory().isEmpty()) {
            return;
        }

        Supplier outerwearSupplier = new Supplier("SUP-201", "411 Summit Ave, Denver, CO", "orders@northernwear.com",
                "Northern Wear", "Taylor Brooks");
        Supplier footwearSupplier = new Supplier("SUP-202", "88 Harbor Blvd, Long Beach, CA", "sales@seasidefoot.com",
                "Seaside Footwear", "Morgan Patel");
        Supplier accessoriesSupplier = new Supplier("SUP-203", "102 Cedar St, Portland, OR", "ops@evergreenacc.com",
                "Evergreen Accessories", "Riley Chen");

        inventoryService.addItem(new ProductItem("PROD-1001", "Winter Parka", "Insulated coat for seasonal display",
                        outerwearSupplier),
                new InventoryItem("INV-1001", "Navy", "L", 12, 10,
                        new ProductItem("PROD-1001", "Winter Parka", "Insulated coat for seasonal display",
                                outerwearSupplier)));
        inventoryService.addItem(new ProductItem("PROD-1002", "Leather Boots", "Water-resistant ankle boots",
                        footwearSupplier),
                new InventoryItem("INV-1002", "Brown", "9", 6, 8,
                        new ProductItem("PROD-1002", "Leather Boots", "Water-resistant ankle boots", footwearSupplier)));
        inventoryService.addItem(new ProductItem("PROD-1003", "Silk Scarf", "Luxury scarf for gift section",
                        accessoriesSupplier),
                new InventoryItem("INV-1003", "Emerald", "One Size", 18, 7,
                        new ProductItem("PROD-1003", "Silk Scarf", "Luxury scarf for gift section", accessoriesSupplier)));
    }

    private Label createFieldLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #334a60;");
        return label;
    }

    private TextField createTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        return field;
    }

    private String requireValue(TextField field, String fieldName) {
        String value = field.getText().trim();

        if (value.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }

        return value;
    }

    private Button createPrimaryButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #1d6fdc; -fx-text-fill: white; -fx-font-weight: bold;"
                + " -fx-background-radius: 8;");
        return button;
    }

    private Button createSecondaryButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #eef3fb; -fx-text-fill: #17324d; -fx-font-weight: bold;"
                + " -fx-background-radius: 8; -fx-border-color: #cdd8e8; -fx-border-radius: 8;");
        return button;
    }

    private void setStatus(String message, boolean error) {
        statusLabel.setText(message);
        statusLabel.setTextFill(error ? Color.web("#b42318") : Color.web("#4f6479"));
    }

    private void showError(String title, String message) {
        setStatus(message, true);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @FunctionalInterface
    private interface ValueProvider<T> {
        T get(InventoryItem item);
    }

    @FunctionalInterface
    private interface IntValueProvider {
        int get(InventoryItem item);
    }

    /**
     * Keeps GridPane column setup out of the form-building code.
     */
    private static final class ColumnConstraintsBuilder {
        private ColumnConstraintsBuilder() {
        }

        private static void apply(GridPane grid) {
            javafx.scene.layout.ColumnConstraints first = new javafx.scene.layout.ColumnConstraints();
            first.setMinWidth(110);

            javafx.scene.layout.ColumnConstraints second = new javafx.scene.layout.ColumnConstraints();
            second.setHgrow(Priority.ALWAYS);

            grid.getColumnConstraints().addAll(first, second);
        }
    }
}
