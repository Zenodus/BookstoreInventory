import javax.swing.*;           // Swing components for UI
import java.awt.*;              // AWT for layouts
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;    // To silence the serial-warning
import java.util.List;

/**
 * Graphical UI for the bookstore inventory system.
 */
public class BookstoreUI extends JFrame implements Serializable {
    private static final long serialVersionUID = 1L;      // serialization id

    /* core references */
    private final BookstoreInventory inventory;           // inventory layer
    private final JTextArea inventoryDisplay;             // inventory text area

    /*TAB #1  – Sales controls */
    private final JTextField saleBookIdField   = new JTextField();
    private final JTextField saleQtyField      = new JTextField();

    /* TAB #2 – Add-book controls */
    private final JTextField addTitleField     = new JTextField();
    private final JTextField addAuthorField    = new JTextField();
    private final JTextField addGenreField     = new JTextField();
    private final JTextField addPriceField     = new JTextField();
    private final JTextField addStockField     = new JTextField();

    /* TAB #3 – Update-stock controls */
    private final JTextField updBookIdField    = new JTextField();
    private final JTextField updNewStockField  = new JTextField();

    /* TAB #4 – Delete-book controls */
    private final JTextField delBookIdField    = new JTextField();

    public BookstoreUI(BookstoreInventory inventory) {
        this.inventory = inventory;

        /*  Frame basics  */
        setTitle("Bookstore Inventory Management");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        /*  CENTER  inventory text area in a scroll pane  */
        inventoryDisplay = new JTextArea();
        inventoryDisplay.setEditable(false);
        add(new JScrollPane(inventoryDisplay), BorderLayout.CENTER);

        /*  NORTH  the tabbed forms  */
        add(buildTabs(), BorderLayout.NORTH);

        /*  SOUTH  global refresh button  */
        JButton refreshBtn = new JButton("Refresh Inventory");
        refreshBtn.addActionListener(e -> updateInventoryDisplay());
        add(refreshBtn, BorderLayout.SOUTH);

        /*  Initial fill  */
        updateInventoryDisplay();
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();

        /* Sales Tab */
        JPanel salesPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        salesPanel.add(new JLabel("Book ID:"));
        salesPanel.add(saleBookIdField);
        salesPanel.add(new JLabel("Quantity:"));
        salesPanel.add(saleQtyField);

        JButton saleBtn = new JButton("Make Sale");
        saleBtn.addActionListener(e -> makeSale());
        salesPanel.add(new JLabel());  // filler
        salesPanel.add(saleBtn);
        tabs.addTab("Sales", salesPanel);

        /* Add-Book Tab */
        JPanel addPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        addPanel.add(new JLabel("Title:"));   addPanel.add(addTitleField);
        addPanel.add(new JLabel("Author:"));  addPanel.add(addAuthorField);
        addPanel.add(new JLabel("Genre:"));   addPanel.add(addGenreField);
        addPanel.add(new JLabel("Price:"));   addPanel.add(addPriceField);
        addPanel.add(new JLabel("Stock:"));   addPanel.add(addStockField);

        JButton addBtn = new JButton("Add Book");
        addBtn.addActionListener(e -> addBook());
        addPanel.add(new JLabel());   // filler
        addPanel.add(addBtn);
        tabs.addTab("Add Book", addPanel);

        /* Update-Stock Tab */
        JPanel updPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        updPanel.add(new JLabel("Book ID:"));   updPanel.add(updBookIdField);
        updPanel.add(new JLabel("New Stock:")); updPanel.add(updNewStockField);

        JButton updBtn = new JButton("Update Stock");
        updBtn.addActionListener(e -> updateStock());
        updPanel.add(new JLabel());   // filler
        updPanel.add(updBtn);
        tabs.addTab("Update Stock", updPanel);

        /* Delete-Book Tab */
        JPanel delPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        delPanel.add(new JLabel("Book ID:"));
        delPanel.add(delBookIdField);

        JButton delBtn = new JButton("Delete Book");
        delBtn.addActionListener(e -> deleteBook());
        delPanel.add(new JLabel());   // filler
        delPanel.add(delBtn);
        tabs.addTab("Delete Book", delPanel);

        return tabs;
    }

    private void makeSale() {
        try {
            int id  = Integer.parseInt(saleBookIdField.getText().trim());
            int qty = Integer.parseInt(saleQtyField.getText().trim());
            inventory.processSale(id, qty);
            JOptionPane.showMessageDialog(this, "Sale recorded.");
            clearFields(saleBookIdField, saleQtyField);
            updateInventoryDisplay();
        } catch (NumberFormatException ex) {
            showError("Enter numeric Book-ID and Quantity.");
        }
    }

    private void addBook() {
        try {
            String title  = addTitleField.getText().trim();
            String author = addAuthorField.getText().trim();
            String genre  = addGenreField.getText().trim();
            double price  = Double.parseDouble(addPriceField.getText().trim());
            int stock     = Integer.parseInt(addStockField.getText().trim());

            if (title.isEmpty()) { showError("Title cannot be blank."); return; }

            // BookID auto-generated by DB ‑ use 0 for placeholder
            Books b = new Books(0, title, author, genre, price, stock);
            inventory.addBook(b);
            JOptionPane.showMessageDialog(this, "Book added.");
            clearFields(addTitleField, addAuthorField, addGenreField, addPriceField, addStockField);
            updateInventoryDisplay();
        } catch (NumberFormatException ex) {
            showError("Price and Stock must be numeric.");
        }
    }

    private void updateStock() {
        try {
            int id   = Integer.parseInt(updBookIdField.getText().trim());
            int qty  = Integer.parseInt(updNewStockField.getText().trim());
            inventory.updateStock(id, qty);
            JOptionPane.showMessageDialog(this, "Stock updated.");
            clearFields(updBookIdField, updNewStockField);
            updateInventoryDisplay();
        } catch (NumberFormatException ex) {
            showError("Enter numeric Book-ID and New Stock.");
        }
    }

    private void deleteBook() {
        try {
            int id = Integer.parseInt(delBookIdField.getText().trim());
            inventory.deleteBook(id);
            JOptionPane.showMessageDialog(this, "Book deleted.");
            clearFields(delBookIdField);
            updateInventoryDisplay();
        } catch (NumberFormatException ex) {
            showError("Enter numeric Book-ID.");
        }
    }

    private void updateInventoryDisplay() {
        List<Books> list = inventory.getBooksList();
        StringBuilder sb = new StringBuilder("Current Inventory:\n");
        for (Books b : list) sb.append(b).append('\n');
        inventoryDisplay.setText(sb.toString());
    }

    private void clearFields(JTextField... fields) {
        for (JTextField f : fields) f.setText("");
    }
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Input Error", JOptionPane.ERROR_MESSAGE);
    }


    public static void main(String[] args) {
        BookstoreInventory inv = new BookstoreInventory();
        // sample seed data
        inv.addBook(new Books(1, "Harry Potter", "J.K. Rowling", "Fantasy", 19.99, 40));
        inv.addBook(new Books(2, "The Hobbit", "J.R.R. Tolkien", "Fantasy", 14.99, 25));

        SwingUtilities.invokeLater(() -> new BookstoreUI(inv).setVisible(true));
    }
}