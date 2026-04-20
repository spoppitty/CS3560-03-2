package model;

/**
 * Represents a general system account.
 * This is the parent class for Employee and Manager.
 */
public class Account {
    /**
     * Display name for the account owner.
     */
    private String accountName;

    /**
     * Username used to log in.
     */
    private String username;

    /**
     * Password used for login checks.
     */
    private String password;

    /**
     * Email address connected to the account.
     */
    private String email;

    /**
     * Whether this account is currently allowed to be used.
     */
    private boolean active;

    /**
     * Creates an account with login and contact information.
     */
    public Account(String accountName, String username, String password, String email, boolean active) {
        this.accountName = accountName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.active = active;
    }

    /**
     * Returns the account display name.
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * Returns the login username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the account password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the account email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns whether the account is active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Updates the account display name.
     */
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    /**
     * Updates the login username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Updates the account password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Updates the account email address.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Updates whether the account is active.
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}
