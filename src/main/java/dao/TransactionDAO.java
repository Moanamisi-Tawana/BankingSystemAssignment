package dao;

import model.Transaction;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// TransactionDAO - Saves and loads banking transaction history to and from the database.
public class TransactionDAO {

    // Saves a new transaction (like a deposit or withdrawal) into the database.
    public int insert(Transaction t) throws SQLException {
        String sql = "INSERT INTO transactions(account_number,type,amount,note,timestamp) " +
                "VALUES (?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getAccountNumber());
            ps.setString(2, t.getType());
            ps.setDouble(3, t.getAmount());
            ps.setString(4, t.getNote());
            ps.setTimestamp(5, Timestamp.valueOf(t.getTimestamp()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    // Gets a list of all transactions belonging to one specific account number.
    public List<Transaction> findByAccountNumber(String accountNumber) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE account_number=? ORDER BY transaction_id";
        List<Transaction> out = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(toTransaction(rs));
            }
        }
        return out;
    }

    // Gets a list of every single transaction stored in the whole system.
    public List<Transaction> findAll() throws SQLException {
        List<Transaction> out = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery(
                     "SELECT * FROM transactions ORDER BY transaction_id")) {
            while (rs.next()) out.add(toTransaction(rs));
        }
        return out;
    }

    // Permanently removes a transaction record from the database.
    public void delete(int transactionId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM transactions WHERE transaction_id=?")) {
            ps.setInt(1, transactionId);
            ps.executeUpdate();
        }
    }

    // Helper method that converts database rows into Java transaction objects.
    private Transaction toTransaction(ResultSet rs) throws SQLException {
        Transaction t = new Transaction(
                rs.getInt("transaction_id"),
                rs.getString("account_number"),
                rs.getString("type"),
                rs.getDouble("amount"),
                rs.getString("note"));
        return t;
    }
}
