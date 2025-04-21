package org.example.sasalele_pos;

import org.example.sasalele_pos.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DashboardApp extends JPanel {

    private final JPanel sidebarPanel;
    private final JPanel mainPanel;
    private CardLayout cardLayout;  // To switch between different content in the main panel

    public DashboardApp(User currentUser) {
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
        col1Panel.setBackground(Color.LIGHT_GRAY);

        // Create JTable for the Products data
        JTable col1Table = createProductTable();
        JScrollPane tableScrollPane = new JScrollPane(col1Table);  // Add table inside a JScrollPane
        col1Panel.add(tableScrollPane, BorderLayout.CENTER);  // Add table to col1Panel

        // Add col1Panel to the centerLayout
        centerLayout.add(col1Panel);

        // Create col2Panel, which will be split into two parts (3:2 ratio)
        JPanel col2Panel = new JPanel();
        GridLayout col2Grid = new GridLayout(2, 1);
        col2Panel.setLayout(col2Grid);
        col2Grid.setVgap(10);

        // First row (3 parts) of col2Panel
        JPanel col2Top = new JPanel();
        GridLayout col2TopGrid = new GridLayout(2, 1);
        col2Top.setLayout(col2TopGrid);
        col2TopGrid.setVgap(10);

        JPanel col2TopRow1 = new JPanel();
        col2Top.add(col2TopRow1);

        GridLayout fieldRow1 = new GridLayout(1, 3);
        col2TopRow1.setLayout(fieldRow1);
        fieldRow1.setVgap(10);  // Vertical gap between components

        JPanel idPanel = new JPanel();
        idPanel.setBackground(Color.LIGHT_GRAY);
        JLabel idLabel = new JLabel("ID:");
        JTextField idField = new JTextField(10);  // Text field for ID input
        idPanel.add(idLabel);
        idPanel.add(idField);

        JPanel qtyPanel = new JPanel();
        qtyPanel.setBackground(Color.LIGHT_GRAY);
        JLabel qtyLabel = new JLabel("Quantity:");
        JTextField qtyField = new JTextField(10);  // Text field for Quantity input
        qtyPanel.add(qtyLabel);
        qtyPanel.add(qtyField);

        JPanel submitPanel = new JPanel();
        submitPanel.setBackground(Color.LIGHT_GRAY);
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            String id = idField.getText();
            String quantity = qtyField.getText();

            System.out.println("ID: " + id + ", Quantity: " + quantity);
        });
        submitPanel.add(submitButton);

        col2TopRow1.add(idPanel);
        col2TopRow1.add(qtyPanel);
        col2TopRow1.add(submitPanel);

        JPanel col2TopRow2 = new JPanel();
        col2Top.add(col2TopRow2);
        col2TopRow2.setLayout(new BorderLayout());  // Use BorderLayout to organize the table
        col2TopRow2.setBackground(Color.LIGHT_GRAY);

        // Create the table with data
        String[][] data = {
                {"1", "Product 1", "10", "100.0", "Action"},
                {"2", "Product 2", "5", "50.0", "Action"},
                {"3", "Product 3", "20", "200.0", "Action"}
        };
        String[] columnNames = {"ID", "Nama Produk", "Qty", "Harga", "Aksi"};

        // Create a table model and JTable
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(tableModel);
        table.setRowHeight(40);

        // Add Action buttons to the "Aksi" column
        table.getColumn("Aksi").setCellRenderer(new ButtonRenderer());
        table.getColumn("Aksi").setCellEditor(new ButtonEditor(new JCheckBox()));

        // Wrap the table in a JScrollPane
        JScrollPane col2Row1TableScrollPane = new JScrollPane(table);

        // Add the table to col2Top
        col2TopRow2.add(col2Row1TableScrollPane, BorderLayout.CENTER);

        JPanel col2Bottom = new JPanel();
        col2Bottom.setBackground(Color.LIGHT_GRAY);
        col2Bottom.setLayout(new BoxLayout(col2Bottom, BoxLayout.Y_AXIS));  // Stack components vertically
        col2Bottom.setBorder(new EmptyBorder(3, 3, 3, 3));
        JPanel totalPanel = new JPanel();
        totalPanel.setBackground(Color.LIGHT_GRAY);
        totalPanel.setLayout(new FlowLayout(FlowLayout.LEFT));  // Align to left
        JLabel totalLabel = new JLabel("Harga Total: Rp.");
        JLabel totalAmount = new JLabel("0");
        totalPanel.add(totalLabel);
        totalPanel.add(totalAmount);
        col2Bottom.add(totalPanel);

        JPanel uangPanel = new JPanel();
        uangPanel.setBackground(Color.LIGHT_GRAY);
        uangPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel uangLabel = new JLabel("Uang Pembeli: Rp.");
        JTextField uangField = new JTextField(10);
        uangPanel.add(uangLabel);
        uangPanel.add(uangField);
        col2Bottom.add(uangPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.LIGHT_GRAY);
        JButton lanjutkanButton = new JButton("Lanjutkan Transaksi");
        lanjutkanButton.addActionListener(e -> {
            // Perform the logic for continuing the transaction
            String uangPembeli = uangField.getText();
            System.out.println("Transaksi Lanjutkan dengan Uang Pembeli: " + uangPembeli);
        });
        buttonPanel.add(lanjutkanButton);
        col2Bottom.add(buttonPanel);

        // Add both rows to col2Panel
        col2Panel.add(col2Top);
        col2Panel.add(col2Bottom);

        // Add col2Panel to centerLayout
        centerLayout.add(col2Panel);

        transaksiPanel.add(centerLayout, BorderLayout.CENTER);

        return transaksiPanel;
    }

    // ButtonRenderer for the Action Buttons with plain text
    static class ButtonRenderer extends JPanel implements TableCellRenderer {
        public ButtonRenderer() {
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

    static class ButtonEditor extends DefaultCellEditor {
        private String label;
        private JTable table;

        public ButtonEditor(JCheckBox checkBox) {
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

    // Create the JTable for Products
    private JTable createProductTable() {
        // Sample data, you can modify this to fetch data from the database
        String[][] data = {
                {"1", "Product 1", "Electronics", "10.0"},
                {"2", "Product 2", "Furniture", "20.5"},
                {"3", "Product 3", "Clothing", "15.75"}
        };
        String[] columnNames = {"ID", "Nama Produk", "Jenis", "Harga"};

        // Create a table model and JTable
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        return new JTable(tableModel);
    }

    // Create a sample "Produk" panel
    private JPanel createProdukPanel() {
        JPanel produkPanel = new JPanel();
        produkPanel.setBackground(Color.LIGHT_GRAY);
        produkPanel.add(new JLabel("Produk Content"));
        return produkPanel;
    }

    // Create a sample "Akun" panel
    private JPanel createAkunPanel() {
        JPanel akunPanel = new JPanel();
        akunPanel.setBackground(Color.YELLOW);
        akunPanel.add(new JLabel("Akun Content"));
        return akunPanel;
    }

    // Create a sample "Log" panel
    private JPanel createLogPanel() {
        JPanel logPanel = new JPanel();
        logPanel.setBackground(Color.PINK);
        logPanel.add(new JLabel("Log Content"));
        return logPanel;
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

    // Method to return to the login screen
    private void openLoginScreen() {
        MainApp mainApp = new MainApp();
        mainApp.setVisible(true);  // Show the login screen
        mainApp.setSize(400, 300); // Set the size of your application window
        mainApp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}