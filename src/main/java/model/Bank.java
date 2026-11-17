package model;

import dao.AccountDAO;
import dao.CustomerDAO;
import dao.TransactionDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Bank {

    private final String name;
    private final List<Customer> customers = new ArrayList<>();
    private final List<Account> accounts = new ArrayList<>();
    private int nextAccountSeq = 1000;
    private CustomerDAO customerDAO;
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;

    public Bank(String name) { this.name = name; }

    public String getName() { return name; }

    public void attachPersistence(CustomerDAO cd, AccountDAO ad, TransactionDAO td) {
        this.customerDAO = cd;
        this.accountDAO = ad;
        this.transactionDAO = td;
    }

    public boolean isPersistent() { return customerDAO != null; }

    public void loadFromDatabase() throws SQLException {
        if (!isPersistent()) return;
        customers.clear();
        accounts.clear();
        for (Customer c : customerDAO.findAll()) customers.add(c);
        for (Account a : accountDAO.findAll()) {
            List<Transaction> txs = transactionDAO.findByAccountNumber(a.getAccountNumber());
            a.hydrateTransactions(txs);
            accounts.add(a);
        }
        recalcAccountSeq();
    }

    private void recalcAccountSeq() {
        int max = 1000;
        for (Account a : accounts) {
            String num = a.getAccountNumber();
            int dash = num.lastIndexOf('-');
            if (dash >= 0) {
                try { max = Math.max(max, Integer.parseInt(num.substring(dash + 1))); }
                catch (NumberFormatException ignored) { }
            }
        }
        this.nextAccountSeq = max;
    }

    // ---------- customers ----------
    public Customer registerCustomer(Customer c) {
        customers.add(c);
        if (isPersistent()) {
            try { customerDAO.insert(c); }
            catch (SQLException e) { throw new RuntimeException("DB insert customer", e); }
        }
        return c;
    }

    public Customer findByUsername(String username) {
        for (Customer c : customers) {
            if (c.getUsername().equals(username)) return c;
        }
        return null;
    }

    // ---------- account opening ----------
    public SavingsAccount openSavings(Customer owner, String branch) {
        SavingsAccount a = new SavingsAccount(nextNumber("SA"), branch, owner);
        addAccount(a);
        return a;
    }

    public InvestmentAccount openInvestment(Customer owner, String branch, double initialDeposit) {
        InvestmentAccount a = new InvestmentAccount(nextNumber("IA"), branch, owner, initialDeposit);
        addAccount(a);
        return a;
    }

    public ChequeAccount openCheque(Customer owner, String branch,
                                    String empName, String empAddr) {
        ChequeAccount a = new ChequeAccount(nextNumber("CA"), branch, owner, empName, empAddr);
        addAccount(a);
        return a;
    }

    private void addAccount(Account a) {
        accounts.add(a);
        if (isPersistent()) {
            try { accountDAO.insert(a); }
            catch (SQLException e) { throw new RuntimeException("DB insert account", e); }
        }
    }

    private String nextNumber(String prefix) {
        return prefix + "-" + (++nextAccountSeq);
    }

    // ---------- lookup ----------
    public Account findAccount(String accountNumber) {
        for (Account a : accounts) {
            if (a.getAccountNumber().equals(accountNumber)) return a;
        }
        return null;
    }

    public List<Account> accountsOf(Customer owner) {
        List<Account> result = new ArrayList<>();
        for (Account a : accounts) {
            if (a.getOwner().getCustomerId().equals(owner.getCustomerId())) {
                result.add(a);
            }
        }
        return result;
    }

    public List<Account> getAllAccounts() { return new ArrayList<>(accounts); }

    // ---------- money movement ----------
    public void deposit(String accountNumber, double amount, String note) {
        Account a = requireAccount(accountNumber);
        a.deposit(amount, note);
        persistLastChange(a);
    }

    public void withdraw(String accountNumber, double amount) {
        Account a = requireAccount(accountNumber);
        a.withdraw(amount);
        persistLastChange(a);
    }

    /** Pay interest on ONE account and save it (used by the GUI). */
    public void payInterest(String accountNumber) {
        Account a = requireAccount(accountNumber);
        if (a instanceof InterestBearing ib) {
            ib.payInterest();
            persistLastChange(a);
        }
    }

    public void payInterestAll() {
        for (Account a : accounts) {
            if (a instanceof InterestBearing ib) {
                ib.payInterest();
                persistLastChange(a);
            }
        }
    }

    /** Save the balance and the newest transaction after any change. */
    private void persistLastChange(Account a) {
        if (!isPersistent()) return;
        try {
            accountDAO.updateBalance(a.getAccountNumber(), a.getBalance());
            List<Transaction> txs = a.getTransactions();
            if (!txs.isEmpty()) {
                transactionDAO.insert(txs.get(txs.size() - 1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB persist change", e);
        }
    }

    private Account requireAccount(String accountNumber) {
        Account a = findAccount(accountNumber);
        if (a == null) {
            throw new IllegalArgumentException("No account: " + accountNumber);
        }
        return a;
    }
}