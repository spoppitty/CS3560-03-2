package model;

/**
 * Represents a manager account in the system.
 * Manager inherits account information from Account.
 */
public class Manager extends Account {
    private String managerID;

    public Manager(String accountName, String username, String password, String email, boolean active,
                   String managerID) {
        super(accountName, username, password, email, active);
        this.managerID = managerID;
    }

    public String getManagerID() {
        return managerID;
    }

    public void setManagerID(String managerID) {
        this.managerID = managerID;
    }
}