package Bank;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL =
            "jdbc:mariadb://localhost:3306/blood_bank?useSSL=false";

    private static final String USER = "root";     // change if needed
    private static final String PASS = "password";         // add password if set

    public static Connection connect() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            Connection con = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("✅ Database connected successfully");
            return con;
        } catch (Exception e) {
            System.out.println("❌ Database Connection Error:");
            e.printStackTrace();
            return null;
        }
    }
}
