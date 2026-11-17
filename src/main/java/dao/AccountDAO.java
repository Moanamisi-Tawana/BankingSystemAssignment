package dao;

import model.*;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// AccountDAO - Saves and loads bank account data to and from the database.
public class AccountDAO {

    // Saves a new bank account into the database.
    public void insert(Account a) throws SQLException {
        String sql = "INSERT INTO accounts(account_number,balance,branch,customer_id," +
                "account_type,employer_name,employer_address) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getAccountNumber());
            ps.setDouble(2, a.getBalance());
            ps.setString(3, a.getBranch());
            ps.setString(4, a.getOwner().getCustomerId());
            ps.setString(5, a.getAccountType().toUpperCase());
            if (a instanceof ChequeAccount ca) {
                ps.setString(6, ca.getEmployerName());
                ps.setString(7, ca.getEmployerAddress());
            } else {
                ps.setNull(6, Types.VARCHAR);
                ps.setNull(7, Types.VARCHAR);
            }
            ps.executeUpdate();
        }
    }

    // Finds a single bank account using its account number.
    public Account findByNumber(String number) throws SQLException {
        String sql = "SELECT a.*, c.firstname, c.surname, c.address, c.username, c.password " +
                "FROM accounts a JOIN customers c ON a.customer_id = c.customer_id " +
                "WHERE a.account_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, number);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? toAccount(rs) : null;
            }
        }
    }

    // Gets a list of all bank accounts belonging to one specific customer ID.
    public List<Account> findByCustomerId(String customerId) throws SQLException {
        String sql = "SELECT a.*, c.firstname, c.surname, c.address, c.username, c.password " +
                "FROM accounts a JOIN customers c ON a.customer_id = c.customer_id " +
                "WHERE a.customer_id = ? ORDER BY a.account_number";
        List<Account> out = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(toAccount(rs));
            }
        }
        return out;
    }

    // Gets a list of every single bank account stored in the whole system.
    public List<Account> findAll() throws SQLException {
        String sql = "SELECT a.*, c.firstname, c.surname, c.address, c.username, c.password " +
                "FROM accounts a JOIN customers c ON a.customer_id = c.customer_id " +
                "ORDER BY a.account_number";
        List<Account> out = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) out.add(toAccount(rs));
        }
        return out;
    }

    // Changes the money balance of an account in the database after a transaction.
    public void updateBalance(String accountNumber, double newBalance) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE accounts SET balance=? WHERE account_number=?")) {
            ps.setDouble(1, newBalance);
            ps.setString(2, accountNumber);
            ps.executeUpdate();
        }
    }

    // Permanently removes a bank account from the database.
    public void delete(String accountNumber) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM accounts WHERE account_number=?")) {
            ps.setString(1, accountNumber);
            ps.executeUpdate();
        }
    }

    // Helper method that converts database rows into Java account objects.
    private Account toAccount(ResultSet rs) throws SQLException {
        String id = rs.getString("customer_id");
        Customer owner = new Customer(id,
                rs.getString("firstname"),
                rs.getString("surname"),
                rs.getString("address"),
                rs.getString("username"),
                rs.getString("password"));

        String number = rs.getString("account_number");
        String branch = rs.getString("branch");
        String type = rs.getString("account_type");
        double balance = rs.getDouble("balance");

        Account a;
        switch (type) {
            case "SAVINGS" -> a = new SavingsAccount(number, branch, owner);
            case "INVESTMENT" -> a = new InvestmentAccount(number, branch, owner, InvestmentAccount.MIN_OPENING_DEPOSIT);
            case "CHEQUE" -> a = new ChequeAccount(number, branch, owner,
                    rs.getString("employer_name"),
                    rs.getString("employer_address"));
            default -> throw new SQLException("Unknown account type: " + type);
        }

        a.restoreBalance(balance);
        return a;
    }
}
