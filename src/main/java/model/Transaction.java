package model;

import java.time.LocalDateTime;

// Transaction - bundles information about a single banking transaction together.
public class Transaction {

    private int transactionId;
    private String accountNumber;
    private String type;
    private double amount;
    private String note;
    private LocalDateTime timestamp;

    // Encapsulation - Sets up a new transaction object with an automatic date and time stamp.
    public Transaction(int transactionId, String accountNumber,
                       String type, double amount, String note) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.note = note;
        this.timestamp = LocalDateTime.now();
    }

    // Encapsulation - Using public getter methods to safely read the private transaction variables.
    public int getTransactionId()    { return transactionId; }
    public String getAccountNumber() { return accountNumber; }
    public String getType()          { return type; }
    public double getAmount()        { return amount; }
    public String getNote()          { return note; }
    public LocalDateTime getTimestamp() { return timestamp; }

    // Polymorphism - Replaces Java's standard string method to print the transaction neatly in BWP.
    @Override
    public String toString() {
        return String.format("%s | %s | BWP %.2f | %s | %s",
                timestamp, type, amount, accountNumber, note);
    }
}
