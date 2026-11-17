package controller;

import model.Bank;
import model.Customer;
import view.OpenAccountView;

// OpenAccountController - Opens the new account screen.
public class OpenAccountController {

    private final Bank bank;
    private final Customer customer;
    private final OpenAccountView view;
    private final Runnable onSuccess;

    public OpenAccountController(Bank bank, Customer customer,
                                 OpenAccountView view, Runnable onSuccess) {
        this.bank = bank;
        this.customer = customer;
        this.view = view;
        this.onSuccess = onSuccess;
        view.setOnOpenAccount(this::handleOpen);
    }

    // Creates the new account after checking that the form is filled out correctly.
    private void handleOpen(OpenAccountView.Request req) {
        if (req.type == null || req.type.isBlank()) {
            view.showMessage("Choose an account type."); return;
        }
        if (req.branch == null || req.branch.isBlank()) {
            view.showMessage("Enter a branch."); return;
        }

        try {
            switch (req.type) {
                case "Savings" -> bank.openSavings(customer, req.branch.trim());
                case "Investment" -> {
                    double initial = parseAmount(req.initialDepositText, "initial deposit");
                    bank.openInvestment(customer, req.branch.trim(), initial);
                }
                case "Cheque" -> bank.openCheque(customer, req.branch.trim(),
                        req.employerName, req.employerAddress);
                default -> { view.showMessage("Unknown account type."); return; }
            }
            onSuccess.run();
        } catch (IllegalArgumentException e) {
            view.showMessage(e.getMessage());
        }
    }

    // Converts the text typed into a usable number.
    public double parseAmount(String text, String label) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Enter an " + label + ".");
        }
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The " + label + " must be a number.");
        }
    }
}