package model;

/**
 * Represents an employee account in the system.
 * Employee inherits account information from Account.
 */
    public class Employee { //extends Account 
    /**
     * Identifier that distinguishes one employee from another.
     */
    private String employeeId;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String role;

    public Employee(String employeeId, String firstName, String lastName,
                    String username, String password, String role) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getEmployeeID() { return employeeId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    public void setEmployeeId(String employeeId) { 
        this.employeeId = employeeId; 
    }
}
