package model;

// Inheritance - Inherits rules from the main Account system and earns monthly interest.
public class SavingsAccount extends Account implements InterestBearing {

    private static final double MONTHLY_RATE = 0.0005;   // 0.05% per month

    // Inheritance - Tells the main Account system to set up this account with a number, branch, and owner.
    public SavingsAccount(String accountNumber, String branch, Customer owner) {
        super(accountNumber, branch, owner);
    }

    // Polymorphism - Returns the name "Savings" so the app knows what type of account this is.
    @Override
    public String getAccountType() { return "Savings"; }

    // Polymorphism - Automatically blocks any attempt to withdraw money from this savings account.
    @Override
    public void withdraw(double amount) {
        throw new UnsupportedOperationException(
                "Savings accounts do not allow withdrawals.");
    }

    // Abstraction - Calculates the monthly interest payment based on the balance and adds it to the account.
    @Override
    public void payInterest() {
        double interest = getBalance() * MONTHLY_RATE;
        addToBalance(interest);
        recordTransaction("INTEREST", interest, "Monthly interest");
    }

    // Abstraction - Gives back the monthly interest rate percentage value.
    @Override
    public double getInterestRate() { return MONTHLY_RATE; }
}
