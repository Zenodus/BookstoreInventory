import java.util.Date;  // Import Date class to track invoice creation date

// Represents an invoice for a book sale transaction
public class Invoice {
    private int invoiceID;    // Unique identifier for each invoice
    private Date date;        // Date when the invoice was created
    private String bookTitle; // Title of the book being sold
    private int quantity;     // Number of copies purchased
    private double totalPrice; // Total amount paid for the purchase

    // Constructor - Creates an invoice when a book is sold
    public Invoice(int invoiceID, Date date, String bookTitle, int quantity, double totalPrice) {
        this.invoiceID = invoiceID;  // Assigns a unique invoice number
        this.date = date;            // Sets the purchase date
        this.bookTitle = bookTitle;  // Stores the title of the purchased book
        this.quantity = quantity;    // Stores the number of copies sold
        this.totalPrice = totalPrice; // Calculates the total cost of the sale
    }

    // Getter methods - Allow access to invoice details
    public int getInvoiceID() { return invoiceID; }  // Returns invoice ID
    public Date getDate() { return date; }  // Returns invoice date
    public String getBookTitle() { return bookTitle; }  // Returns book title
    public int getQuantity() { return quantity; }  // Returns quantity sold
    public double getTotalPrice() { return totalPrice; }  // Returns total sale amount

    // Displays invoice details in a user-friendly format
    @Override
    public String toString() {
        return "Invoice ID: " + invoiceID + ", Date: " + date + ", Book: " + bookTitle + 
               ", Quantity: " + quantity + ", Total: $" + totalPrice;
    }
}