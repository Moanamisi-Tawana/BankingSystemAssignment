package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // File-based database: creates bankingdb.mv.db in the project root.
    // DB_CLOSE_DELAY=-1 keeps it open while at least one connection is alive.
    private static final String URL =
            "jdbc:h2:./bankingdb;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}