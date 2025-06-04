import java.util.Date;

// Represents a supplier restock request for books
public class ReorderRequest {
    private int requestID;
    private int bookID;
    private String bookTitle;
    private int quantityRequested;
    private String supplierName;
    private String status;
    private Date requestDate;

    // Constructor - Creates a new reorder request
    public ReorderRequest(int bookID, String bookTitle, int quantityRequested, String supplierName) {
        this.bookID = bookID;
        this.bookTitle = bookTitle;
        this.quantityRequested = quantityRequested;
        this.supplierName = supplierName;
        this.status = "Pending";  // Default status for new requests
        this.requestDate = new Date();
    }

    // Getters and setters
    public int getRequestID() { return requestID; }
    public int getBookID() { return bookID; }
    public String getBookTitle() { return bookTitle; }
    public int getQuantityRequested() { return quantityRequested; }
    public String getSupplierName() { return supplierName; }
    public String getStatus() { return status; }
    public Date getRequestDate() { return requestDate; }

    // Update request status
    public void approveRequest() { this.status = "Approved"; }
    public void rejectRequest() { this.status = "Rejected"; }

    // Display request details
    @Override
    public String toString() {
        return "Reorder Request - Book: " + bookTitle + ", Quantity: " + quantityRequested +
               ", Supplier: " + supplierName + ", Status: " + status + ", Date: " + requestDate;
    }
}