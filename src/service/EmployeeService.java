package service;

import model.Employee;

/**
 * Handles employee-related use cases.
 */
public class EmployeeService {

    /**
     * Creates a new active employee account.
     *
     * @param employeeID employee-specific identifier
     * @param accountName display name for the account
     * @param username login username
     * @param password login password
     * @param email employee email address
     * @return new employee object
     */
    public Employee createEmployeeAccount(String employeeID, String accountName,
                                          String username, String password, String email) {
        return new Employee(accountName, username, password, email, true, employeeID);
    }

    /**
     * Updates the employee's email address.
     *
     * @param employee employee to update
     * @param newEmail replacement email address
     */
    public void updateEmployeeInformation(Employee employee, String newEmail) {
        employee.setEmail(newEmail);
    }

    /**
     * Deactivates an employee account.
     *
     * @param employee employee account to deactivate
     */
    public void deactivateEmployeeAccount(Employee employee) {
        employee.setActive(false);
    }
}
