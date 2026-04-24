package repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.ShipmentRecord;

/**
 * Persists shipment data in MySQL.
 */
public class ShipmentRepository {
    private static final String SHIPMENT_SELECT = """
            SELECT
                sh.shipment_id,
                sh.purchase_order_id,
                si.inventory_id,
                p.product_name,
                s.name AS supplier_name,
                po.order_date AS purchase_date,
                sh.shipment_date,
                sh.delivery_date,
                sh.shipment_status,
                si.shipment_quantity,
                poi.cost_per_item,
                poi.cost_per_item * si.shipment_quantity AS total_price
            FROM shipments sh
            JOIN purchase_orders po ON sh.purchase_order_id = po.purchase_order_id
            JOIN suppliers s ON sh.supplier_id = s.supplier_id
            JOIN shipment_items si ON sh.shipment_id = si.shipment_id
            LEFT JOIN purchase_order_items poi
                ON sh.purchase_order_id = poi.purchase_order_id
               AND si.inventory_id = poi.inventory_id
            JOIN inventory_items i ON si.inventory_id = i.inventory_id
            JOIN products p ON i.product_id = p.product_id
            """;

    /**
     * Loads all shipments for the shipment history.
     */
    public List<ShipmentRecord> findOpenShipments() {
        String sql = SHIPMENT_SELECT + """
                 ORDER BY sh.shipment_date DESC, sh.shipment_id, si.shipment_item_id
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return mapShipmentRecords(resultSet);
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to load shipments from the database.", exception);
        }
    }

    /**
     * Searches shipment history by shipment, order, item, product, supplier, or status.
     */
    public List<ShipmentRecord> searchOpenShipments(String keyword) {
        String sql = SHIPMENT_SELECT + """
                 WHERE LOWER(sh.shipment_id) LIKE ?
                    OR LOWER(sh.purchase_order_id) LIKE ?
                    OR LOWER(si.inventory_id) LIKE ?
                    OR LOWER(p.product_name) LIKE ?
                    OR LOWER(s.name) LIKE ?
                    OR LOWER(sh.shipment_status) LIKE ?
                 ORDER BY sh.shipment_date DESC, sh.shipment_id, si.shipment_item_id
                """;
        String pattern = "%" + keyword.trim().toLowerCase() + "%";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int index = 1; index <= 6; index++) {
                statement.setString(index, pattern);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                return mapShipmentRecords(resultSet);
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to search shipments in the database.", exception);
        }
    }

    /**
     * Receives a shipment, adds shipped quantities to inventory, and hides it from the open list.
     */
    public boolean receiveShipment(String shipmentID, LocalDate deliveryDate) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            final boolean[] received = new boolean[1];
            runInTransaction(connection, () -> {
                if (isShipmentReceived(connection, shipmentID)) {
                    return;
                }

                received[0] = markShipmentReceived(connection, shipmentID, deliveryDate);
                if (received[0]) {
                    addShipmentQuantitiesToInventory(connection, shipmentID);
                    markPurchaseOrderReceived(connection, shipmentID);
                }
            });
            return received[0];
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to receive shipment in the database.", exception);
        }
    }

    private List<ShipmentRecord> mapShipmentRecords(ResultSet resultSet) throws SQLException {
        List<ShipmentRecord> shipments = new ArrayList<>();

        while (resultSet.next()) {
            shipments.add(new ShipmentRecord(resultSet.getString("shipment_id"),
                    resultSet.getString("purchase_order_id"),
                    resultSet.getString("inventory_id"),
                    resultSet.getString("product_name"),
                    resultSet.getString("supplier_name"),
                    toLocalDate(resultSet.getDate("purchase_date")),
                    toLocalDate(resultSet.getDate("shipment_date")),
                    toLocalDate(resultSet.getDate("delivery_date")),
                    resultSet.getString("shipment_status"),
                    resultSet.getInt("shipment_quantity"),
                    resultSet.getDouble("cost_per_item"),
                    resultSet.getDouble("total_price")));
        }

        return shipments;
    }

    private LocalDate toLocalDate(Date date) {
        return date == null ? null : date.toLocalDate();
    }

    private boolean isShipmentReceived(Connection connection, String shipmentID) throws SQLException {
        String sql = "SELECT LOWER(shipment_status) = 'received' AS received FROM shipments WHERE shipment_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, shipmentID);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getBoolean("received");
            }
        }
    }

    private boolean markShipmentReceived(Connection connection, String shipmentID, LocalDate deliveryDate)
            throws SQLException {
        String sql = """
                UPDATE shipments
                SET shipment_status = 'Received',
                    delivery_date = ?
                WHERE shipment_id = ?
                  AND LOWER(shipment_status) <> 'received'
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(deliveryDate));
            statement.setString(2, shipmentID);
            return statement.executeUpdate() > 0;
        }
    }

    private void addShipmentQuantitiesToInventory(Connection connection, String shipmentID) throws SQLException {
        String sql = """
                UPDATE inventory_items i
                JOIN shipment_items si ON i.inventory_id = si.inventory_id
                SET i.quantity_on_hand = i.quantity_on_hand + si.shipment_quantity
                WHERE si.shipment_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, shipmentID);
            statement.executeUpdate();
        }
    }

    private void markPurchaseOrderReceived(Connection connection, String shipmentID) throws SQLException {
        String sql = """
                UPDATE purchase_orders po
                JOIN shipments sh ON po.purchase_order_id = sh.purchase_order_id
                SET po.order_status = 'Received'
                WHERE sh.shipment_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, shipmentID);
            statement.executeUpdate();
        }
    }

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
        void run() throws SQLException;
    }


    public String createShipment(String inventoryId, int quantity) {
        String shipmentId = "SHIP-" + System.currentTimeMillis();
        String purchaseOrderId = "PO-" + System.currentTimeMillis();

        try (Connection connection = DatabaseConnection.getConnection()) {

            runInTransaction(connection, () -> {


                String insertPO = """
                            INSERT INTO purchase_orders (purchase_order_id, order_date, order_status)
                            VALUES (?, CURRENT_DATE, 'Pending')
                        """;

                try (PreparedStatement stmt = connection.prepareStatement(insertPO)) {
                    stmt.setString(1, purchaseOrderId);
                    stmt.executeUpdate();
                }


                String insertShipment = """
                            INSERT INTO shipments (shipment_id, purchase_order_id, supplier_id, shipment_date, shipment_status)
                            SELECT ?, ?, s.supplier_id, CURRENT_DATE, 'Pending'
                            FROM inventory_items i
                            JOIN products p ON i.product_id = p.product_id
                            JOIN suppliers s ON p.supplier_id = s.supplier_id
                            WHERE i.inventory_id = ?
                        """;

                try (PreparedStatement stmt = connection.prepareStatement(insertShipment)) {
                    stmt.setString(1, shipmentId);
                    stmt.setString(2, purchaseOrderId);
                    stmt.setString(3, inventoryId);
                    stmt.executeUpdate();
                }


                String insertItem = """
                            INSERT INTO shipment_items (shipment_item_id, shipment_id, inventory_id, shipment_quantity)
                            VALUES (?, ?, ?, ?)
                        """;

                try (PreparedStatement stmt = connection.prepareStatement(insertItem)) {
                    stmt.setString(1, "ITEM-" + System.currentTimeMillis());
                    stmt.setString(2, shipmentId);
                    stmt.setString(3, inventoryId);
                    stmt.setInt(4, quantity);
                    stmt.executeUpdate();
                }

                String insertPOItem = """
                            INSERT INTO purchase_order_items 
                                (order_item_id, purchase_order_id, inventory_id, cost_per_item, order_quantity)
                            SELECT ?, ?, i.inventory_id, p.product_price, ?
                            FROM inventory_items i
                            JOIN products p ON i.product_id = p.product_id
                            WHERE i.inventory_id = ?
                        """;

                try (PreparedStatement stmt = connection.prepareStatement(insertPOItem)) {
                    stmt.setString(1, "POITEM-" + System.currentTimeMillis());
                    stmt.setString(2, purchaseOrderId);
                    stmt.setInt(3, quantity);
                    stmt.setString(4, inventoryId);
                    stmt.executeUpdate();
                }

            });

            return shipmentId;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException("Failed to create shipment.", e);
        }
    }
}
