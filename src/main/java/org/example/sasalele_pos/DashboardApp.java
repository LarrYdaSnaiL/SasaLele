package org.example.sasalele_pos;

import org.example.sasalele_pos.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DashboardApp extends JPanel {

    private JPanel sidebarPanel;
    private JPanel mainPanel;
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

        JPanel col1Layout = new JPanel();
        col1Layout.setBackground(Color.LIGHT_GRAY);
        col1Layout.add(new JLabel("Column 1 Content"));

        JPanel col2Layout = new JPanel();
        col2Layout.setBackground(Color.LIGHT_GRAY);
        col2Layout.add(new JLabel("Column 2 Content"));

        centerLayout.add(col1Layout);
        centerLayout.add(col2Layout);
        transaksiPanel.add(centerLayout, BorderLayout.CENTER);

        return transaksiPanel;
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
            switch (command) {
                case "Transaksi":
                    cardLayout.show(mainPanel, "Transaksi");
                    break;
                case "Produk":
                    cardLayout.show(mainPanel, "Produk");
                    break;
                case "Akun":
                    cardLayout.show(mainPanel, "Akun");
                    break;
                case "Log":
                    cardLayout.show(mainPanel, "Log");
                    break;
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