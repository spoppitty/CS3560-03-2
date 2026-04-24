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
                sh.shipment_date,
                sh.delivery_date,
                sh.shipment_status,
                si.shipment_quantity
            FROM shipments sh
            JOIN suppliers s ON sh.supplier_id = s.supplier_id
            JOIN shipment_items si ON sh.shipment_id = si.shipment_id
            JOIN inventory_items i ON si.inventory_id = i.inventory_id
            JOIN products p ON i.product_id = p.product_id
            """;

    /**
     * Loads all shipments that have not been received yet.
     */
    public List<ShipmentRecord> findOpenShipments() {
        String sql = SHIPMENT_SELECT + """
                 WHERE LOWER(sh.shipment_status) <> 'received'
                 ORDER BY sh.shipment_date, sh.shipment_id, si.shipment_item_id
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
     * Searches open shipments by shipment, order, item, product, supplier, or status.
     */
    public List<ShipmentRecord> searchOpenShipments(String keyword) {
        String sql = SHIPMENT_SELECT + """
                 WHERE LOWER(sh.shipment_status) <> 'received'
                   AND (
                       LOWER(sh.shipment_id) LIKE ?
                    OR LOWER(sh.purchase_order_id) LIKE ?
                    OR LOWER(si.inventory_id) LIKE ?
                    OR LOWER(p.product_name) LIKE ?
                    OR LOWER(s.name) LIKE ?
                    OR LOWER(sh.shipment_status) LIKE ?
                   )
                 ORDER BY sh.shipment_date, sh.shipment_id, si.shipment_item_id
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
                    toLocalDate(resultSet.getDate("shipment_date")),
                    toLocalDate(resultSet.getDate("delivery_date")),
                    resultSet.getString("shipment_status"),
                    resultSet.getInt("shipment_quantity")));
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
}
