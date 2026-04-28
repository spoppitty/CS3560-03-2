package ui;

import javafx.application.Application;
import javafx.stage.Stage;
import model.Employee;
import service.SessionManager;

public class InventorySubsystemApp extends Application {

    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        stage.setTitle("Store System");

        showLogin();
        stage.show();
    }

    public void showLogin() {
        stage.setScene(LoginDashboard.create(stage, this));
    }

    public void showInventory(Employee employee) {
        stage.setScene(new InventoryDashboardApp().createScene(stage, this));
    }

    public void showSales() {
        Employee employee = SessionManager.getCurrentUser();
        if (employee != null) {
            stage.setTitle("Store System - Sales - " + employee.getFirstName());
        } else {
            stage.setTitle("Store System - Sales");
        }
        stage.setScene(new SalesDashboard().createScene(stage, this));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
