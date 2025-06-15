import java.util.ArrayList;      // Import ArrayList to store books (local cache if you want one later)
import java.util.List;           // Import List interface for managing collections
import java.util.logging.*;      // Import logging framework for tracking inventory changes
import java.io.IOException;      // Import IOException for file handling

import java.sql.Connection;      // Import SQL Connection for database connectivity
import java.sql.PreparedStatement;   // Import PreparedStatement for executing SQL queries
import java.sql.ResultSet;       // Import ResultSet for retrieving query results
import java.sql.SQLException;    // Import SQLException for handling database errors
import java.sql.Timestamp;       // Import Timestamp for the invoice date
import java.util.Date;           // Import Date so the Timestamp import is truly used

// Class to manage bookstore inventory operations
public class BookstoreInventory {
    private static final Logger logger =
            Logger.getLogger(BookstoreInventory.class.getName());  // Logger instance

    /* Logging configuration */
    static {
        try {
            FileHandler fileHandler = new FileHandler("bookstore_logs.txt", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(consoleHandler);

            logger.setLevel(Level.INFO);
        } catch (IOException e) {
            System.out.println("Error initializing logging system: " + e.getMessage());
        }
    }

    public BookstoreInventory() {
        logger.log(Level.INFO, "Bookstore Inventory system initialized.");
    }

    // Method to add a new book to the inventory (stored in MySQL)
    public void addBook(Books book) {
        String sql =
                "INSERT INTO books (title, author, genre, price, stockQuantity) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getGenre());
            stmt.setDouble(4, book.getPrice());
            stmt.setInt   (5, book.getStockQuantity());
            stmt.executeUpdate();

            System.out.println("Book added to database: " + book.getTitle());
            logger.log(Level.INFO,
                    "Book added: " + book.getTitle() + " | Stock: " + book.getStockQuantity());
        } catch (SQLException e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    // Method to update the stock of a specific book in MySQL
    public void updateStock(int bookID, int newStock) {
        String sql = "UPDATE books SET stockQuantity = ? WHERE bookID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newStock);
            stmt.setInt(2, bookID);
            stmt.executeUpdate();

            System.out.println("Stock updated for Book ID: " + bookID + " | New Stock: " + newStock);
            logger.log(Level.INFO,
                    "Stock updated for Book ID: " + bookID + " | New Stock: " + newStock);
        } catch (SQLException e) {
            System.out.println("Error updating stock: " + e.getMessage());
        }
    }

    // Method to delete a book from inventory in MySQL
    public void deleteBook(int bookID) {
        String sql = "DELETE FROM books WHERE bookID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookID);
            stmt.executeUpdate();

            System.out.println("Book ID " + bookID + " removed from database.");
            logger.log(Level.INFO, "Book ID " + bookID + " removed from inventory.");
        } catch (SQLException e) {
            System.out.println("Error deleting book: " + e.getMessage());
        }
    }

    // Method to process a book sale (updates stock AND writes an invoice row)
    public void processSale(int bookID, int quantity) {

        /* 1. Reduce stock    2. Create invoice    (do both in one DB transaction) */
        final String updateStockSQL =
                "UPDATE books SET stockQuantity = stockQuantity - ? WHERE bookID = ?";

        final String insertInvoiceSQL =
                "INSERT INTO invoices (saleDate, bookID, bookTitle, quantity, totalPrice) " +
                "VALUES (?, ?, (SELECT title FROM books WHERE bookID = ?), ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);  // Begin atomic block

            try (PreparedStatement stockStmt   = conn.prepareStatement(updateStockSQL);
                 PreparedStatement invoiceStmt = conn.prepareStatement(insertInvoiceSQL)) {

                /* ---------- Update stock ---------- */
                stockStmt.setInt(1, quantity);
                stockStmt.setInt(2, bookID);
                stockStmt.executeUpdate();

                /* ---------- Insert invoice ---------- */
                double priceEach = getBookPrice(bookID);
                double total     = priceEach * quantity;

                // Uses Date → keeps the Date import valid and removes unused-import warning
                Timestamp now = new Timestamp(new Date().getTime());

                invoiceStmt.setTimestamp(1, now);  // saleDate
                invoiceStmt.setInt      (2, bookID);
                invoiceStmt.setInt      (3, bookID);  // for sub-query placeholder
                invoiceStmt.setInt      (4, quantity);
                invoiceStmt.setDouble   (5, total);
                invoiceStmt.executeUpdate();

                conn.commit();  // All good
                System.out.println("Sale processed successfully for Book ID: " + bookID);
                logger.log(Level.INFO,
                        "Sale processed | Book ID: " + bookID + " | Qty: " + quantity +
                        " | Total: $" + total);

            } catch (SQLException inner) {
                conn.rollback();                 // Undo both steps if either fails
                throw inner;
            }

        } catch (SQLException e) {
            System.out.println("Error processing sale: " + e.getMessage());
        }
    }

    // Method to retrieve inventory from MySQL
    public void displayInventory() {
        String sql = "SELECT * FROM books";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n---- Current Inventory ----");
            while (rs.next()) {
                System.out.println(
                        "Book ID: " + rs.getInt("bookID") +
                        ", Title: " + rs.getString("title") +
                        ", Author: " + rs.getString("author") +
                        ", Genre: " + rs.getString("genre") +
                        ", Price: $" + rs.getDouble("price") +
                        ", Stock: " + rs.getInt("stockQuantity"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving inventory: " + e.getMessage());
        }
    }

    // Method to retrieve book price from MySQL
    private double getBookPrice(int bookID) {
        String sql = "SELECT price FROM books WHERE bookID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble("price");

        } catch (SQLException e) {
            System.out.println("Error retrieving book price: " + e.getMessage());
        }
        return 0.0;
    }

    // Optional helper for UI: return list of Books objects
    public List<Books> getBooksList() {
        List<Books> list = new ArrayList<>();
        String sql = "SELECT * FROM books";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Books(
                        rs.getInt("bookID"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("genre"),
                        rs.getDouble("price"),
                        rs.getInt("stockQuantity")));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving book list: " + e.getMessage());
        }
        return list;
    }

    public static void main(String[] args) {
        Connection conn = DatabaseConnection.getConnection();
        System.out.println(conn != null
                ? "Connected to MySQL database successfully!"
                : "Failed to connect to MySQL!!!");

        BookstoreInventory inv = new BookstoreInventory();
        inv.displayInventory();
    }
}