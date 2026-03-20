package service;

import model.Account;

/**
 * Handles account-related use cases.
 */
public class AccountService {

    public boolean login(String username, String password) {
        // TODO: validate credentials
        return false;
    }

    public void logout(Account account) {
        // TODO: perform logout logic
    }

    public boolean changePassword(Account account, String oldPassword, String newPassword) {
        if (account.getPassword().equals(oldPassword)) {
            account.setPassword(newPassword);
            return true;
        }
        return false;
    }

    public void deactivateAccount(Account account) {
        account.setActive(false);
    }

    public void updateEmail(Account account, String newEmail) {
        account.setEmail(newEmail);
    }
}