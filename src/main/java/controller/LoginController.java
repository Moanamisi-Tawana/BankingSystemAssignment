package controller;

import model.Bank;
import model.Customer;
import view.LoginView;

import java.util.function.Consumer;

// LoginController - Handles the login screen and checks the user's credentials.
public class LoginController {

    private final Bank bank;
    private final LoginView view;
    private final Consumer<Customer> onSuccess;

    // Constructor - Sets up the login screen and connects the login button to the code.
    public LoginController(Bank bank, LoginView view, Consumer<Customer> onSuccess) {
        this.bank = bank;
        this.view = view;
        this.onSuccess = onSuccess;
        view.setOnLogin(this::handleLogin);
    }

    private void handleLogin(String username, String password) {
        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            view.showMessage("Enter both username and password.");
            return;
        }
        Customer found = bank.findByUsername(username.trim());
        // Same message whether the user does not exist OR the password is wrong.
        if (found == null || !found.getPassword().equals(password)) {
            view.showMessage("Invalid username or password.");
            return;
        }
        view.showMessage("");
        onSuccess.accept(found);
    }
}