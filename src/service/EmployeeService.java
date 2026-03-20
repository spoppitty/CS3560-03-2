package service;

import model.Employee;

/**
 * Handles employee-related use cases.
 */
public class EmployeeService {

    public Employee createEmployeeAccount(String employeeID, String accountName,
                                          String username, String password, String email) {
        return new Employee(accountName, username, password, email, true, employeeID);
    }

    public void updateEmployeeInformation(Employee employee, String newEmail) {
        employee.setEmail(newEmail);
    }

    public void deactivateEmployeeAccount(Employee employee) {
        employee.setActive(false);
    }
}