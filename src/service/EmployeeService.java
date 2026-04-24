package service;

import model.Employee;
import repository.EmployeeRepository;

/**
 * Handles employee-related use cases.
 */
public class EmployeeService {

    private final EmployeeRepository employeeRepository = new EmployeeRepository();

    public Employee createEmployeeAccount(String employeeID, String firstName,
                                          String lastName, String username,
                                          String password, String role) {
        return new Employee(employeeID, firstName, lastName, username, password, role);
    }

    public Employee registerEmployee(String employeeID, String firstName, String lastName,
                                     String username, String password, String confirmPassword,
                                     String role) {
        if (firstName.isBlank() || lastName.isBlank()) {
            throw new IllegalArgumentException("Name fields cannot be empty.");
        }

        if (username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }

        if (password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        if (employeeID == null || employeeID.isBlank()) {
            throw new IllegalArgumentException("Employee ID cannot be empty.");
        }

        if (employeeID.length() > 10) {
            throw new IllegalArgumentException("Employee ID must be 10 characters or fewer.");
        }

        if (employeeRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists.");
        }

        Employee employee = createEmployeeAccount(
                employeeID,
                firstName,
                lastName,
                username,
                password,
                role
        );

        employeeRepository.createEmployee(employee);
        return employee;
    }

    public String generateEmployeeId() {
        int suffix = (int) (System.currentTimeMillis() % 1_000_000);
        return String.format("EMP-%04d", suffix % 10_000);
    }
}
