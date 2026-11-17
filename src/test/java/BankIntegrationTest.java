import dao.AccountDAO;
import dao.CustomerDAO;
import dao.TransactionDAO;
import model.Bank;
import model.Customer;
import model.SavingsAccount;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end integration test:
 *   Bank -> model -> DAO -> H2 database
 *   and back: DAO -> H2 -> Bank.
 */
public class BankIntegrationTest {

    static Bank bank;

    @BeforeAll
    static void setup() throws Exception {
        util.DBSchema.initialize();                     // tables + 10 sample rows (once)
        bank = new Bank("Test Bank");
        bank.attachPersistence(
                new CustomerDAO(), new AccountDAO(), new TransactionDAO());
        bank.loadFromDatabase();
    }

    @Test
    void loginFindsCustomerFromDatabase() {
        Customer c = bank.findByUsername("thabo");
        assertNotNull(c, "thabo should exist in the seeded DB");
        assertEquals("pw1", c.getPassword());
    }

    @Test
    void openAccountDepositAndVerifyBalanceInDatabase() throws Exception {
        Customer c = bank.findByUsername("naledi");
        assertNotNull(c);

        SavingsAccount sa = bank.openSavings(c, "Lobatse");
        String accNo = sa.getAccountNumber();

        bank.deposit(accNo, 250.00, "Test integration deposit");

        // 1. In-memory balance
        assertEquals(250.00, bank.findAccount(accNo).getBalance(), 0.01);

        // 2. Balance persisted to the DB
        try (Connection db = util.DBConnection.getConnection();
             PreparedStatement ps = db.prepareStatement(
                     "SELECT balance FROM accounts WHERE account_number=?")) {
            ps.setString(1, accNo);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next(), "account row should exist");
                assertEquals(250.00, rs.getDouble("balance"), 0.01);
            }
        }

        // 3. Transaction row for the deposit exists
        try (Connection db = util.DBConnection.getConnection();
             PreparedStatement ps = db.prepareStatement(
                     "SELECT COUNT(*) FROM transactions WHERE account_number=?")) {
            ps.setString(1, accNo);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                assertEquals(1, rs.getInt(1));
            }
        }

        // 4. Clean up so re-running the test doesn't accumulate rows
        try (Connection db = util.DBConnection.getConnection()) {
            try (PreparedStatement ps = db.prepareStatement(
                    "DELETE FROM transactions WHERE account_number=?")) {
                ps.setString(1, accNo); ps.executeUpdate();
            }
            try (PreparedStatement ps = db.prepareStatement(
                    "DELETE FROM accounts WHERE account_number=?")) {
                ps.setString(1, accNo); ps.executeUpdate();
            }
        }
    }
}