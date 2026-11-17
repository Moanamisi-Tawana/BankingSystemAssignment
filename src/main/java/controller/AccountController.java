package controller;

import model.Account;
import model.Bank;
import model.ChequeAccount;
import model.Customer;
import model.InterestBearing;
import model.Transaction;
import view.AccountView;

import java.util.ArrayList;
import java.util.List;

// AccountController - Handles banking transactions and updates the account view.
public class AccountController {

    private final Bank bank;
    private final Customer customer;
    private final AccountView view;
    private String lastSelected;

    // Constructor - Sets up the controller and connects the buttons to the code.
    public AccountController(Bank bank, Customer customer, AccountView view) {
        this.bank = bank;
        this.customer = customer;
        this.view = view;

        view.setWelcome("Logged in as: " + customer.getFullName());
        view.setOnAccountSelected(this::handleAccountSelected);
        view.setOnDeposit(this::handleDeposit);
        view.setOnWithdraw(this::handleWithdraw);
        view.setOnPayInterest(this::handlePayInterest);
        refreshAccountList();
    }

    public void refreshAccountList() {
        List<String> numbers = new ArrayList<>();
        for (Account a : bank.accountsOf(customer)) {
            numbers.add(a.getAccountNumber());
        }
        view.setAccounts(numbers);
        view.showHistory("");
        if (numbers.isEmpty()) {
            view.showAccountDetails("You have no accounts yet. Click 'Open new account'.");
        } else {
            view.showAccountDetails("Select an account from the list.");
        }
    }

    private void handleAccountSelected(String accountNumber) {
        if (accountNumber == null) return;
        Account a = bank.findAccount(accountNumber);
        if (a == null) {
            view.showError("Account not found.");
            return;
        }
        this.lastSelected = accountNumber;
        view.clearMessage();
        view.showAccountDetails(describe(a));
        view.showHistory(formatHistory(a));
    }

    private void handleDeposit(AccountView.DepositRequest req) {
        if (req.accountNumber == null) {
            view.showError("Select an account first.");
            return;
        }
        Double amount = parseAmount(req.amountText);
        if (amount == null) return;

        Account a = bank.findAccount(req.accountNumber);
        if (a == null) { view.showError("Account not found."); return; }

        String note = (req.note == null || req.note.isBlank()) ? "Deposit" : req.note.trim();
        try {
            bank.deposit(req.accountNumber, amount, note);
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
            return;
        }
        handleAccountSelected(req.accountNumber);
        view.showMessage("Deposited BWP " + String.format("%.2f", amount));
    }

    private void handleWithdraw(AccountView.WithdrawRequest req) {
        if (req.accountNumber == null) {
            view.showError("Select an account first.");
            return;
        }
        Double amount = parseAmount(req.amountText);
        if (amount == null) return;

        Account a = bank.findAccount(req.accountNumber);
        if (a == null) { view.showError("Account not found."); return; }

        try {
            bank.withdraw(req.accountNumber, amount);
        } catch (UnsupportedOperationException | IllegalArgumentException e) {
            view.showError(e.getMessage());
            return;
        }
        handleAccountSelected(req.accountNumber);
        view.showMessage("Withdrew BWP " + String.format("%.2f", amount));
    }

    private void handlePayInterest() {
        if (lastSelected == null) {
            view.showError("Select an account first.");
            return;
        }
        Account a = bank.findAccount(lastSelected);
        if (a == null) { view.showError("Account not found."); return; }

        if (!(a instanceof InterestBearing ib)) {
            view.showError(a.getAccountType() + " accounts do not earn interest.");
            return;
        }
        bank.payInterest(lastSelected);
        handleAccountSelected(lastSelected);
        view.showMessage("Interest applied at rate "
                + (ib.getInterestRate() * 100) + "%/month.");
    }

    private Double parseAmount(String raw) {
        if (raw == null || raw.isBlank()) {
            view.showError("Enter an amount.");
            return null;
        }
        try {
            double v = Double.parseDouble(raw.trim());
            if (v <= 0) {
                view.showError("Amount must be greater than zero.");
                return null;
            }
            return v;
        } catch (NumberFormatException e) {
            view.showError("Amount must be a number.");
            return null;
        }
    }

    private String describe(Account a) {
        StringBuilder sb = new StringBuilder();
        sb.append(a.getAccountType()).append(" ").append(a.getAccountNumber())
                .append("  |  Balance: BWP ").append(String.format("%.2f", a.getBalance()))
                .append("  |  Branch: ").append(a.getBranch());
        if (a instanceof InterestBearing ib) {
            sb.append("  |  Rate: ").append(ib.getInterestRate() * 100).append("% / month");
        }
        if (a instanceof ChequeAccount ca) {
            sb.append("  |  Employer: ").append(ca.getEmployerName());
        }
        return sb.toString();
    }

    private String formatHistory(Account a) {
        if (a.getTransactions().isEmpty()) return "(no transactions yet)";
        StringBuilder sb = new StringBuilder();
        for (Transaction t : a.getTransactions()) {
            sb.append(t).append("\n");
        }
        return sb.toString();
    }
}