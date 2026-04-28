package ui;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Employee;
import repository.DatabaseException;
import repository.EmployeeRepository;
import service.EmployeeService;
import service.SessionManager;

public class LoginDashboard {

    public static Scene create(Stage stage, InventorySubsystemApp app) {

        Label title = new Label("Login");
        title.getStyleClass().add("app-title");

        TextField username = new TextField();
        username.setPromptText("Username");

        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        Button login = new Button("Login");
        login.getStyleClass().add("primary-button");

        Button createAccount = new Button("Create Account");
        createAccount.getStyleClass().add("secondary-button");

        Label status = new Label();
        status.getStyleClass().add("panel-description");

        VBox layout = new VBox(12, title, username, password, login, createAccount, status);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.getStyleClass().addAll("app-root", "login-card");

        login.setOnAction(e -> {

            String user = username.getText().trim();
            String pass = password.getText();

            if (user.isBlank() || pass.isBlank()) {
                status.setText("Enter both username and password.");
                return;
            }

            try {
                EmployeeRepository repo = new EmployeeRepository();
                Employee employee = repo.authenticate(user, pass);

                if (employee != null) {
                    SessionManager.setCurrentUser(employee);
                    app.showInventory(employee);
                } else {
                    status.setText("Invalid login");
                }
            } catch (DatabaseException ex) {
                status.setText(ex.getMessage());
            }
        });

        createAccount.setOnAction(e -> {

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Create Account");
        
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
        
            TextField firstName = new TextField();
            TextField lastName = new TextField();
            TextField createAccountUserName = new TextField();
            PasswordField createAccountPassword = new PasswordField();
            PasswordField confirm = new PasswordField();
        
            grid.add(new Label("First Name"), 0, 0);
            grid.add(firstName, 1, 0);
            grid.add(new Label("Last Name"), 0, 1);
            grid.add(lastName, 1, 1);
            grid.add(new Label("Username"), 0, 2);
            grid.add(createAccountUserName, 1, 2);
            grid.add(new Label("Password"), 0, 3);
            grid.add(createAccountPassword, 1, 3);
            grid.add(new Label("Confirm"), 0, 4);
            grid.add(confirm, 1, 4);
        
            dialog.getDialogPane().setContent(grid);
        
            ButtonType createBtn = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(createBtn, ButtonType.CANCEL);

            dialog.showAndWait()
                    .filter(createBtn::equals)
                    .ifPresent(result -> {
                        try {
                            EmployeeService service = new EmployeeService();
                            String employeeId = service.generateEmployeeId();

                            Employee emp = service.registerEmployee(
                                    employeeId,
                                    firstName.getText().trim(),
                                    lastName.getText().trim(),
                                    createAccountUserName.getText().trim(),
                                    createAccountPassword.getText(),
                                    confirm.getText(),
                                    "EMP"
                            );

                            status.setText("Account created: " + emp.getUsername());
                            username.setText(emp.getUsername());
                            password.clear();
                        } catch (IllegalArgumentException | DatabaseException ex) {
                            status.setText(ex.getMessage());
                        }
                    });
        });

        Scene scene = new Scene(layout, 440, 360);
        java.net.URL stylesheet = LoginDashboard.class.getResource("/ui/inventory-dashboard.css");
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }
        return scene;
        }
    }
