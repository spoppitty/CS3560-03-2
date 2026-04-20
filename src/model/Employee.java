package model;

/**
 * Represents an employee account in the system.
 * Employee inherits account information from Account.
 */
public class Employee extends Account {
    /**
     * Identifier that distinguishes one employee from another.
     */
    private String employeeID;

    /**
     * Creates an employee account with account fields plus an employee ID.
     */
    public Employee(String accountName, String username, String password, String email, boolean active,
                    String employeeID) {
        super(accountName, username, password, email, active);
        this.employeeID = employeeID;
    }

    /**
     * Returns the employee ID.
     */
    public String getEmployeeID() {
        return employeeID;
    }

    /**
     * Updates the employee ID.
     */
    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }
}
