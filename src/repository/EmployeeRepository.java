package repository;

import model.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeRepository {

    /**
     * Authenticates a user by username + password
     */
    public Employee authenticate(String username, String password) {
        String sql = """
                SELECT *
                FROM employees
                WHERE username = ? AND password = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapEmployee(rs);
                }
            }

            return null;

        } catch (SQLException e) {
            throw new DatabaseException("Error during authentication", e);
        }
    }

    /**
     * Creates a new employee account
     */
    public void createEmployee(Employee employee) {
        String sql = """
                INSERT INTO employees
                (employee_id, first_name, last_name, username, password, role)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, employee.getEmployeeID());
            stmt.setString(2, employee.getFirstName());
            stmt.setString(3, employee.getLastName());
            stmt.setString(4, employee.getUsername());
            stmt.setString(5, employee.getPassword());
            stmt.setString(6, employee.getRole());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Error creating employee", e);
        }
    }

    /**
     * Deletes an employee (manager only)
     */
    public boolean deleteEmployee(String employeeId) {
        String sql = "DELETE FROM employees WHERE employee_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, employeeId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DatabaseException("Error deleting employee", e);
        }
    }

    /**
     * Checks if username already exists
     */
    public boolean existsByUsername(String username) {
        String sql = "SELECT 1 FROM employees WHERE username = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error checking username", e);
        }
    }

    /**
     * Maps a row → Employee object
     */
    private Employee mapEmployee(ResultSet rs) throws SQLException {
        return new Employee(
                rs.getString("employee_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("role")
        );
    }
}
