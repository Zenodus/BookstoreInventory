import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Class to manage MySQL database connection
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/book_inventory_management";
    private static final String USER = "root";
    private static final String PASSWORD = "UC4+z7pRja";  

    // Method to establish connection with MySQL
    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to MySQL database successfully!");
            return conn;
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            return null;
        }
    }
}