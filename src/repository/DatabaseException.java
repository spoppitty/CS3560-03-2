package repository;

/**
 * Runtime wrapper for database failures that should be shown to the user.
 */
public class DatabaseException extends RuntimeException {
    /**
     * Stores a user-friendly message while preserving the original SQL exception.
     *
     * @param message message shown by the UI
     * @param cause original database exception for debugging
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
