package org.example.sasalele_pos;

import org.example.sasalele_pos.database.LogDAO;
import org.example.sasalele_pos.database.ProductDAO;
import org.example.sasalele_pos.database.UserDAO;
import org.example.sasalele_pos.exceptions.InvalidProductException;
import org.example.sasalele_pos.exceptions.InvalidTransactionException;
import org.example.sasalele_pos.functions.CurrencyParser;
import org.example.sasalele_pos.model.*;
import org.example.sasalele_pos.services.ProductService;
import org.example.sasalele_pos.services.TransactionService;
import org.example.sasalele_pos.transactions.RefundTransaction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DashboardApp extends JPanel {

    private String[][] data = null;

    JLabel idLabel, nameLabel, priceLabel;
    JTextField idTextField, nameField, priceField;
    JPanel buttonPanel, productDetailsPanel;
    JButton cancelButton, saveButton;

    private final JPanel sidebarPanel, mainPanel;
    private CardLayout cardLayout;  // To switch between different content in the main panel

    static User currentUser;

    public DashboardApp(User currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout());

        // Create Sidebar and Main content area
        sidebarPanel = createSidebar();
        mainPanel = createMainPanel();

        // Add Sidebar and Main Panel to the frame
        add(sidebarPanel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));  // Stack components vertically
        sidebar.setPreferredSize(new Dimension(200, getHeight()));
        sidebar.setBackground(new Color(34, 40, 49));  // Sidebar background color

        // Top label "Sasa - Lele"
        JLabel topLabel = new JLabel("Sasa - Lele", JLabel.CENTER);
        topLabel.setFont(new Font("Serif", Font.BOLD, 20));
        topLabel.setForeground(Color.WHITE);
        topLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(topLabel);
        sidebar.add(Box.createVerticalStrut(20)); // Space between top label and buttons

        // Add filler to push buttons to the center vertically
        sidebar.add(Box.createVerticalGlue());

        // Buttons for Sidebar sections
        String[] buttonLabels = {"Transaksi", "Produk", "Akun", "Log"};
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.PLAIN, 18));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setPreferredSize(new Dimension(180, 40));
            button.addActionListener(new SidebarButtonListener());
            button.setBackground(Color.WHITE);  // Default background color (white)
            button.setForeground(Color.BLACK);  // Text color
            sidebar.add(button);
            sidebar.add(Box.createVerticalStrut(10)); // Space between buttons
        }

        // Add filler to push buttons to the center vertically (if needed)
        sidebar.add(Box.createVerticalGlue());

        // Logout button (to go back to the login screen)
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 18));
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setPreferredSize(new Dimension(180, 40));
        logoutButton.addActionListener(e -> openLoginScreen());
        sidebar.add(logoutButton);
        sidebar.add(Box.createVerticalStrut(10));

        return sidebar;
    }

    private JPanel createMainPanel() {
        cardLayout = new CardLayout();
        JPanel panel = new JPanel(cardLayout);

        // Create panels for each section in the main frame
        JPanel transaksiPanel = createTransaksiPanel();
        JPanel produkPanel = createProdukPanel();
        JPanel akunPanel = createAkunPanel();
        JPanel logPanel = createLogPanel();

        // Add all panels to the main panel (card layout)
        panel.add(transaksiPanel, "Transaksi");
        panel.add(produkPanel, "Produk");
        panel.add(akunPanel, "Akun");
        panel.add(logPanel, "Log");

        // Initially show Transaksi panel
        cardLayout.show(panel, "Transaksi");

        return panel;
    }

    // Create a sample "Transaksi" panel
    private JPanel createTransaksiPanel() {
        JPanel transaksiPanel = new JPanel();
        transaksiPanel.setBackground(Color.WHITE);

        transaksiPanel.setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel("Transaksi", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        transaksiPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerLayout = new JPanel();
        centerLayout.setBackground(Color.WHITE);

        GridLayout gridLayout = new GridLayout(1, 2);
        gridLayout.setHgap(10);
        centerLayout.setLayout(gridLayout);
        centerLayout.setBorder(new EmptyBorder(10, 10, 10, 10));

        // col1Panel to hold the table
        JPanel col1Panel = new JPanel();
        col1Panel.setLayout(new BorderLayout());

        // Create JTable for the Products data
        JTable col1Table = createProductTable();
        JScrollPane tableScrollPane = new JScrollPane(col1Table);  // Add table inside a JScrollPane
        col1Panel.add(tableScrollPane, BorderLayout.CENTER);  // Add table to col1Panel

        // Add col1Panel to the centerLayout
        centerLayout.add(col1Panel);

        List<CartItem> cartItems = new ArrayList<>();

        JPanel col2Panel = new JPanel();
        col2Panel.setLayout(new BorderLayout());
        centerLayout.setBorder(new EmptyBorder(10, 10, 10, 10));
        centerLayout.add(col2Panel);

        // Column names
        String[] columnNames = {"ID", "Nama Produk", "Qty", "Harga", "Aksi"};

        // Empty Data
        String[][] data = new String[0][5];

        // Create a table model and JTable
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(tableModel);
        table.setRowHeight(40);

        // Add Action buttons to the "Aksi" column (Edit, Delete)
        table.getColumn("Aksi").setCellRenderer(new ButtonRendererTransaksi());
        table.getColumn("Aksi").setCellEditor(new ButtonEditorTransaksi(new JCheckBox()));

        JScrollPane tableScrollPane2 = new JScrollPane(table);
        col2Panel.add(tableScrollPane2, BorderLayout.CENTER);

        JPanel col2TopRow1 = new JPanel();
        GridLayout fieldRow1 = new GridLayout(1, 3);
        col2TopRow1.setLayout(fieldRow1);
        fieldRow1.setVgap(10);  // Vertical gap between components
        col2Panel.add(col2TopRow1, BorderLayout.NORTH);

        JPanel idPanel = new JPanel();
        JLabel idLabel = new JLabel("ID:");
        JTextField idField = new JTextField(10);  // Text field for ID input
        idPanel.add(idLabel);
        idPanel.add(idField);
        col2TopRow1.add(idPanel);

        JPanel qtyPanel = new JPanel();
        JLabel qtyLabel = new JLabel("Quantity:");
        JTextField qtyField = new JTextField(10);  // Text field for Quantity input
        qtyPanel.add(qtyLabel);
        qtyPanel.add(qtyField);
        col2TopRow1.add(qtyPanel);

        JPanel buttonPanel = new JPanel();
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            String idF = idField.getText();
            int qtyF = Integer.parseInt(qtyField.getText());

            addRowtoTable(tableModel, idF, qtyF, cartItems);
        });
        buttonPanel.add(submitButton);
        col2TopRow1.add(buttonPanel);

        centerLayout.add(col2Panel);
        transaksiPanel.add(centerLayout, BorderLayout.CENTER);

        JPanel col2SouthPanel = new JPanel();
        col2SouthPanel.setLayout(new BoxLayout(col2SouthPanel, BoxLayout.Y_AXIS));
        col2SouthPanel.setBorder(new EmptyBorder(10, 10, 50, 10));
        col2Panel.add(col2SouthPanel, BorderLayout.SOUTH);

        JPanel totalPanel = new JPanel();
        JLabel totalLabel = new JLabel("Total Harga: Rp. ");
        JLabel hargaLabel = new JLabel();
        totalPanel.add(totalLabel);
        totalPanel.add(hargaLabel);
        col2SouthPanel.add(totalPanel);

        tableModel.addTableModelListener(e -> {
            setTotalHarga(tableModel, hargaLabel);
        });

        JPanel uangPanel = new JPanel();
        JLabel uangLabel = new JLabel("Uang: ");
        JTextField uangField = new JTextField(10);
        uangPanel.add(uangLabel);
        uangPanel.add(uangField);
        col2SouthPanel.add(uangPanel);

        JPanel transactionButtonPanel = new JPanel();
        JButton transactionButton = new JButton("Transaksi Panel");
        transactionButton.addActionListener(e -> {
            double totalHarga = CurrencyParser.convertCurrencyToDouble(hargaLabel.getText());
            double totalUang = CurrencyParser.convertCurrencyToDouble(uangField.getText());
            double uangKembalian = totalUang - totalHarga;

            if (totalHarga > totalUang) {
                JOptionPane.showMessageDialog(null, "Uang Pelanggan Kurang: Rp." + String.format("%,.2f", (totalHarga - totalUang)));
            } else {
                TransactionService transactionService = new TransactionService();
                try {
                    transactionService.processSale(cartItems, totalUang, currentUser.getUsername());
                    showTransactionDialog(totalHarga, totalUang, uangKembalian, tableModel);
                } catch (InvalidTransactionException ex) {
                    JOptionPane.showMessageDialog(null, "Transaksi gagal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        transactionButtonPanel.add(transactionButton);
        col2SouthPanel.add(transactionButtonPanel);

        return transaksiPanel;
    }

    // Method to set the total harga and update the label (similar to your 'setTotalHarga' method)
    public void setTotalHarga(DefaultTableModel tableModel, JLabel totalLabel) {
        // Initialize the total price to 0
        double totalHarga = 0;

        // Iterate over all the rows of the table (starting from row 0)
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            try {
                // Get the price and quantity for each row
                double price = CurrencyParser.convertCurrencyToDouble((String) tableModel.getValueAt(i, 3));  // Assuming the "Price" column is at index 3
                int quantity = (int) CurrencyParser.convertCurrencyToDouble((String) tableModel.getValueAt(i, 2));     // Assuming the "Quantity" column is at index 2

                // Add the subtotal (price * quantity) to the total price
                totalHarga += price * quantity;
            } catch (Exception e) {
                System.err.println("Error processing row " + i + ": " + e.getMessage());
            }
        }

        // Update the total price in the UI (e.g., a JLabel)
        totalLabel.setText(String.format("%,.2f", totalHarga));
    }

    private static void addRowtoTable(DefaultTableModel tableModel, String id, int qty, List<CartItem> cartItems) {
        Product product = ProductDAO.getProductById(id);

        if (product != null) {
            CartItem newItem = new CartItem(product, qty);
            cartItems.add(newItem);

            String[] newRow = new String[5];
            newRow[0] = product.getId();
            newRow[1] = product.getName();
            newRow[2] = String.valueOf(qty);
            newRow[3] = String.format("Rp. %,.2f", product.getPrice());
            newRow[4] = "Action";

            tableModel.addRow(newRow);
        } else {
            JOptionPane.showMessageDialog(null, "Product not found");
        }
    }

    public String[][] getData() {
        return data;
    }

    public void setData(String[][] data) {
        this.data = data;
    }

    // ButtonRenderer for the Action Buttons with plain text
    static class ButtonRendererTransaksi extends JPanel implements TableCellRenderer {
        public ButtonRendererTransaksi() {
            setLayout(new FlowLayout(FlowLayout.LEFT));  // Align buttons horizontally
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            // Create buttons for the actions using text
            JButton decreaseButton = new JButton("<-");
            JButton deleteButton = new JButton("D");
            JButton increaseButton = new JButton("->");

            // Add buttons to the panel
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(decreaseButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(increaseButton);

            return buttonPanel;
        }
    }

    static class ButtonEditorTransaksi extends DefaultCellEditor {
        private String label;
        private JTable table;

        public ButtonEditorTransaksi(JCheckBox checkBox) {
            super(checkBox);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.table = table;

            // Create action buttons with text labels
            JButton decreaseButton = new JButton("<-");
            JButton deleteButton = new JButton("D");
            JButton increaseButton = new JButton("->");

            // Set action listeners for buttons
            decreaseButton.addActionListener(e -> handleActionButtonClick(row, "decrease"));
            deleteButton.addActionListener(e -> handleActionButtonClick(row, "delete"));
            increaseButton.addActionListener(e -> handleActionButtonClick(row, "increase"));

            // Add buttons to a panel
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(decreaseButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(increaseButton);

            return buttonPanel;
        }

        private void handleActionButtonClick(int row, String action) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            int qty = 0;

            // Ensure the row exists before proceeding
            if (row < 0 || row >= model.getRowCount()) {
                System.out.println("Invalid row index: " + row);
                return;  // Exit if the row is invalid
            }

            // Get current qty as Integer, not String, to avoid casting issues
            try {
                qty = Integer.parseInt((String) model.getValueAt(row, 2));  // Get current qty as Integer
            } catch (NumberFormatException e) {
                System.out.println("Error: " + e.getMessage());
                return;  // Exit if there's an issue with converting qty to Integer
            }

            switch (action) {
                case "decrease" -> {
                    if (qty > 0) {
                        model.setValueAt(String.valueOf(qty - 1), row, 2);  // Decrease qty
                    }
                }
                case "increase" -> model.setValueAt(String.valueOf(qty + 1), row, 2);  // Increase qty
                case "delete" -> {
                    // Only allow row deletion when it's the first row (index 0)
                    if (row == 0) {
                        model.removeRow(row);  // Delete the first row
                    } else {
                        // Show a dialog message if trying to delete any row other than the first row
                        JOptionPane.showMessageDialog(table, "You can only delete the first row.", "Delete Not Allowed", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }

            // Refresh the table after deletion to avoid invalid index reference
            table.revalidate();
            table.repaint();
        }

        @Override
        public Object getCellEditorValue() {
            return label;
        }
    }

    // Show the transaction dialog with total, amount paid, change, and purchased items
    private void showTransactionDialog(double totalHarga, double uangDiberikan, double kembalian, DefaultTableModel tableModel) {
        // Create the dialog to display the transaction details
        JDialog transactionDialog = new JDialog((Frame) null, "Transaksi Detail", true);
        transactionDialog.setSize(400, 500);  // Increase size to accommodate the table and the "Done" button
        transactionDialog.setLocationRelativeTo(null);

        // Panel to hold the text labels and the table
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));  // Stack components vertically

        // Add totalHarga, uangDiberikan, and kembalian labels
        panel.add(new JLabel("Total Harga: Rp. " + String.format("%,.2f", totalHarga)));
        panel.add(new JLabel("Uang Diberikan: Rp. " + String.format("%,.2f", uangDiberikan)));
        panel.add(new JLabel("Kembalian: Rp. " + String.format("%,.2f", kembalian)));

        // Create a table with the purchased items data
        String[] columnNames = {"ID", "Nama Produk", "Qty", "Harga", "Total"};
        String[][] rowData = new String[tableModel.getRowCount()][5];  // Create a rowData array with the same number of rows as the table

        // Loop through the table to fetch the data for the dialog
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            rowData[i][0] = (String) tableModel.getValueAt(i, 0);  // ID
            rowData[i][1] = (String) tableModel.getValueAt(i, 1);  // Nama Produk
            rowData[i][2] = (String) tableModel.getValueAt(i, 2);  // Qty
            rowData[i][3] = (String) tableModel.getValueAt(i, 3);  // Harga
            int qty = Integer.parseInt((String) tableModel.getValueAt(i, 2));
            double harga = CurrencyParser.convertCurrencyToDouble((String) tableModel.getValueAt(i, 3));
            rowData[i][4] = String.format("Rp. " + "%,.2f", qty * harga); // Total (Qty * Harga)
        }

        // Create a table model and JTable for displaying purchased items
        JTable itemsTable = new JTable(rowData, columnNames);
        JScrollPane itemsTableScrollPane = new JScrollPane(itemsTable);

        // Add the table to the panel
        panel.add(itemsTableScrollPane);

        // Create a "Done" button to close the dialog
        JButton doneButton = new JButton("Done");
        doneButton.addActionListener(e -> {
            transactionDialog.dispose();  // Close the dialog when the "Done" button is clicked
        });

        // Add the "Done" button to the panel
        panel.add(doneButton);

        // Add the panel to the dialog
        transactionDialog.add(panel);
        transactionDialog.setVisible(true);
    }

    private JTable createProductTable() {
        // Fetch all products from the database using getAllProducts()
        List<Product> products = ProductDAO.getAllProducts();  // This will fetch the list of products

        // Convert the list of prod objects into a 2D array for the table
        String[][] data = new String[products.size()][4];  // 4 columns (ID, Name, Type, Price)

        // Populate the data array with values from the list
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            data[i][0] = String.valueOf(product.getId());  // ID
            data[i][1] = product.getName();               // Name
            data[i][2] = product.getProductType();        // Type
            data[i][3] = String.valueOf(product.getPrice()); // Price
        }

        // Column names
        String[] columnNames = {"ID", "Nama Produk", "Jenis", "Harga"};

        // Create a table model and JTable
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);

        // Return the table with populated data
        return new JTable(tableModel);
    }

    // Create a sample "Produk" panel
    private JPanel createProdukPanel() {
        // Create the main panel for Produk
        JPanel produkPanel = new JPanel();
        produkPanel.setBackground(Color.WHITE);
        produkPanel.setLayout(new BorderLayout());

        // Title Label for Produk
        JLabel titleLabel = new JLabel("Produk", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        produkPanel.add(titleLabel, BorderLayout.NORTH);

        // Fetch all products from the database using getAllProducts()
        List<Product> products = ProductDAO.getAllProducts();  // This will fetch the list of products

        // Convert the list of prod objects into a 2D array for the table
        String[][] data = new String[products.size()][8];  // 4 columns (ID, Name, Type, Price)

        // Populate the data array with values from the list
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            data[i][0] = String.valueOf(product.getId());  // ID
            data[i][1] = product.getName();               // Name
            data[i][2] = product.getProductType();        // Type
            data[i][3] = String.valueOf(product.getPrice()); // Price

            // Handle expiry date, URL, and vendor based on product type
            if (product instanceof PerishableProduct perishable) {
                data[i][4] = perishable.getExpiryDate().toString();  // Expiry Date
            } else {
                data[i][4] = "N/A";  // Expiry Date not applicable
            }

            if (product instanceof DigitalProduct digital) {
                data[i][5] = digital.getUrl();  // URL
                data[i][6] = digital.getVendorName();  // Vendor
            } else {
                data[i][5] = "N/A";  // URL not applicable
                data[i][6] = "N/A";  // Vendor not applicable
            }

            data[i][7] = "Action";
        }

        // Column names
        String[] columnNames = {"ID", "Nama Produk", "Jenis", "Harga", "Tanggal Expire", "URL", "Vendor", "Aksi"};

        // Create a table model and JTable
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        JTable produkTable = new JTable(tableModel);
        produkTable.setRowHeight(40);

        // Add Action buttons to the "Aksi" column (Edit, Delete)
        produkTable.getColumn("Aksi").setCellRenderer(new ButtonRendererProduk());
        produkTable.getColumn("Aksi").setCellEditor(new ButtonEditorProduk(new JCheckBox()));

        // Wrap the table in a JScrollPane
        JScrollPane tableScrollPane = new JScrollPane(produkTable);
        produkPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Create the "Create New Product" button below the table
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton createNewProductButton = new JButton("Create New Product");
        createNewProductButton.addActionListener(e -> {
            // Show the dialog for selecting the product type
            showProductTypeDialog();
        });
        buttonPanel.add(createNewProductButton);
        produkPanel.add(buttonPanel, BorderLayout.SOUTH);

        return produkPanel;
    }

    private void showProductTypeDialog() {
        // Create a new dialog for selecting the product type
        JDialog productTypeDialog = new JDialog((Frame) null, "Select Product Type", true);
        productTypeDialog.setSize(400, 200);  // Set dialog size
        productTypeDialog.setLocationRelativeTo(null);

        // Panel to hold the dropdown and buttons
        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));  // Stack components vertically

        // Dropdown for product type selection
        JLabel productTypeLabel = new JLabel("Select Product Type:");
        String[] productTypes = {"BundleProduct", "NonPerishable", "Perishable", "DigitalProduct"};
        JComboBox<String> productTypeComboBox = new JComboBox<>(productTypes);
        dialogPanel.add(productTypeLabel);
        dialogPanel.add(productTypeComboBox);

        // Panel for "Cancel" and "Next" buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // Cancel Button: closes the dialog
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> productTypeDialog.dispose());

        // Next Button: goes to the next dialog for product details input
        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(e -> {
            String selectedProductType = (String) productTypeComboBox.getSelectedItem();
            showProductDetailsDialog(selectedProductType);  // Pass selected product type to the next dialog
            productTypeDialog.dispose();  // Close the product type dialog
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(nextButton);

        dialogPanel.add(buttonPanel);

        // Add the dialog panel to the dialog window
        productTypeDialog.add(dialogPanel);
        productTypeDialog.setVisible(true);
    }

    private void showProductDetailsDialog(String productType) {
        // Create a dialog for entering product details
        JDialog productDetailsDialog = new JDialog((Frame) null, "Enter Product Details", true);
        productDetailsDialog.setSize(400, 300);
        productDetailsDialog.setLocationRelativeTo(null);

        // Panel to hold the input fields and buttons
        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));  // Stack components vertically

        // Label to show the selected product type
        dialogPanel.add(new JLabel("Product Type: " + productType));

        switch (productType) {
            case "BundleProduct":
                break;
            case "NonPerishable":
                // Input fields for product details
                productDetailsPanel = new JPanel();
                productDetailsPanel.setLayout(new GridLayout(4, 2));

                idLabel = new JLabel("ID");
                idTextField = new JTextField(10);
                productDetailsPanel.add(idLabel);
                productDetailsPanel.add(idTextField);

                nameLabel = new JLabel("Product Name:");
                nameField = new JTextField(15);
                productDetailsPanel.add(nameLabel);
                productDetailsPanel.add(nameField);

                priceLabel = new JLabel("Price:");
                priceField = new JTextField(10);
                productDetailsPanel.add(priceLabel);
                productDetailsPanel.add(priceField);

                dialogPanel.add(productDetailsPanel);

                // Panel for "Cancel" and "Save" buttons
                buttonPanel = new JPanel();
                buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

                // Cancel Button: closes the dialog
                cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(e -> productDetailsDialog.dispose());

                // Save Button: saves the product details (for now just show a message)
                saveButton = new JButton("Save");
                saveButton.addActionListener(e -> {
                    String productId = idTextField.getText();
                    String productName = nameField.getText();
                    double productPrice = Double.parseDouble(priceField.getText());

                    ProductService productService = new ProductService();
                    try {
                        productService.addProduct(new NonPerishableProduct(productId, productName, productPrice));
                    } catch (InvalidProductException ex) {
                        throw new RuntimeException(ex);
                    }
                    productDetailsDialog.dispose();
                });

                buttonPanel.add(cancelButton);
                buttonPanel.add(saveButton);

                dialogPanel.add(buttonPanel);

                // Add the dialog panel to the dialog window
                productDetailsDialog.add(dialogPanel);
                productDetailsDialog.setVisible(true);
                break;
            case "Perishable":
                // Input fields for product details
                productDetailsPanel = new JPanel();
                productDetailsPanel.setLayout(new GridLayout(4, 2));

                idLabel = new JLabel("ID");
                idTextField = new JTextField(10);
                productDetailsPanel.add(idLabel);
                productDetailsPanel.add(idTextField);

                nameLabel = new JLabel("Product Name:");
                nameField = new JTextField(15);
                productDetailsPanel.add(nameLabel);
                productDetailsPanel.add(nameField);

                priceLabel = new JLabel("Price:");
                priceField = new JTextField(10);
                productDetailsPanel.add(priceLabel);
                productDetailsPanel.add(priceField);

                // Create a MaskFormatter for date input (yyyy-MM-dd)
                MaskFormatter dateFormatter = null;
                try {
                    dateFormatter = new MaskFormatter("####-##-##"); // Format: yyyy-MM-dd
                    dateFormatter.setPlaceholderCharacter('_'); // Placeholder for missing characters
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                JLabel expiryDateLabel = new JLabel("Expiry Date:");
                JFormattedTextField dateField = new JFormattedTextField(dateFormatter);
                dateField.setColumns(10); // Set the size of the text field
                productDetailsPanel.add(expiryDateLabel);
                productDetailsPanel.add(dateField);

                dialogPanel.add(productDetailsPanel);

                // Panel for "Cancel" and "Save" buttons
                buttonPanel = new JPanel();
                buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

                // Cancel Button: closes the dialog
                cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(e -> productDetailsDialog.dispose());

                // Save Button: saves the product details (for now just show a message)
                saveButton = new JButton("Save");
                saveButton.addActionListener(e -> {
                    String productId = idTextField.getText();
                    String productName = nameField.getText();
                    double productPrice = Double.parseDouble(priceField.getText());
                    String dateString = dateField.getText();  // Get the date as a string
                    if (!dateString.isEmpty()) {
                        try {
                            // Convert the string to a LocalDate object using DateTimeFormatter
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            LocalDate expiryDate = LocalDate.parse(dateString, formatter);

                            ProductService productService = new ProductService();
                            try {
                                productService.addProduct(new PerishableProduct(productId, productName, productPrice, expiryDate));
                            } catch (InvalidProductException ex) {
                                throw new RuntimeException(ex);
                            }
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    productDetailsDialog.dispose();
                });

                buttonPanel.add(cancelButton);
                buttonPanel.add(saveButton);

                dialogPanel.add(buttonPanel);

                // Add the dialog panel to the dialog window
                productDetailsDialog.add(dialogPanel);
                productDetailsDialog.setVisible(true);
                break;
            case "DigitalProduct":
                productDetailsPanel = new JPanel();
                productDetailsPanel.setLayout(new GridLayout(5, 2));

                idLabel = new JLabel("ID");
                idTextField = new JTextField(10);
                productDetailsPanel.add(idLabel);
                productDetailsPanel.add(idTextField);

                nameLabel = new JLabel("Product Name:");
                nameField = new JTextField(15);
                productDetailsPanel.add(nameLabel);
                productDetailsPanel.add(nameField);

                priceLabel = new JLabel("Price:");
                priceField = new JTextField(10);
                productDetailsPanel.add(priceLabel);
                productDetailsPanel.add(priceField);

                JLabel urlLabel = new JLabel("URL:");
                JTextField urlField = new JTextField(15);
                productDetailsPanel.add(urlLabel);
                productDetailsPanel.add(urlField);

                JLabel vendorNameLabel = new JLabel("Vendor:");
                JTextField vendorNameField = new JTextField(10);
                productDetailsPanel.add(vendorNameLabel);
                productDetailsPanel.add(vendorNameField);

                dialogPanel.add(productDetailsPanel);

                // Panel for "Cancel" and "Save" buttons
                buttonPanel = new JPanel();
                buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

                // Cancel Button: closes the dialog
                cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(e -> productDetailsDialog.dispose());

                // Save Button: saves the product details (for now just show a message)
                saveButton = new JButton("Save");
                saveButton.addActionListener(e -> {
                    String productId = idTextField.getText();
                    String productName = nameField.getText();
                    double productPrice = Double.parseDouble(priceField.getText());
                    String urlString = urlField.getText();
                    String vendorName = vendorNameField.getText();

                    ProductService productService = new ProductService();
                    try {
                        productService.addProduct(new DigitalProduct(productId, productName, productPrice, urlString, vendorName));
                    } catch (InvalidProductException ex) {
                        throw new RuntimeException(ex);
                    }
                    productDetailsDialog.dispose();
                });

                buttonPanel.add(cancelButton);
                buttonPanel.add(saveButton);

                dialogPanel.add(buttonPanel);

                // Add the dialog panel to the dialog window
                productDetailsDialog.add(dialogPanel);
                productDetailsDialog.setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(null, "Invalid Product Type");
                break;
        }
    }

    static class ButtonRendererProduk extends JPanel implements TableCellRenderer {
        public ButtonRendererProduk() {
            setLayout(new FlowLayout(FlowLayout.LEFT));  // Align buttons horizontally
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            // Create buttons for the actions using text
            JButton editButton = new JButton("Edit");
            JButton deleteButton = new JButton("Delete");

            // Add buttons to the panel
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);

            return buttonPanel;
        }
    }

    public static class ButtonEditorProduk extends DefaultCellEditor {

        public ButtonEditorProduk(JCheckBox checkBox) {
            super(checkBox);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

            // Create action buttons with text labels
            JButton editButton = new JButton("Edit");
            JButton deleteButton = new JButton("Delete");

            // Add action listener for the Edit button
            editButton.addActionListener(e -> {
                // Get the type of the product from the current row
                String productType = (String) table.getValueAt(row, 2); // Assuming "Type" is in the 3rd column (index 2)

                // Open a dialog based on the product type
                switch (productType) {
                    case "PERISHABLE" -> openPerishableProductDialog(table, row);
                    case "DIGITAL_PRODUCT" -> openDigitalProductDialog(table, row);
                    case "BUNDLE_PRODUCT" -> openBundleProductDialog(table, row);
                    case null, default -> openNonPerishableProductDialog(table, row);
                }
            });

            // Add action listener for the Delete button
            deleteButton.addActionListener(e -> {
                // Only allow deletion of the first row
                if (row == 0) {
                    // Get the product ID from the selected row in the table
                    String productId = (String) table.getModel().getValueAt(row, 0);

                    // Remove the product from the table first
                    ((DefaultTableModel) table.getModel()).removeRow(row);

                    // Now, attempt to delete the product from the database
                    ProductDAO productDAO = new ProductDAO();

                    // Check if the product exists and is referenced in the database
                    if (Objects.equals(Objects.requireNonNull(ProductDAO.getProductById(productId)).getId(), productId)) {
                        try {
                            // First, remove references in transaction_items table
                            productDAO.deleteProductFromTransactionItems(productId);  // This method will delete related transaction items

                            // Then, delete the product from the products table
                            productDAO.deleteProduct(productId);  // Now delete the product
                            JOptionPane.showMessageDialog(null, "Product deleted successfully!");
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Failed to delete product: " + ex.getMessage());
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Product does not exist");
                    }
                } else {
                    JOptionPane.showMessageDialog(table, "You can only delete the first row.", "Delete Not Allowed", JOptionPane.WARNING_MESSAGE);
                }
            });


            // Add buttons to a panel
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);

            return buttonPanel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }

        // Open dialog for editing a Perishable product
        private void openPerishableProductDialog(JTable table, int row) {
            // Create the dialog panel for perishable product details
            JPanel productDetailsPanel = new JPanel();
            productDetailsPanel.setLayout(new GridLayout(4, 2));

            JLabel idLabel = new JLabel("ID");
            JTextField idTextField = new JTextField(10);
            idTextField.setText((String) table.getModel().getValueAt(row, 0));  // Pre-fill the ID field (disable editing)
            idTextField.setEditable(false);  // Set to non-editable because ID shouldn't be changed
            productDetailsPanel.add(idLabel);
            productDetailsPanel.add(idTextField);

            JLabel nameLabel = new JLabel("Product Name:");
            JTextField nameField = new JTextField(15);
            nameField.setText((String) table.getModel().getValueAt(row, 1));  // Pre-fill with existing product name
            productDetailsPanel.add(nameLabel);
            productDetailsPanel.add(nameField);

            JLabel priceLabel = new JLabel("Price:");
            JTextField priceField = new JTextField(10);
            priceField.setText(String.valueOf((String) table.getModel().getValueAt(row, 3)));  // Pre-fill with product price
            productDetailsPanel.add(priceLabel);
            productDetailsPanel.add(priceField);

            // Create a MaskFormatter for date input (yyyy-MM-dd)
            MaskFormatter dateFormatter = null;
            try {
                dateFormatter = new MaskFormatter("####-##-##"); // Format: yyyy-MM-dd
                dateFormatter.setPlaceholderCharacter('_'); // Placeholder for missing characters
            } catch (ParseException e) {
                e.printStackTrace();
            }

            JLabel expiryDateLabel = new JLabel("Expiry Date:");
            JFormattedTextField dateField = new JFormattedTextField(dateFormatter);
            dateField.setText((String) table.getModel().getValueAt(row, 4));  // Pre-fill expiry date if it's a perishable product
            dateField.setColumns(10); // Set the size of the text field
            productDetailsPanel.add(expiryDateLabel);
            productDetailsPanel.add(dateField);

            // Panel for "Cancel" and "Save" buttons
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

            // Cancel Button: closes the dialog
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> {
                // Close the dialog when cancel is clicked
                ((JDialog) SwingUtilities.getWindowAncestor(productDetailsPanel)).dispose();
            });

            // Save Button: saves the product details
            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(e -> {
                String productId = idTextField.getText();
                String productName = nameField.getText();
                double productPrice = 0;

                // Check if the price field is valid
                try {
                    productPrice = Double.parseDouble(priceField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid price entered. Please enter a valid number.");
                    return;
                }

                String dateString = dateField.getText();  // Get the date as a string
                if (!dateString.isEmpty()) {
                    try {
                        // Convert the string to a LocalDate object using DateTimeFormatter
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate expiryDate = LocalDate.parse(dateString, formatter);

                        // Now create and update the product
                        ProductService productService = new ProductService();
                        try {
                            // Update the existing product
                            PerishableProduct updatedProduct = new PerishableProduct(productId, productName, productPrice, expiryDate);
                            productService.updateProduct(updatedProduct);  // Update the product in the product service or database
                            JOptionPane.showMessageDialog(null, "Product updated successfully!");
                            // Optionally, dispose of the dialog if product is saved successfully
                            ((JDialog) SwingUtilities.getWindowAncestor(productDetailsPanel)).dispose();
                            refreshProductTable(table);
                        } catch (InvalidProductException ex) {
                            JOptionPane.showMessageDialog(null, "Failed to update product: " + ex.getMessage());
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Invalid expiry date format. Please use yyyy-MM-dd.");
                    }
                }
            });

            buttonPanel.add(cancelButton);
            buttonPanel.add(saveButton);

            // Adding the productDetailsPanel and buttonPanel to the dialog
            JDialog productDialog = new JDialog();
            productDialog.setTitle("Edit Perishable Product");
            productDialog.setSize(400, 300);
            productDialog.setLocationRelativeTo(null);  // Center the dialog on the screen
            productDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            productDialog.setLayout(new BorderLayout());
            productDialog.add(productDetailsPanel, BorderLayout.CENTER);
            productDialog.add(buttonPanel, BorderLayout.SOUTH);
            productDialog.setVisible(true);
        }

        // Open dialog for editing a Digital product
        private void openDigitalProductDialog(JTable table, int row) {
            // Create the dialog panel for digital product details
            JPanel productDetailsPanel = new JPanel();
            productDetailsPanel.setLayout(new GridLayout(5, 2)); // Adjusted for 5 fields (ID, Name, Price, URL, Vendor Name)

            JLabel idLabel = new JLabel("ID");
            JTextField idTextField = new JTextField(10);
            idTextField.setText((String) table.getModel().getValueAt(row, 0));  // Pre-fill the ID field (non-editable)
            idTextField.setEditable(false);  // Set to non-editable because ID shouldn't be changed
            productDetailsPanel.add(idLabel);
            productDetailsPanel.add(idTextField);

            JLabel nameLabel = new JLabel("Product Name:");
            JTextField nameField = new JTextField(15);
            nameField.setText((String) table.getModel().getValueAt(row, 1));  // Pre-fill with existing product name
            productDetailsPanel.add(nameLabel);
            productDetailsPanel.add(nameField);

            JLabel priceLabel = new JLabel("Price:");
            JTextField priceField = new JTextField(10);
            priceField.setText(String.valueOf((String) table.getModel().getValueAt(row, 3)));  // Pre-fill with existing product price
            productDetailsPanel.add(priceLabel);
            productDetailsPanel.add(priceField);

            JLabel urlLabel = new JLabel("URL:");
            JTextField urlField = new JTextField(15);
            urlField.setText(((String) table.getModel().getValueAt(row, 5))); // Pre-fill with existing product URL
            productDetailsPanel.add(urlLabel);
            productDetailsPanel.add(urlField);

            JLabel vendorNameLabel = new JLabel("Vendor:");
            JTextField vendorNameField = new JTextField(10);
            vendorNameField.setText(((String) table.getModel().getValueAt(row, 6))); // Pre-fill with existing vendor name
            productDetailsPanel.add(vendorNameLabel);
            productDetailsPanel.add(vendorNameField);

            // Panel for "Cancel" and "Save" buttons
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

            // Cancel Button: closes the dialog
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> {
                // Close the dialog when cancel is clicked
                ((JDialog) SwingUtilities.getWindowAncestor(productDetailsPanel)).dispose();
            });

            // Save Button: saves the product details
            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(e -> {
                String productId = idTextField.getText();
                String productName = nameField.getText();
                double productPrice = 0;

                // Check if the price field is valid
                try {
                    productPrice = Double.parseDouble(priceField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid price entered. Please enter a valid number.");
                    return;
                }

                String urlString = urlField.getText();
                String vendorName = vendorNameField.getText();

                // Now create and update the product
                ProductService productService = new ProductService();
                try {
                    // Update the existing product
                    DigitalProduct updatedProduct = new DigitalProduct(productId, productName, productPrice, urlString, vendorName);
                    productService.updateProduct(updatedProduct);  // Update the product in the product service or database
                    JOptionPane.showMessageDialog(null, "Product updated successfully!");
                    // Optionally, dispose of the dialog if product is saved successfully
                    ((JDialog) SwingUtilities.getWindowAncestor(productDetailsPanel)).dispose();
                    refreshProductTable(table);
                } catch (InvalidProductException ex) {
                    JOptionPane.showMessageDialog(null, "Failed to update product: " + ex.getMessage());
                }
            });

            buttonPanel.add(cancelButton);
            buttonPanel.add(saveButton);

            // Adding the productDetailsPanel and buttonPanel to the dialog
            JDialog productDialog = new JDialog();
            productDialog.setTitle("Edit Digital Product");
            productDialog.setSize(400, 300);
            productDialog.setLocationRelativeTo(null);  // Center the dialog on the screen
            productDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            productDialog.setLayout(new BorderLayout());
            productDialog.add(productDetailsPanel, BorderLayout.CENTER);
            productDialog.add(buttonPanel, BorderLayout.SOUTH);
            productDialog.setVisible(true);
        }

        // Open dialog for editing a Bundle product
        private void openBundleProductDialog(JTable table, int row) {
            // You can create a dialog to edit the product properties here
            JOptionPane.showMessageDialog(null, "Edit Bundle Product at row " + row);
        }

        // Open dialog for editing a NonPerishable product
        private void openNonPerishableProductDialog(JTable table, int row) {
            // Create the dialog panel for non-perishable product details
            JPanel productDetailsPanel = new JPanel();
            productDetailsPanel.setLayout(new GridLayout(3, 2)); // Adjusted for 3 fields (ID, Name, Price)

            JLabel idLabel = new JLabel("ID");
            JTextField idTextField = new JTextField(10);
            idTextField.setText((String) table.getModel().getValueAt(row, 0));  // Pre-fill the ID field (non-editable)
            idTextField.setEditable(false);  // Set to non-editable because ID shouldn't be changed
            productDetailsPanel.add(idLabel);
            productDetailsPanel.add(idTextField);

            JLabel nameLabel = new JLabel("Product Name:");
            JTextField nameField = new JTextField(15);
            nameField.setText((String) table.getModel().getValueAt(row, 1));  // Pre-fill with existing product name
            productDetailsPanel.add(nameLabel);
            productDetailsPanel.add(nameField);

            JLabel priceLabel = new JLabel("Price:");
            JTextField priceField = new JTextField(10);
            priceField.setText((String) table.getModel().getValueAt(row, 3));  // Pre-fill with existing product price
            productDetailsPanel.add(priceLabel);
            productDetailsPanel.add(priceField);

            // Panel for "Cancel" and "Save" buttons
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

            // Cancel Button: closes the dialog
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> {
                // Close the dialog when cancel is clicked
                ((JDialog) SwingUtilities.getWindowAncestor(productDetailsPanel)).dispose();
            });

            // Save Button: saves the product details
            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(e -> {
                String productId = idTextField.getText();
                String productName = nameField.getText();
                double productPrice = 0;

                // Check if the price field is valid
                try {
                    productPrice = Double.parseDouble(priceField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid price entered. Please enter a valid number.");
                    return;
                }

                // Now create and update the product
                ProductService productService = new ProductService();
                try {
                    // Update the existing product
                    NonPerishableProduct updatedProduct = new NonPerishableProduct(productId, productName, productPrice);
                    productService.updateProduct(updatedProduct);  // Update the product in the product service or database
                    JOptionPane.showMessageDialog(null, "Product updated successfully!");
                    // Optionally, dispose of the dialog if product is saved successfully
                    ((JDialog) SwingUtilities.getWindowAncestor(productDetailsPanel)).dispose();
                    refreshProductTable(table);
                } catch (InvalidProductException ex) {
                    JOptionPane.showMessageDialog(null, "Failed to update product: " + ex.getMessage());
                }
            });

            buttonPanel.add(cancelButton);
            buttonPanel.add(saveButton);

            // Adding the productDetailsPanel and buttonPanel to the dialog
            JDialog productDialog = new JDialog();
            productDialog.setTitle("Edit Non-Perishable Product");
            productDialog.setSize(400, 300);
            productDialog.setLocationRelativeTo(null);  // Center the dialog on the screen
            productDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            productDialog.setLayout(new BorderLayout());
            productDialog.add(productDetailsPanel, BorderLayout.CENTER);
            productDialog.add(buttonPanel, BorderLayout.SOUTH);
            productDialog.setVisible(true);
        }
    }

    // Create a sample "Akun" panel
    private JPanel createAkunPanel() {
        // Create the main panel for Akun
        JPanel akunPanel = new JPanel(new BorderLayout());
        akunPanel.setBackground(Color.WHITE);
        akunPanel.setLayout(new BorderLayout());

        // Title Label for Akun
        JLabel titleLabel = new JLabel("Akun", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        akunPanel.add(titleLabel, BorderLayout.NORTH);

        // Table setup
        String[] columnNames = {"Username", "Role", "Aksi"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Only action column is editable
            }
        };

        // Ambil data dari database
        java.util.List<User> userList = new UserDAO().getAllUsers();
        for (User user : userList) {
            model.addRow(new Object[]{user.getUsername(), user.getRole(), ""});
        }

        JTable table = new JTable(model);
        table.setRowHeight(40); // Tinggi baris biar keliatan lebih bagus
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        table.getColumn("Aksi").setCellRenderer(new ButtonRenderer());
        table.getColumn("Aksi").setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(table);
        akunPanel.add(scrollPane, BorderLayout.CENTER);
        return akunPanel;
    }

    static class ButtonRenderer extends JPanel implements TableCellRenderer {
        public ButtonRenderer() {
            setLayout(new GridBagLayout());
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            removeAll();
            JButton editButton = new JButton("Edit");
            JButton deleteButton = new JButton("Delete");

            editButton.setPreferredSize(new Dimension(80, 30));
            deleteButton.setPreferredSize(new Dimension(80, 30));

            editButton.setFont(new Font("Arial", Font.PLAIN, 12));
            deleteButton.setFont(new Font("Arial", Font.PLAIN, 12));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 5, 0, 5);
            gbc.gridx = 0;
            add(editButton, gbc);
            gbc.gridx = 1;
            add(deleteButton, gbc);

            return this;
        }
    }

    static class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton editButton, deleteButton;
        private JTable table;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new GridBagLayout());

            editButton = new JButton("Edit");
            deleteButton = new JButton("Delete");

            editButton.setPreferredSize(new Dimension(80, 30));
            deleteButton.setPreferredSize(new Dimension(80, 30));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 5, 0, 5);
            gbc.gridx = 0;
            panel.add(editButton, gbc);
            gbc.gridx = 1;
            panel.add(deleteButton, gbc);

            // Menangani klik tombol Edit
            editButton.addActionListener(e -> {
                int row = table.getSelectedRow();
                String username = table.getValueAt(row, 0).toString();
                String role = table.getValueAt(row, 1).toString();

                // Menampilkan form edit username dan role
                JTextField usernameField = new JTextField(username);

                // Dropdown (JComboBox) untuk role
                String[] roles = {"User", "Admin"};
                JComboBox<String> roleComboBox = new JComboBox<>(roles);
                roleComboBox.setSelectedItem(role);  // Menetapkan role yang ada sebagai pilihan yang terpilih

                // Menyusun form input
                Object[] message = {
                        "Username:", usernameField,
                        "Role:", roleComboBox
                };

                int option = JOptionPane.showConfirmDialog(panel, message, "Edit Akun", JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    // Ambil data baru dari input form
                    String newUsername = usernameField.getText();
                    String newRole = (String) roleComboBox.getSelectedItem(); // Ambil nilai yang dipilih dari combo box

                    // Update database
                    boolean success = new UserDAO().updateUser(username, newUsername, newRole);

                    if (success) {
                        // Update model
                        table.setValueAt(newUsername, row, 0);
                        table.setValueAt(newRole, row, 1);
                        JOptionPane.showMessageDialog(panel, "Akun berhasil diperbarui.");
                    } else {
                        JOptionPane.showMessageDialog(panel, "Gagal memperbarui akun.");
                    }
                }

                fireEditingStopped();
            });

            // Menangani klik tombol Delete
            deleteButton.addActionListener(e -> {
                int result = JOptionPane.showConfirmDialog(panel, "Yakin hapus akun?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    int row = table.getSelectedRow();
                    String username = table.getValueAt(row, 0).toString();

                    // Hapus dari database
                    boolean success = new UserDAO().deleteUser(username);

                    if (success) {
                        // Hapus dari model
                        ((DefaultTableModel) table.getModel()).removeRow(row);
                        JOptionPane.showMessageDialog(panel, "Akun berhasil dihapus.");
                    } else {
                        JOptionPane.showMessageDialog(panel, "Gagal menghapus akun.");
                    }
                }
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.table = table; // Set reference to table
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    // Create a sample "Log" panel
    private JPanel createLogPanel() {
        // Create the main panel for Log
        JPanel logPanel = new JPanel();
        logPanel.setBackground(Color.WHITE);
        logPanel.setLayout(new BorderLayout());

        // Title Label for Log
        JLabel titleLabel = new JLabel("Log", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        logPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());

        JTable logTable = createLogTable();
        JScrollPane tableScrollPane = new JScrollPane(logTable);  // Add table inside a JScrollPane
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);  // Add table to col1Panel
        logPanel.add(centerPanel, BorderLayout.CENTER);

        return logPanel;
    }

    private JTable createLogTable() {
        LogDAO logDAO = new LogDAO();
        List<Log> logs = logDAO.getAllLogs();

        // Create a 2D array to store data for the table
        String[][] data = new String[logs.size()][4];  // 4 columns: Timestamp, Type, Log Description, Aksi

        // Populate the data array with values from the logs list
        for (int i = 0; i < logs.size(); i++) {
            Log l = logs.get(i);

            // Convert the timestamp to a formatted string (for display)
            String formattedTimestamp = l.getTimestamp().toString();  // Assuming the timestamp is in LocalDateTime format

            // Populate the data array with the log's fields
            data[i][0] = formattedTimestamp;  // Timestamp
            data[i][1] = l.getType();         // Type
            data[i][2] = l.getDescription();  // Description

            // Add "Refund" button if the Type is "Transaksi"
            if ("TRANSACTION".equals(l.getType())) {
                data[i][3] = "Refund";  // Button text for refund action
            } else {
                data[i][3] = "";  // Empty for other types
            }
        }

        // Column names for the JTable
        String[] columnNames = {"Timestamp", "Type", "Log Description", "Aksi"};

        // Create a table model and JTable using the populated data and column names
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);

        // Create a JTable and bind the table model to it
        JTable logTable = new JTable(tableModel);
        logTable.setRowHeight(40);  // Adjust the row height if needed

        // Set custom renderer and editor for the "Aksi" column (where Refund button will appear)
        logTable.getColumn("Aksi").setCellRenderer(new ButtonRendererLog());
        logTable.getColumn("Aksi").setCellEditor(new ButtonEditorLog(new JCheckBox()));

        // Return the populated JTable
        return logTable;
    }

    static class ButtonRendererLog extends JButton implements TableCellRenderer {

        public ButtonRendererLog() {
            setLayout(new FlowLayout(FlowLayout.LEFT));  // Align buttons horizontally
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JButton refundButton = new JButton("Refund");

            JPanel refundPanel = new JPanel();
            refundPanel.add(refundButton);

            return refundPanel;
        }
    }

    static class ButtonEditorLog extends DefaultCellEditor {
        private String label;

        public ButtonEditorLog(JCheckBox checkBox) {
            super(checkBox);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            JButton refundButton = new JButton("Refund");
            refundButton.addActionListener(e -> {
                String desc = table.getModel().getValueAt(row, 2).toString();

                String regex = "(TX-\\d+).*(\\d+\\.\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(desc);

                if (matcher.find()) {
                    String transactionId = matcher.group(1);

                    TransactionService transactionService = new TransactionService();

                    try {
                        transactionService.processRefund(transactionId, currentUser.getUsername());
                    } catch (InvalidTransactionException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    System.out.println("No match found");
                }
            });

            JPanel refundPanel = new JPanel();
            refundPanel.add(refundButton);
            return refundPanel;
        }

        @Override
        public Object getCellEditorValue() {
            return label;
        }
    }

    // ActionListener for sidebar buttons
    private class SidebarButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            // Reset all button colors to default
            resetButtonColors();

            // Switch between the panels based on button clicked
            switch (command) {
                case "Transaksi":
                    cardLayout.show(mainPanel, "Transaksi");
                    highlightButton("Transaksi");
                    break;
                case "Produk":
                    cardLayout.show(mainPanel, "Produk");
                    highlightButton("Produk");
                    break;
                case "Akun":
                    cardLayout.show(mainPanel, "Akun");
                    highlightButton("Akun");
                    break;
                case "Log":
                    cardLayout.show(mainPanel, "Log");
                    highlightButton("Log");
                    break;
            }
        }
    }

    // Method to reset button colors (to default)
    private void resetButtonColors() {
        for (Component comp : sidebarPanel.getComponents()) {
            if (comp instanceof JButton button) {
                button.setBackground(Color.WHITE);  // Set to default white color
                button.setForeground(Color.BLACK);  // Optional: Change text color to white for better visibility
            }
        }
    }

    // Highlight the active button in the sidebar (blue for active)
    private void highlightButton(String panelName) {
        for (Component comp : sidebarPanel.getComponents()) {
            if (comp instanceof JButton button) {
                if (button.getText().equals(panelName)) {
                    button.setBackground(new Color(54, 137, 209));  // Blue for active button
                    button.setForeground(Color.WHITE);  // Optional: Change text color to white for better visibility
                }
            }
        }
    }

    public static void refreshProductTable(JTable productTable) {
        DefaultTableModel tableModel = (DefaultTableModel) productTable.getModel();
        tableModel.setRowCount(0);  // Clear existing rows

        // Fetch the updated list of products from the database
        List<Product> products = ProductDAO.getAllProducts();

        // Add the updated rows to the table
        for (Product product : products) {
            String[] row = new String[5];
            row[0] = product.getId();
            row[1] = product.getName();
            row[2] = String.valueOf(product.getPrice());
            row[3] = product.getProductType();
            // Add other columns as needed
            tableModel.addRow(row);
        }
    }

    // Method to return to the login screen
    private void openLoginScreen() {
        MainApp mainApp = new MainApp();
        mainApp.setVisible(true);  // Show the login screen
        mainApp.setSize(400, 300); // Set the size of your application window
        mainApp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}