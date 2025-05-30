// Defines a class representing a book in inventory
public class Books {
    private int bookID;        // Unique identifier for each book
    private String title;      // Title of the book
    private String author;     // Author of the book
    private String genre;      // Genre/category of the book
    private double price;      // Price of the book
    private int stockQuantity; // Number of copies available in inventory

    // Constructor to initialize a book with all its details
    public Books(int bookID, String title, String author, String genre, double price, int stockQuantity) {
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    // Getter methods to retrieve book details
    public int getBookID() { return bookID; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public double getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }

    // Method to update the stock quantity for this book
    public void updateStock(int newStock) {
        this.stockQuantity = newStock;
    }

    // Method to display book information in a readable format
    @Override
    public String toString() {
        return "Book ID: " + bookID + ", Title: " + title + ", Author: " + author +
               ", Genre: " + genre + ", Price: $" + price + ", Stock: " + stockQuantity;
    }
}