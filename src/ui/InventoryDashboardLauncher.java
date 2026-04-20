package ui;

/**
 * Separate launcher so IDEs and command-line runs have a simple main class.
 */
public final class InventoryDashboardLauncher {
    /**
     * Prevents creating launcher objects; this class only forwards to main.
     */
    private InventoryDashboardLauncher() {
    }

    /**
     * Command-line entry point used by Maven and IDE run configurations.
     *
     * @param args command-line arguments passed by Java
     */
    public static void main(String[] args) {
        InventoryDashboardApp.main(args);
    }
}
