package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.InventoryItem;
import model.ProductItem;
import model.Supplier;
import model.Employee;

/**
 * Persists inventory data in MySQL.
 */
public class InventoryRepository {
    /**
     * Base SELECT query that joins inventory rows to their product and supplier details.
     */
    private static final String INVENTORY_SELECT = """
            SELECT
                i.inventory_id,
                i.color,
                i.size,
                i.quantity_on_hand,
                i.reorder_level,
                p.product_id,
                p.product_name,
                p.description,
                p.product_price,
                s.supplier_id,
                s.address,
                s.email,
                s.name,
                s.contact_name
            FROM inventory_items i
            JOIN products p ON i.product_id = p.product_id
            JOIN suppliers s ON p.supplier_id = s.supplier_id
            """;

    /**
     * Loads every inventory item from MySQL.
     *
     * @return inventory items with nested product and supplier objects
     */
    public List<InventoryItem> findAll() {
        String sql = INVENTORY_SELECT + " ORDER BY i.inventory_id";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            return mapInventoryItems(resultSet);
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to load inventory from the database.", exception);
        }
    }

    /**
     * Searches inventory, product, and supplier fields using one keyword.
     *
     * @param keyword text from the dashboard search box
     * @return matching inventory items
     */
    public List<InventoryItem> search(String keyword) {
        String sql = INVENTORY_SELECT + """
                 WHERE LOWER(i.inventory_id) LIKE ?
                    OR LOWER(i.color) LIKE ?
                    OR LOWER(i.size) LIKE ?
                    OR LOWER(p.product_id) LIKE ?
                    OR LOWER(p.product_name) LIKE ?
                    OR LOWER(p.description) LIKE ?
                    OR LOWER(s.name) LIKE ?
                 ORDER BY i.inventory_id
                """;
        String pattern = "%" + keyword.trim().toLowerCase() + "%";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int index = 1; index <= 7; index++) {
                statement.setString(index, pattern);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                return mapInventoryItems(resultSet);
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to search inventory in the database.", exception);
        }
    }

    /**
     * Finds one inventory item by its primary key.
     *
     * @param inventoryID inventory ID such as INV-1001
     * @return matching item, or null when no row exists
     */
    public InventoryItem findByInventoryId(String inventoryID) {
        String sql = INVENTORY_SELECT + " WHERE i.inventory_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, inventoryID);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapInventoryItem(resultSet);
                }
            }

            return null;
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to find inventory item in the database.", exception);
        }
    }

    /**
     * Checks whether an inventory ID is already stored in the database.
     *
     * @param inventoryID inventory ID to check
     * @return true when a matching row exists
     */
    public boolean existsByInventoryId(String inventoryID) {
        String sql = "SELECT 1 FROM inventory_items WHERE inventory_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, inventoryID);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to check inventory item in the database.", exception);
        }
    }

    /**
     * Counts all inventory rows for the summary number in the UI.
     *
     * @return total number of inventory rows
     */
    public int countInventoryItems() {
        String sql = "SELECT COUNT(*) FROM inventory_items";

        try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to count inventory items in the database.", exception);
        }
    }

    /**
     * Inserts a new inventory item and ensures its supplier and product exist first.
     *
     * @param product product details connected to the inventory row
     * @param inventoryItem inventory row to insert
     */
    public void addItem(ProductItem product, InventoryItem inventoryItem) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            runInTransaction(connection, () -> {
                upsertSupplier(connection, product.getSupplier());
                upsertProduct(connection, product);
                insertInventoryItem(connection, product, inventoryItem);
            });
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to add inventory item to the database.", exception);
        }
    }

    /**
     * Deletes an inventory item by ID.
     *
     * @param inventoryID inventory row to delete
     * @return true when a row was deleted
     */
    public boolean removeItem(String inventoryID) {
        String sql = "DELETE FROM inventory_items WHERE inventory_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, inventoryID);
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to remove inventory item from the database.", exception);
        }
    }

    /**
     * Updates inventory, product, and supplier data together.
     *
     * @param inventoryItem updated item containing nested product and supplier details
     * @return true when the inventory row was found and updated
     */
    public boolean updateInventoryItem(InventoryItem inventoryItem) {
        ProductItem product = inventoryItem.getProductItem();

        try (Connection connection = DatabaseConnection.getConnection()) {
            final boolean[] updated = new boolean[1];
            runInTransaction(connection, () -> {
                upsertSupplier(connection, product.getSupplier());
                upsertProduct(connection, product);
                updated[0] = updateInventoryRow(connection, inventoryItem);
            });
            return updated[0];
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to update inventory item in the database.", exception);
        }
    }

    /**
     * Updates only the quantity field for one inventory row.
     *
     * @param inventoryID inventory row to update
     * @param newQuantity replacement quantity
     * @return true when the row was found and updated
     */
    public boolean updateInventoryQuantity(String inventoryID, int newQuantity) {
        String sql = "UPDATE inventory_items SET quantity_on_hand = ? WHERE inventory_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, newQuantity);
            statement.setString(2, inventoryID);
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to update inventory quantity in the database.", exception);
        }
    }

    /**
     * Saves product changes and the product's supplier without touching inventory fields.
     *
     * @param product product details to insert or update
     */
    public void updateProduct(ProductItem product) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            runInTransaction(connection, () -> {
                upsertSupplier(connection, product.getSupplier());
                upsertProduct(connection, product);
            });
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to update product in the database.", exception);
        }
    }

    /**
     * Converts all rows in a ResultSet into InventoryItem objects.
     *
     * @param resultSet JDBC result set returned by a SELECT query
     * @return list of mapped inventory items
     * @throws SQLException if a column cannot be read
     */
    private List<InventoryItem> mapInventoryItems(ResultSet resultSet) throws SQLException {
        List<InventoryItem> items = new ArrayList<>();

        while (resultSet.next()) {
            items.add(mapInventoryItem(resultSet));
        }

        return items;
    }

    /**
     * Converts the current ResultSet row into the object graph used by the UI.
     *
     * @param resultSet positioned result set row
     * @return inventory item with product and supplier attached
     * @throws SQLException if a column cannot be read
     */
    private InventoryItem mapInventoryItem(ResultSet resultSet) throws SQLException {
        Supplier supplier = new Supplier(resultSet.getString("supplier_id"), resultSet.getString("address"),
                resultSet.getString("email"), resultSet.getString("name"), resultSet.getString("contact_name"));
        ProductItem product = new ProductItem(resultSet.getString("product_id"), resultSet.getString("product_name"),
                resultSet.getString("description"), resultSet.getDouble("product_price"), supplier);

        return new InventoryItem(resultSet.getString("inventory_id"), resultSet.getString("color"),
                resultSet.getString("size"), resultSet.getInt("quantity_on_hand"),
                resultSet.getInt("reorder_level"), product);
    }

    /**
     * Inserts the inventory row after related supplier and product rows exist.
     *
     * @param connection transaction connection
     * @param product product linked by product_id
     * @param inventoryItem inventory values to store
     * @throws SQLException if MySQL rejects the insert
     */
    private void insertInventoryItem(Connection connection, ProductItem product, InventoryItem inventoryItem)
            throws SQLException {
        String sql = """
                INSERT INTO inventory_items
                    (inventory_id, product_id, color, size, quantity_on_hand, reorder_level)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, inventoryItem.getInventoryID());
            statement.setString(2, product.getProductID());
            statement.setString(3, inventoryItem.getColor());
            statement.setString(4, inventoryItem.getSize());
            statement.setInt(5, inventoryItem.getQuantityOnHand());
            statement.setInt(6, inventoryItem.getReorderLevel());
            statement.executeUpdate();
        }
    }

    /**
     * Updates the inventory table columns for an existing inventory ID.
     *
     * @param connection transaction connection
     * @param inventoryItem updated inventory values
     * @return true when MySQL updated a row
     * @throws SQLException if MySQL rejects the update
     */
    private boolean updateInventoryRow(Connection connection, InventoryItem inventoryItem) throws SQLException {
        String sql = """
                UPDATE inventory_items
                SET product_id = ?,
                    color = ?,
                    size = ?,
                    quantity_on_hand = ?,
                    reorder_level = ?
                WHERE inventory_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, inventoryItem.getProductItem().getProductID());
            statement.setString(2, inventoryItem.getColor());
            statement.setString(3, inventoryItem.getSize());
            statement.setInt(4, inventoryItem.getQuantityOnHand());
            statement.setInt(5, inventoryItem.getReorderLevel());
            statement.setString(6, inventoryItem.getInventoryID());
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Inserts a supplier or updates it when the supplier ID already exists.
     *
     * @param connection transaction connection
     * @param supplier supplier values to save
     * @throws SQLException if MySQL rejects the insert or update
     */
    private void upsertSupplier(Connection connection, Supplier supplier) throws SQLException {
        String sql = """
                INSERT INTO suppliers (supplier_id, address, email, name, contact_name)
                VALUES (?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    address = ?,
                    email = ?,
                    name = ?,
                    contact_name = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, supplier.getSupplierID());
            statement.setString(2, supplier.getAddress());
            statement.setString(3, supplier.getEmail());
            statement.setString(4, supplier.getName());
            statement.setString(5, supplier.getContactName());
            statement.setString(6, supplier.getAddress());
            statement.setString(7, supplier.getEmail());
            statement.setString(8, supplier.getName());
            statement.setString(9, supplier.getContactName());
            statement.executeUpdate();
        }
    }

    /**
     * Inserts a product or updates it when the product ID already exists.
     *
     * @param connection transaction connection
     * @param product product values to save
     * @throws SQLException if MySQL rejects the insert or update
     */
    private void upsertProduct(Connection connection, ProductItem product) throws SQLException {
        String sql = """
                INSERT INTO products (product_id, product_name, description, product_price, supplier_id)
                VALUES (?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    product_name = ?,
                    description = ?,
                    product_price = ?,
                    supplier_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, product.getProductID());
            statement.setString(2, product.getProductName());
            statement.setString(3, product.getDescription());
            statement.setDouble(4, product.getPricePerItem());
            statement.setString(5, product.getSupplier().getSupplierID());
            statement.setString(6, product.getProductName());
            statement.setString(7, product.getDescription());
            statement.setDouble(8, product.getPricePerItem());
            statement.setString(9, product.getSupplier().getSupplierID());
            statement.executeUpdate();
        }
    }

    /**
     * Runs several SQL statements as one unit so partial saves are rolled back.
     *
     * @param connection open JDBC connection
     * @param operation database work to run inside the transaction
     * @throws SQLException if the operation fails and must be rolled back
     */
    private void runInTransaction(Connection connection, DatabaseOperation operation) throws SQLException {
        boolean previousAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);

        try {
            operation.run();
            connection.commit();
        } catch (SQLException exception) {
            connection.rollback();
            throw exception;
        } finally {
            connection.setAutoCommit(previousAutoCommit);
        }
    }

    @FunctionalInterface
    private interface DatabaseOperation {
        /**
         * Executes database work that can throw SQLException.
         *
         * @throws SQLException if any SQL statement fails
         */
        void run() throws SQLException;
    }

    // suppliers
    public List<Supplier> findAllSuppliers() {
        String sql = """
            SELECT supplier_id, address, email, name, contact_name
            FROM suppliers
            ORDER BY name, supplier_id
            """;

        List<Supplier> suppliers = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                suppliers.add(new Supplier(
                    resultSet.getString("supplier_id"),
                    resultSet.getString("address"),
                    resultSet.getString("email"),
                    resultSet.getString("name"),
                    resultSet.getString("contact_name")
                ));
            }

            return suppliers;
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to load suppliers from the database.", exception);
        }
    }

    public List<Supplier> searchSuppliers(String keyword) {
        String sql = """
            SELECT supplier_id, address, email, name, contact_name
            FROM suppliers
            WHERE LOWER(supplier_id) LIKE ?
            OR LOWER(name) LIKE ?
            OR LOWER(email) LIKE ?
            OR LOWER(contact_name) LIKE ?
            OR LOWER(address) LIKE ?
            ORDER BY name, supplier_id
            """;

        String pattern = "%" + keyword.trim().toLowerCase() + "%";
        List<Supplier> suppliers = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            for (int i = 1; i <= 5; i++) {
                statement.setString(i, pattern);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    suppliers.add(new Supplier(
                        resultSet.getString("supplier_id"),
                        resultSet.getString("address"),
                        resultSet.getString("email"),
                        resultSet.getString("name"),
                        resultSet.getString("contact_name")
                    ));
                }
            }

            return suppliers;
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to search suppliers in the database.", exception);
        }
    }

    public Supplier findSupplierById(String supplierID) {
        String sql = """
            SELECT supplier_id, address, email, name, contact_name
            FROM suppliers
            WHERE supplier_id = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, supplierID);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Supplier(
                        resultSet.getString("supplier_id"),
                        resultSet.getString("address"),
                        resultSet.getString("email"),
                        resultSet.getString("name"),
                        resultSet.getString("contact_name")
                    );
                }
                return null;
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to find supplier in the database.", exception);
        }
    }

    public boolean existsBySupplierId(String supplierID) {
        String sql = "SELECT 1 FROM suppliers WHERE supplier_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, supplierID);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to check supplier in the database.", exception);
        }
    }

    public void addSupplier(Supplier supplier) {
        String sql = """
            INSERT INTO suppliers (supplier_id, address, email, name, contact_name)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, supplier.getSupplierID());
            statement.setString(2, supplier.getAddress());
            statement.setString(3, supplier.getEmail());
            statement.setString(4, supplier.getName());
            statement.setString(5, supplier.getContactName());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to add supplier to the database.", exception);
        }
    }

    public boolean updateSupplier(Supplier supplier) {
        String sql = """
            UPDATE suppliers
            SET address = ?, email = ?, name = ?, contact_name = ?
            WHERE supplier_id = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, supplier.getAddress());
            statement.setString(2, supplier.getEmail());
            statement.setString(3, supplier.getName());
            statement.setString(4, supplier.getContactName());
            statement.setString(5, supplier.getSupplierID());

            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to update supplier in the database.", exception);
        }
    }
}
