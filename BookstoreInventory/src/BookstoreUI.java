import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BookstoreUI {
    private JFrame frame;
    private JList<String> bookList;
    private DefaultListModel<String> listModel;
    private BookstoreInventory inventory; // Connects UI to inventory system

    public BookstoreUI() {
        inventory = new BookstoreInventory(); // Initialize inventory system

        // Setup JFrame (Main Window)
        frame = new JFrame("Bookstore Inventory");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Setup book list display
        listModel = new DefaultListModel<>();
        bookList = new JList<>(listModel);
        updateList(); // Populate list with inventory

        // Button to update stock
        JButton updateStockButton = new JButton("Update Stock");
        updateStockButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int bookID = Integer.parseInt(JOptionPane.showInputDialog("Enter Book ID:"));
                int newStock = Integer.parseInt(JOptionPane.showInputDialog("Enter new stock quantity:"));
                inventory.updateStock(bookID, newStock);
                updateList();
            }
        });

        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(bookList), BorderLayout.CENTER);
        frame.add(updateStockButton, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void updateList() {
        listModel.clear();
        for (Books book : inventory.getBooksList()) {
            listModel.addElement(book.getTitle() + " | Stock: " + book.getStockQuantity());
        }
    }

    public static void main(String[] args) {
        new BookstoreUI();
    }
}