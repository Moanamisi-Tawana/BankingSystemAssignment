package model;

// Inheritance - A specific type of bank account that builds on the main Account system and earns monthly interest.
public class InvestmentAccount extends Account implements InterestBearing {

    public static final double MIN_OPENING_DEPOSIT = 500.00;
    private static final double MONTHLY_RATE = 0.05;      // 5% per month

    // Inheritance - Sets up the investment account with a number, branch, owner, and initial deposit.
    public InvestmentAccount(String accountNumber, String branch,
                             Customer owner, double initialDeposit) {
        super(accountNumber, branch, owner);
        if (initialDeposit < MIN_OPENING_DEPOSIT) {
            throw new IllegalArgumentException(
                    "Investment account needs at least BWP " + MIN_OPENING_DEPOSIT);
        }
        deposit(initialDeposit, "Opening deposit");
    }

    // Polymorphism - Returns the name "Investment" so the app knows what type of account this is.
    @Override
    public String getAccountType() { return "Investment"; }

    // Polymorphism - Allows money to be withdrawn from the investment account.
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
