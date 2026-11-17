package dao;

import model.Customer;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// CustomerDAO - Saves and loads customer profiles to and from the database.
public class CustomerDAO {

    // Saves a new customer profile into the database.
    public void insert(Customer c) throws SQLException {
        String sql = "INSERT INTO customers VALUES (?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCustomerId());
            ps.setString(2, c.getFirstname());
            ps.setString(3, c.getSurname());
            ps.setString(4, c.getAddress());
            ps.setString(5, c.getUsername());
            ps.setString(6, c.getPassword());
            ps.executeUpdate();
        }
    }

    // Finds a customer profile using their unique customer ID number.
    public Customer findById(String id) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? toCustomer(rs) : null;
            }
        }
    }

    // Finds a customer profile using their login username.
    public Customer findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM customers WHERE username=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? toCustomer(rs) : null;
            }
        }
    }

    // Gets a list of every single customer stored in the whole system.
    public List<Customer> findAll() throws SQLException {
        List<Customer> out = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM customers ORDER BY customer_id")) {
            while (rs.next()) out.add(toCustomer(rs));
        }
        return out;
    }

    // Changes the details of an existing customer in the database.
    public void update(Customer c) throws SQLException {
        String sql = "UPDATE customers SET firstname=?,surname=?,address=?,username=?,password=? " +
                "WHERE customer_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getFirstname());
            ps.setString(2, c.getSurname());
            ps.setString(3, c.getAddress());
            ps.setString(4, c.getUsername());
            ps.setString(5, c.getPassword());
            ps.setString(6, c.getCustomerId());
            ps.executeUpdate();
        }
    }

    // Permanently removes a customer profile from the database.
    public void delete(String id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM customers WHERE customer_id=?")) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }

    // Helper method that converts database rows into Java customer objects.
    private Customer toCustomer(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getString("customer_id"),
                rs.getString("firstname"),
                rs.getString("surname"),
                rs.getString("address"),
                rs.getString("username"),
                rs.getString("password"));
    }
}
