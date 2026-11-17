package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Creates the tables (if they don't exist) and seeds 10 sample rows the first time.
 * Rerunning is safe: seeding only happens when the customers' table is empty.
 */
public class DBSchema {

    public static void initialize() throws SQLException {
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement()) {

            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS customers (
                  customer_id  VARCHAR(20)  PRIMARY KEY,
                  firstname    VARCHAR(50)  NOT NULL,
                  surname      VARCHAR(50)  NOT NULL,
                  address      VARCHAR(200),
                  username     VARCHAR(50)  UNIQUE NOT NULL,
                  password     VARCHAR(100) NOT NULL
                )""");

            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS accounts (
                  account_number    VARCHAR(20) PRIMARY KEY,
                  balance           DECIMAL(15,2) NOT NULL,
                  branch            VARCHAR(50) NOT NULL,
                  customer_id       VARCHAR(20) NOT NULL,
                  account_type      VARCHAR(20) NOT NULL,
                  employer_name     VARCHAR(100),
                  employer_address  VARCHAR(200),
                  FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
                )""");

            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS transactions (
                  transaction_id   INT AUTO_INCREMENT PRIMARY KEY,
                  account_number   VARCHAR(20) NOT NULL,
                  type             VARCHAR(20) NOT NULL,
                  amount           DECIMAL(15,2) NOT NULL,
                  note             VARCHAR(200),
                  timestamp        TIMESTAMP NOT NULL,
                  FOREIGN KEY (account_number) REFERENCES accounts(account_number)
                )""");
        }

        if (isEmpty()) {
            seed();
        }
    }

    private static boolean isEmpty() throws SQLException {
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM " + "customers")) {
            rs.next();
            return rs.getInt(1) == 0;
        }
    }

    /** 5 customers, 10 accounts (2 each), 10 transactions (1 per account). */
    private static void seed() throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);

            // --- 5 customers ---
            String[][] people = {
                    {"C001", "Thabo",  "Nkosi",   "Plot 11, Gaborone",     "thabo",  "Thabo123!"},
                    {"C002", "Naledi", "Mokoena", "Plot 22, Francistown",  "naledi", "Naledi123!"},
                    {"C003", "Kagiso", "Pule",    "Plot 33, Maun",         "kagiso", "Kagiso123!"},
                    {"C004", "Zanele", "Dube",    "Plot 44, Lobatse",      "zanele", "Zanele123!"},
                    {"C005", "Tumelo", "Molefe",  "Plot 55, Serowe",       "tumelo", "Tumelo123!"},
            };
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO customers VALUES (?,?,?,?,?,?)")) {
                for (String[] p : people) {
                    ps.setString(1, p[0]);
                    ps.setString(2, p[1]);
                    ps.setString(3, p[2]);
                    ps.setString(4, p[3]);
                    ps.setString(5, p[4]);
                    ps.setString(6, p[5]);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // --- 10 accounts (2 per customer) ---
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO accounts(account_number,balance,branch,customer_id,account_type," +
                            "employer_name,employer_address) VALUES (?,?,?,?,?,?,?)")) {

                int seq = 1000;
                for (String[] p : people) {
                    // savings
                    ps.setString(1, "SA-" + (++seq));
                    ps.setDouble(2, 500.00);
                    ps.setString(3, "Main");
                    ps.setString(4, p[0]);
                    ps.setString(5, "SAVINGS");
                    ps.setNull(6, java.sql.Types.VARCHAR);
                    ps.setNull(7, java.sql.Types.VARCHAR);
                    ps.addBatch();

                    // investment
                    ps.setString(1, "IA-" + (++seq));
                    ps.setDouble(2, 1000.00);
                    ps.setString(3, "Main");
                    ps.setString(4, p[0]);
                    ps.setString(5, "INVESTMENT");
                    ps.setNull(6, java.sql.Types.VARCHAR);
                    ps.setNull(7, java.sql.Types.VARCHAR);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // --- 10 transactions (opening deposit on every account) ---
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO transactions(account_number,type,amount,note,timestamp) " +
                            "VALUES (?,?,?,?,?)");
                 Statement s = c.createStatement();
                 ResultSet rs = s.executeQuery(
                         "SELECT account_number, balance FROM accounts")) {

                java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
                while (rs.next()) {
                    ps.setString(1, rs.getString("account_number"));
                    ps.setString(2, "DEPOSIT");
                    ps.setDouble(3, rs.getDouble("balance"));
                    ps.setString(4, "Opening deposit");
                    ps.setTimestamp(5, now);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            c.commit();
        }
    }

}