import javax.swing.*;           // Swing components for UI
import java.awt.*;              // AWT for layouts
import java.awt.event.*;        // Listeners (ActionEvent, etc.)
import java.io.Serializable;    // To fix serialization warning
import java.util.List;          // For working with collections

// Class to create UI for the bookstore inventory system
public class BookstoreUI extends JFrame implements Serializable {
    private static final long serialVersionUID = 1L;  // Fix for serialization warning

    private final BookstoreInventory inventory;  // Reference to inventory system
    private final JTextArea inventoryDisplay;    // UI component to show inventory list

    // Panels used for different functionalities (Sales, and Inventory Management)
    private final JTabbedPane tabbedPane = new JTabbedPane();

    // Sales panel components
    private final JTextField saleBookIDField = new JTextField();
    private final JTextField saleQuantityField = new JTextField();

    // Add Book panel components
    private final JTextField addTitleField = new JTextField();
    private final JTextField addAuthorField = new JTextField();
    private final JTextField addGenreField = new JTextField();
    private final JTextField addPriceField = new JTextField();
    private final JTextField addStockField = new JTextField();

    // Update Stock panel components
    private final JTextField updateBookIDField = new JTextField();
    private final JTextField updateStockField = new JTextField();

    // Delete Book panel components
    private final JTextField deleteBookIDField = new JTextField();

    // Constructor – Initializes the extended bookstore UI
    public BookstoreUI (BookstoreInventory inventory) {
        this.inventory = inventory;

        setTitle("Extended Bookstore Inventory Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Build and add the Tabbed Pane with two main tabs:
        //  1. Sales/Invoice Entry, 2. Inventory Management
        tabbedPane.addTab("Sales / Invoices", createSalesPanel());
        tabbedPane.addTab("Manage Inventory", createInventoryPanel());
        add(tabbedPane, BorderLayout.CENTER);

        // Display area at the bottom to view updated inventory
        inventoryDisplay = new JTextArea();
        inventoryDisplay.setEditable(false);
        add(new JScrollPane(inventoryDisplay), BorderLayout.SOUTH);

        updateInventoryDisplay();  // Initial inventory display
    }

    // Build the Sales / Invoice Entry panel
    private JPanel createSalesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Form for sales input
        JPanel form = new JPanel(new GridLayout(2, 2, 5, 5));
        form.add(new JLabel("Book ID:"));
        form.add(saleBookIDField);
        form.add(new JLabel("Quantity:"));
        form.add(saleQuantityField);

        // Button panel for sale processing and refreshing
        JPanel btnPanel = new JPanel();
        JButton saleBtn = new JButton("Make Sale");
        saleBtn.addActionListener(e -> processSale());
        JButton refreshBtn = new JButton("Refresh Inventory");
        refreshBtn.addActionListener(e -> updateInventoryDisplay());
        btnPanel.add(saleBtn);
        btnPanel.add(refreshBtn);

        panel.add(form, BorderLayout.NORTH);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Build the Inventory Management panel for adding, updating, and deleting books
    private JPanel createInventoryPanel() {
        // We'll split the panel vertically into three sections.
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 10));

        // ----- Add Book Section -----
        JPanel addPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        addPanel.setBorder(BorderFactory.createTitledBorder("Add Book"));
        addPanel.add(new JLabel("Title:"));
        addPanel.add(addTitleField);
        addPanel.add(new JLabel("Author:"));
        addPanel.add(addAuthorField);
        addPanel.add(new JLabel("Genre:"));
        addPanel.add(addGenreField);
        addPanel.add(new JLabel("Price:"));
        addPanel.add(addPriceField);
        addPanel.add(new JLabel("Stock:"));
        addPanel.add(addStockField);
        JButton addBtn = new JButton("Add Book");
        addBtn.addActionListener(e -> addBook());
        addPanel.add(new JLabel());  // filler
        addPanel.add(addBtn);

        // ----- Update Stock Section -----
        JPanel updatePanel = new JPanel(new GridLayout(3, 2, 5, 5));
        updatePanel.setBorder(BorderFactory.createTitledBorder("Update Stock"));
        updatePanel.add(new JLabel("Book ID:"));
        updatePanel.add(updateBookIDField);
        updatePanel.add(new JLabel("New Stock:"));
        updatePanel.add(updateStockField);
        JButton updateBtn = new JButton("Update Stock");
        updateBtn.addActionListener(e -> updateStock());
        updatePanel.add(new JLabel());  // filler
        updatePanel.add(updateBtn);

        // ----- Delete Book Section -----
        JPanel deletePanel = new JPanel(new GridLayout(2, 2, 5, 5));
        deletePanel.setBorder(BorderFactory.createTitledBorder("Delete Book"));
        deletePanel.add(new JLabel("Book ID:"));
        deletePanel.add(deleteBookIDField);
        JButton deleteBtn = new JButton("Delete Book");
        deleteBtn.addActionListener(e -> deleteBook());
        deletePanel.add(new JLabel());  // filler
        deletePanel.add(deleteBtn);

        panel.add(addPanel);
        panel.add(updatePanel);
        panel.add(deletePanel);
        return panel;
    }

    // Process a sale from the Sales tab
    private void processSale() {
        try {
            int bookID = Integer.parseInt(saleBookIDField.getText().trim());
            int quantity = Integer.parseInt(saleQuantityField.getText().trim());
            if (quantity <= 0) {
                throw new NumberFormatException("Quantity must be positive.");
            }
            inventory.processSale(bookID, quantity);
            JOptionPane.showMessageDialog(this, "Sale processed successfully!",
                                          "Success", JOptionPane.INFORMATION_MESSAGE);
            saleBookIDField.setText("");
            saleQuantityField.setText("");
            updateInventoryDisplay();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter numeric values (and a positive quantity).",
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Add a new book using input from the Add Book section
    private void addBook() {
        try {
            String title = addTitleField.getText().trim();
            String author = addAuthorField.getText().trim();
            String genre = addGenreField.getText().trim();
            double price = Double.parseDouble(addPriceField.getText().trim());
            int stock = Integer.parseInt(addStockField.getText().trim());
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // BookID is auto-generated by the database.
            Books newBook = new Books(0, title, author, genre, price, stock);
            inventory.addBook(newBook);
            JOptionPane.showMessageDialog(this, "Book added successfully!",
                                          "Success", JOptionPane.INFORMATION_MESSAGE);
            addTitleField.setText("");
            addAuthorField.setText("");
            addGenreField.setText("");
            addPriceField.setText("");
            addStockField.setText("");
            updateInventoryDisplay();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price and Stock must be numeric.",
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Update an existing book's stock using input from the Update Stock section
    private void updateStock() {
        try {
            int bookID = Integer.parseInt(updateBookIDField.getText().trim());
            int newStock = Integer.parseInt(updateStockField.getText().trim());
            inventory.updateStock(bookID, newStock);
            JOptionPane.showMessageDialog(this, "Stock updated successfully!",
                                          "Success", JOptionPane.INFORMATION_MESSAGE);
            updateBookIDField.setText("");
            updateStockField.setText("");
            updateInventoryDisplay();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Book ID and New Stock must be numeric.",
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Delete a book using input from the Delete Book section
    private void deleteBook() {
        try {
            int bookID = Integer.parseInt(deleteBookIDField.getText().trim());
            inventory.deleteBook(bookID);
            JOptionPane.showMessageDialog(this, "Book deleted successfully!",
                                          "Success", JOptionPane.INFORMATION_MESSAGE);
            deleteBookIDField.setText("");
            updateInventoryDisplay();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Book ID must be numeric.",
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Update the inventory display text area with current inventory data 
    private void updateInventoryDisplay() {
        List<Books> booksList = inventory.getBooksList();
        StringBuilder sb = new StringBuilder("Current Inventory:\n");
        for (Books b : booksList) {
            sb.append(b).append("\n");
        }
        inventoryDisplay.setText(sb.toString());
    }

    // Main method to launch the extended UI
    public static void main(String[] args) {
        // Create your inventory system and seed it with sample books
        BookstoreInventory inventory = new BookstoreInventory();
        inventory.addBook(new Books(1, "Harry Potter", "J.K. Rowling", "Fantasy", 19.99, 50));
        inventory.addBook(new Books(2, "The Hobbit", "J.R.R. Tolkien", "Fantasy", 14.99, 10));

        SwingUtilities.invokeLater(() -> {
        	BookstoreUI ui = new BookstoreUI(inventory);
            ui.setVisible(true);
        });
    }
}