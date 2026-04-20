package service;

import model.Account;

/**
 * Handles account-related use cases.
 */
public class AccountService {

    /**
     * Checks whether the provided username and password are valid.
     *
     * @param username username typed by the user
     * @param password password typed by the user
     * @return true when credentials are accepted
     */
    public boolean login(String username, String password) {
        // TODO: validate credentials
        return false;
    }

    /**
     * Logs an account out of the system.
     *
     * @param account account that is logging out
     */
    public void logout(Account account) {
        // TODO: perform logout logic
    }

    /**
     * Changes an account password when the old password matches.
     *
     * @param account account to update
     * @param oldPassword current password for verification
     * @param newPassword replacement password
     * @return true when the password was changed
     */
    public boolean changePassword(Account account, String oldPassword, String newPassword) {
        if (account.getPassword().equals(oldPassword)) {
            account.setPassword(newPassword);
            return true;
        }
        return false;
    }

    /**
     * Marks an account inactive so it can no longer be used.
     *
     * @param account account to deactivate
     */
    public void deactivateAccount(Account account) {
        account.setActive(false);
    }

    /**
     * Updates the email address stored on an account.
     *
     * @param account account to update
     * @param newEmail replacement email address
     */
    public void updateEmail(Account account, String newEmail) {
        account.setEmail(newEmail);
    }
}
