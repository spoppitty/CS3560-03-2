package ui;

/**
 * Separate launcher so IDEs and command-line runs have a simple main class.
 */
public final class InventoryDashboardLauncher {
    private InventoryDashboardLauncher() {
    }

    public static void main(String[] args) {
        InventoryDashboardApp.main(args);
    }
}
