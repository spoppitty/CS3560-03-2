package model;

/**
 * Represents a manager account in the system.
 * Manager inherits account information from Account.
 */
public class Manager extends Account {
    /**
     * Identifier that distinguishes one manager from another.
     */
    private String managerID;

    /**
     * Creates a manager account with account fields plus a manager ID.
     */
    public Manager(String accountName, String username, String password, String email, boolean active,
                   String managerID) {
        super(accountName, username, password, email, active);
        this.managerID = managerID;
    }

    /**
     * Returns the manager ID.
     */
    public String getManagerID() {
        return managerID;
    }

    /**
     * Updates the manager ID.
     */
    public void setManagerID(String managerID) {
        this.managerID = managerID;
    }
}
