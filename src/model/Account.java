package model;

/**
 * Represents a general system account.
 * This is the parent class for Employee and Manager.
 */
public class Account {
    private String accountName;
    private String username;
    private String password;
    private String email;
    private boolean active;

    public Account(String accountName, String username, String password, String email, boolean active) {
        this.accountName = accountName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.active = active;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActive() {
        return active;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}