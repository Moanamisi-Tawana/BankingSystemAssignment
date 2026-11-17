import model.*;

public class App {

    public static void main(String[] args) {

        Bank bank = new Bank("First National Bank of Bots");

        // 1. Register customers (now with login credentials)
        Customer alice = new Customer("C001", "Alice", "Molefe",
                "Plot 123, Gaborone", "alice", "Alice123");
        Customer bob   = new Customer("C002", "Bob", "Kgosana",
                "Plot 456, Francistown", "bob", "Bob123");
        bank.registerCustomer(alice);
        bank.registerCustomer(bob);

        // 2. Alice opens one of each account type through the bank
        SavingsAccount    sa = bank.openSavings(alice, "Main Mall");
        InvestmentAccount ia = bank.openInvestment(alice, "Main Mall", 1000.00);
        ChequeAccount     ca = bank.openCheque(alice, "Main Mall",
                "Botswana Power Corp", "Plot 1, Gaborone");

        // 3. Deposits / withdrawals
        bank.deposit(sa.getAccountNumber(), 500.00, "Monthly savings");
        bank.deposit(ca.getAccountNumber(), 2000.00, "Salary");
        bank.withdraw(ca.getAccountNumber(), 500.00);
        bank.withdraw(ia.getAccountNumber(), 200.00);

        // 4. Interest for every account that implements InterestBearing
        bank.payInterestAll();

        // 5. Print every account plus its transaction history
        System.out.println("=== Accounts and histories ===");
        for (Account a : bank.getAllAccounts()) {
            System.out.println(a);
            for (Transaction t : a.getTransactions()) {
                System.out.println("    " + t);
            }
        }

        // 6. Login check
        Customer found = bank.findByUsername("alice");
        System.out.println("\nLogin 'alice' -> "
                + (found != null ? found.getFullName() : "NOT FOUND"));
        System.out.println("Bob's accounts: " + bank.accountsOf(bob).size());
    }

    /** Builds a Bank with two sample customers and some accounts. */
    public static Bank createSeededBank() {
        Bank bank = new Bank("First National Bank of Bots");

        Customer alice = new Customer("C001", "Alice", "Molefe",
                "Plot 123, Gaborone", "alice", "Alice123");
        Customer bob = new Customer("C002", "Bob", "Kgosana",
                "Plot 456, Francistown", "bob", "Bob123");
        bank.registerCustomer(alice);
        bank.registerCustomer(bob);

        SavingsAccount    sa = bank.openSavings(alice, "Main Mall");
        InvestmentAccount ia = bank.openInvestment(alice, "Main Mall", 1000.00);
        bank.openCheque(alice, "Main Mall",
                "Botswana Power Corp", "Plot 1, Gaborone");
        bank.deposit(sa.getAccountNumber(), 500.00, "Monthly savings");
        bank.deposit(ia.getAccountNumber(), 500.00, "Extra top-up");

        return bank;
    }

    public static Bank createBankWithPersistence() throws Exception {
        util.DBSchema.initialize();
        Bank bank = new Bank("First National Bank Botswana");
        bank.attachPersistence(
                new dao.CustomerDAO(),
                new dao.AccountDAO(),
                new dao.TransactionDAO());
        bank.loadFromDatabase();
        return bank;
    }
}