import java.util.ArrayList;   // Import ArrayList to store books and transactions
import java.util.List;        // Import List interface for flexibility
import java.util.logging.*;   // Import Java's built-in logging framework
import java.io.IOException;   // Import IOException for handling file logging errors
import java.util.Date;        // Import Date class for timestamps in invoices and reorder requests

// Class to manage the bookstore's inventory system
public class BookstoreInventory {
    private List<Books> books = new ArrayList<>();  // List to store all books in inventory
    private List<Invoice> invoices = new ArrayList<>();  // List to store invoices for sales
    private List<ReorderRequest> reorderRequests = new ArrayList<>();  // List to store supplier restock requests
    private static final Logger logger = Logger.getLogger(BookstoreInventory.class.getName()); // Logger instance

    // Configure logging (writes logs to console & file)
    static {
        try {
            // Create a file handler to store logs in 'bookstore_logs.txt'
            FileHandler fileHandler = new FileHandler("bookstore_logs.txt", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            // Console handler to display logs in terminal
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(consoleHandler);

            // Set default logging level to INFO
            logger.setLevel(Level.INFO);
        } catch (IOException e) {
            System.out.println("Error initializing logging system: " + e.getMessage());
        }
    }

    // Constructor - Logs when the inventory system starts
    public BookstoreInventory() {
        logger.log(Level.INFO, "Bookstore Inventory system initialized.");
    }

    // Method to add a new book to the inventory
    public void addBook(Books book) {
        books.add(book);  // Adds a book to the inventory list
        logger.log(Level.INFO, "Book added: " + book.getTitle() + " | Stock: " + book.getStockQuantity());
    }

    // Method to display all books in inventory
    public void displayInventory() {
        if (books.isEmpty()) {  // Checks if inventory is empty
            System.out.println("No books in inventory.");
            logger.log(Level.WARNING, "Inventory check: No books available.");
        } else {
            for (Books book : books) {  // Loops through the list and prints each book
                System.out.println(book);
            }
            logger.log(Level.INFO, "Displayed current inventory list.");
        }
    }

    // Method to update the stock quantity of a specific book
    public void updateStock(int bookID, int newStock) {
        for (Books book : books) {  // Loops through books to find matching book ID
            if (book.getBookID() == bookID) {  // If book is found, update stock quantity
                book.updateStock(newStock);
                System.out.println("Updated stock for: " + book.getTitle());
                logger.log(Level.INFO, "Stock updated for: " + book.getTitle() + " | New Stock: " + newStock);
                return;
            }
        }
        System.out.println("Book ID not found.");  // Message if book ID does not exist
        logger.log(Level.WARNING, "Update failed: Book ID " + bookID + " not found.");
    }

    // Method to delete a book from inventory based on book ID
    public void deleteBook(int bookID) {
        books.removeIf(book -> book.getBookID() == bookID);  // Removes book from list
        System.out.println("Book ID " + bookID + " removed from inventory.");
        logger.log(Level.INFO, "Book ID " + bookID + " removed from inventory.");
    }

    // Method to check if any books are running low on stock
    public void checkLowStock(int threshold) {
        for (Books book : books) {
            if (book.getStockQuantity() <= threshold) {  // If stock is below threshold, show alert
                System.out.println("Low stock alert: " + book.getTitle() + " (Stock: " + book.getStockQuantity() + ")");
                logger.log(Level.WARNING, "Low stock alert: " + book.getTitle() + " | Stock: " + book.getStockQuantity());
            }
        }
    }

    // Method to return the list of books (used for UI display)
    public List<Books> getBooksList() {
        return books;  // Returns the current inventory list
    }

    // ----------------------------------------------
    // NEW WEEK 4 FEATURE: Supplier Request System
    // ----------------------------------------------
    
    // Method to allow users to manually request additional stock from suppliers
    public void requestStockFromSupplier(int bookID, int quantity, String supplierName) {
        for (Books book : books) {
            if (book.getBookID() == bookID) {
                ReorderRequest request = new ReorderRequest(bookID, book.getTitle(), quantity, supplierName);
                reorderRequests.add(request);
                System.out.println("Supplier request placed: " + request);
                logger.log(Level.INFO, "Supplier request placed for: " + book.getTitle() + " | Quantity: " + quantity + " | Supplier: " + supplierName);
                return;
            }
        }
        System.out.println("Supplier request failed: Book ID not found.");
        logger.log(Level.WARNING, "Supplier request failed: Book ID " + bookID + " not found.");
    }

    // ----------------------------------------------
    // NEW WEEK 4 FEATURE: Low Stock Automation - Suggest Reorders
    // ----------------------------------------------
    
    // Method to automatically detect low stock and suggest a reorder request
    public void checkLowStockAndSuggestReorder(int threshold) {
        for (Books book : books) {
            if (book.getStockQuantity() <= threshold) {
                System.out.println("Low stock detected for: " + book.getTitle());
                System.out.println("Suggested reorder request: Quantity " + (threshold * 2)); // Suggests double reorder
                
                // Automatically place reorder request
                ReorderRequest newRequest = new ReorderRequest(book.getBookID(), book.getTitle(), threshold * 2, "Default Supplier");
                reorderRequests.add(newRequest);
                System.out.println("Reorder request placed: " + newRequest);
                logger.log(Level.INFO, "Auto-reorder request placed for: " + book.getTitle() + " | Suggested Quantity: " + (threshold * 2));
            }
        }
    }

    // Method to display all reorder requests
    public void displayReorderRequests() {
        System.out.println("\n---- Reorder Requests ----");
        if (reorderRequests.isEmpty()) {
            System.out.println("No supplier requests yet.");
        } else {
            for (ReorderRequest request : reorderRequests) {
                System.out.println(request);
            }
        }
        logger.log(Level.INFO, "Displayed all reorder requests.");
    }

    // Main method to simulate bookstore operations
    public static void main(String[] args) {
        BookstoreInventory inventory = new BookstoreInventory(); // Create inventory system
        
        // Adding sample books manually
        inventory.addBook(new Books(1, "Harry Potter and the Sorcerer's Stone", "J.K. Rowling", "Fantasy", 19.99, 50));
        inventory.addBook(new Books(2, "The Hobbit", "J.R.R. Tolkien", "Fantasy", 14.99, 10));

        // Display inventory
        inventory.displayInventory();

        // Process supplier request manually
        inventory.requestStockFromSupplier(2, 20, "Book Supplier Inc.");

        // Auto-reorder suggestion for low stock threshold (below 15)
        inventory.checkLowStockAndSuggestReorder(15);

        // Display reorder requests
        inventory.displayReorderRequests();
    }
}