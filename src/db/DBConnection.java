package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {
        Connection con = null;
        try {
            // Driver auto-loads in JDBC 4+, no Class.forName required
            con = DriverManager.getConnection(
                "jdbc:oracle:thin:@//localhost:1521/XEPDB1",
                "medshop",
                "medshop123"
            );
            System.out.println("Database connected successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
}
