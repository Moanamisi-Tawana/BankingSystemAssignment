package model;

public class ChequeAccount extends Account {

    private String employerName;
    private String employerAddress;

    public ChequeAccount(String accountNumber, String branch, Customer owner,
                         String employerName, String employerAddress) {
        super(accountNumber, branch, owner);
        if (employerName == null || employerName.isBlank()) {
            throw new IllegalArgumentException("Cheque account needs an employer name.");
        }
        if (employerAddress == null || employerAddress.isBlank()) {
            throw new IllegalArgumentException("Cheque account needs an employer address.");
        }
        this.employerName = employerName;
        this.employerAddress = employerAddress;
    }

    public String getEmployerName()    { return employerName; }
    public String getEmployerAddress() { return employerAddress; }

    @Override
    public String getAccountType() { return "Cheque"; }

    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal must be greater than zero.");
        }
        if (amount > getBalance()) {
            throw new IllegalArgumentException("Insufficient funds.");
        }
        subtractBalance(amount);
        recordTransaction("WITHDRAW", amount, "Withdrawal");
    }
}