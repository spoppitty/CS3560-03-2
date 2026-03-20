package model;

/**
 * Represents an employee account in the system.
 * Employee inherits account information from Account.
 */
public class Employee extends Account {
    private String employeeID;

    public Employee(String accountName, String username, String password, String email, boolean active,
                    String employeeID) {
        super(accountName, username, password, email, active);
        this.employeeID = employeeID;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }
}