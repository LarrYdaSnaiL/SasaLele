package org.example.sasalele_pos;

import org.example.sasalele_pos.model.User;
import org.example.sasalele_pos.services.AuthService;
import org.example.sasalele_pos.services.ProductService;
import org.example.sasalele_pos.services.TransactionService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainApp extends JFrame {

    private static AuthService authService;
    private static ProductService productService;
    private static TransactionService transactionService;
    private static User currentUser;

    public MainApp() {
        setTitle("Sasa - Lele");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Create panel with FlowLayout to center components
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(34, 40, 49));

        // Add title label
        JLabel titleLabel = new JLabel("Sasa - Lele POS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));

        // Create the buttons
        JButton loginButton = createButton("Login");
        JButton signupButton = createButton("Signup");

        // Center the buttons using BoxLayout
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(34, 40, 49));
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);

        panel.add(buttonPanel);

        add(panel);

        // Add action listeners for buttons
        loginButton.addActionListener(e -> showLoginDialog());
        signupButton.addActionListener(e -> showSignupDialog());
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(new Color(68, 165, 254));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(300, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(54, 137, 209));
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(68, 165, 254));
            }
        });
        return button;
    }

    private void showLoginDialog() {
        JDialog loginDialog = new JDialog(this, "Login", true);
        loginDialog.setSize(300, 200);
        loginDialog.setLocationRelativeTo(this);
        loginDialog.setLayout(new GridLayout(3, 2));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        JButton loginSubmitButton = new JButton("Login");

        // Action listener for the login button
        loginSubmitButton.addActionListener(e -> {
            // Capture the username and password entered by the user
            String username = userField.getText();
            String password = new String(passField.getPassword());

            // Simulate the authentication process using the provided logic
            boolean isAuthenticated = authService.loginUser(username, password);

            if (isAuthenticated) {
                currentUser = authService.getCurrentUser(); // ✅ Sekarang method ini tersedia
                JOptionPane.showMessageDialog(this, "Login berhasil!");
                loginDialog.dispose(); // Close the dialog upon successful login
                openDashboard(); // Open the dashboard after successful login
            } else {
                JOptionPane.showMessageDialog(this, "Username/password salah!");
            }
        });

        loginDialog.add(userLabel);
        loginDialog.add(userField);
        loginDialog.add(passLabel);
        loginDialog.add(passField);
        loginDialog.add(new JLabel());
        loginDialog.add(loginSubmitButton);

        loginDialog.setVisible(true);
    }

    // Show signup dialog
    private void showSignupDialog() {
        JDialog signupDialog = new JDialog(this, "Signup", true);
        signupDialog.setSize(300, 250);
        signupDialog.setLocationRelativeTo(this);
        signupDialog.setLayout(new GridLayout(5, 2));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        // Show Password checkbox
        JCheckBox showPasswordCheckBox = new JCheckBox("Show Password");

        JLabel roleLabel = new JLabel("Role:");
        // Dropdown (JComboBox) for role selection
        String[] roles = {"User", "Admin"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);

        // Signup submit button
        JButton signupSubmitButton = new JButton("Signup");

        // Action listener for the signup button
        signupSubmitButton.addActionListener(e -> {
            // Capture the username and password entered by the user
            String username = userField.getText();
            String password = new String(passField.getPassword());
            String role = (String) roleComboBox.getSelectedItem(); // Get the selected role

            // Validate the input (e.g., check if password is empty)
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            // Check if the username already exists
            boolean userExists = authService.userExists(username);
            if (userExists) {
                JOptionPane.showMessageDialog(this, "Username is already taken!");
                return;
            }

            // Call the registerUser function to register the user
            boolean isRegistered = authService.registerUser(username, password, role);

            if (isRegistered) {
                JOptionPane.showMessageDialog(this, "Signup successful!");
                signupDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error during signup!");
            }
        });

        // Action listener for the Show Password checkbox
        showPasswordCheckBox.addActionListener(e -> {
            if (showPasswordCheckBox.isSelected()) {
                passField.setEchoChar((char) 0); // Show password
            } else {
                passField.setEchoChar('*'); // Hide password
            }
        });

        // Add components to the dialog
        signupDialog.add(userLabel);
        signupDialog.add(userField);
        signupDialog.add(passLabel);
        signupDialog.add(passField);
        signupDialog.add(new JLabel());
        signupDialog.add(showPasswordCheckBox);
        signupDialog.add(roleLabel);
        signupDialog.add(roleComboBox);
        signupDialog.add(new JLabel());
        signupDialog.add(signupSubmitButton);

        signupDialog.setVisible(true);
    }

    // Open the Dashboard after successful login
    private void openDashboard() {
        DashboardApp dashboard = new DashboardApp(currentUser);
        setContentPane(dashboard);
        revalidate();
        repaint();

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
    }

    public static void main(String[] args) {
        authService = new AuthService();
        productService = new ProductService();
        transactionService = new TransactionService();

        SwingUtilities.invokeLater(() -> {
            MainApp app = new MainApp();
            app.setVisible(true);
        });
    }
}