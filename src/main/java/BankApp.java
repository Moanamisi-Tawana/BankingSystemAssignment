import controller.AccountController;
import controller.LoginController;
import controller.OpenAccountController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Bank;
import model.Customer;
import view.AccountView;
import view.LoginView;
import view.OpenAccountView;

// BankApp - Controls the main application window and handles switching between different screens.
public class BankApp extends Application {

    private Stage stage;
    private Bank bank;
    private Customer currentCustomer;

    // Initializes the banking system data and opens up the initial login window.
    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        try {
            this.bank = App.createBankWithPersistence();
        } catch (Exception e) {
            e.printStackTrace();
            this.bank = App.createSeededBank();
        }
        stage.setTitle("Banking System");
        showLogin();
        stage.show();
    }

    // A method that creates the login screen objects and sets up the successful login redirect.
    private void showLogin() {
        LoginView view = new LoginView();
        new LoginController(bank, view, customer -> {
            currentCustomer = customer;
            showAccounts();
        });
        stage.setScene(new Scene(view.build(), 420, 360));
    }

    // Displays the main dashboard showing the customer's active bank accounts.
    private void showAccounts() {
        AccountView view = new AccountView();
        new AccountController(bank, currentCustomer, view);
        // navigation only - stays in BankApp
        view.setOnOpenAccountRequested(this::showOpenAccount);
        view.setOnLogout(this::showLogin);
        stage.setScene(new Scene(view.build(), 640, 720));
    }

    // Opens up the screen that lets the logged-in customer open a new account.
    private void showOpenAccount() {
        OpenAccountView view = new OpenAccountView();
        new OpenAccountController(bank, currentCustomer, view, this::showAccounts);
        view.setOnBack(this::showAccounts);
        stage.setScene(new Scene(view.build(), 480, 640));
    }
}
