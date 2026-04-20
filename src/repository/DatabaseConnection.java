package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Centralizes database connection settings for the application.
 */
public final class DatabaseConnection {
    /**
     * Default MySQL URL used when DB_URL is not set in the environment.
     */
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/department_store_inventory?useSSL=false&allowPublicKeyRetrieval=true";

    /**
     * Default MySQL username used when DB_USER is not set.
     */
    private static final String DEFAULT_USER = "root";

    /**
     * Default MySQL password used when DB_PASSWORD is not set.
     */
    private static final String DEFAULT_PASSWORD = "";

    /**
     * Prevents creating DatabaseConnection objects because this class only has static helpers.
     */
    private DatabaseConnection() {
    }

    /**
     * Opens a new connection to MySQL using environment variables or local defaults.
     *
     * @return a live JDBC connection
     * @throws SQLException if the database cannot be reached or credentials are invalid
     */
    public static Connection getConnection() throws SQLException {
        String url = System.getenv().getOrDefault("DB_URL", DEFAULT_URL);
        String user = System.getenv().getOrDefault("DB_USER", DEFAULT_USER);
        String password = System.getenv().getOrDefault("DB_PASSWORD", DEFAULT_PASSWORD);

        return DriverManager.getConnection(url, user, password);
    }
}
