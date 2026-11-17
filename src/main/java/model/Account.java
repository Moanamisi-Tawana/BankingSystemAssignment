package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Account - An abstract class that holds shared banking data for specific account types.
public abstract class Account {

    // ENCAPSULATION (Using 'private' variables to hide the internal data from outside files).
    private String accountNumber;
    private double balance;
    private String branch;
    private Customer owner;
    private final List<Transaction> transactions = new ArrayList<>();

    public Account(String accountNumber, String branch, Customer owner) {
        if (owner == null) {
            throw new IllegalArgumentException("An account must belong to a customer.");
        }
        this.accountNumber = accountNumber;
        this.branch = branch;
        this.owner = owner;
        this.balance = 0.0;
    }

    // ENCAPSULATION (Using public getter methods to safely read the private variables).
    public String getAccountNumber() { return accountNumber; }
    public double getBalance()       { return balance; }
    public String getBranch()        { return branch; }
    public Customer getOwner()       { return owner; }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public abstract String getAccountType();
    public abstract void withdraw(double amount);

    // METHOD OVERLOADING (Same method name 'deposit' but takes a different number of parameters).
    public void deposit(double amount) {
        deposit(amount, "Deposit");
    }

    public void deposit(double amount, String note) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit must be greater than zero.");
        }
        balance += amount;
        recordTransaction("DEPOSIT", amount, note);
    }

    protected void addToBalance(double amount) {
        if (amount > 0) balance += amount;
    }

    protected void subtractBalance(double amount) {
        if (amount > 0) balance -= amount;
    }

    /** For the DAO layer only - replaces the balance exactly as stored. */
    public void restoreBalance(double newBalance) {
        this.balance = newBalance;
    }

    /** For the DAO layer only - replaces the whole transaction history. */
    public void hydrateTransactions(List<Transaction> txs) {
        this.transactions.clear();
        if (txs != null) this.transactions.addAll(txs);
    }

    /** Called from deposit / withdraw / payInterest so every move is logged. */
    protected void recordTransaction(String type, double amount, String note) {
        transactions.add(new Transaction(
                transactions.size() + 1, accountNumber, type, amount, note));
    }

    @Override
    public String toString() {
        return getAccountType() + " [" + accountNumber + "] "
                + "owner=" + owner.getFullName()
                + " balance=" + balance + " branch=" + branch;
    }
}
