
package restockalertsystem3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginPage extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;
    
    // Dummy credentials for demo
    private final String VALID_EMAIL = "admin@inventory.com";
    private final String VALID_PASSWORD = "admin123";
    
    public LoginPage() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
        setVisible(true);
    }
    
    private void initializeComponents() {
        setTitle("Inventory Management System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Set background color
        getContentPane().setBackground(new Color(240, 248, 255));
        
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        statusLabel = new JLabel("", SwingConstants.CENTER);
        
        // Style components
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(Color.RED);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(240, 248, 255));
        JLabel titleLabel = new JLabel("Inventory Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // Login Panel
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 0;
        loginPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        loginPanel.add(emailField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        loginPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        loginPanel.add(passwordField, gbc);
        
        // Login Button
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginPanel.add(loginButton, gbc);
        
        // Status Label
        gbc.gridy = 3;
        loginPanel.add(statusLabel, gbc);
        
        add(loginPanel, BorderLayout.CENTER);
        
        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(240, 248, 255));
        JLabel infoLabel = new JLabel("<html><center>Demo Credentials:<br>Email: admin@inventory.com<br>Password: admin123</center></html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoLabel.setForeground(Color.GRAY);
        infoPanel.add(infoLabel);
        add(infoPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        loginButton.addActionListener(e -> performLogin());
        
        // Allow Enter key to login
        KeyListener enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        
        emailField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);
    }
    
    private void performLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please fill in all fields");
            return;
        }
        
        if (email.equals(VALID_EMAIL) && password.equals(VALID_PASSWORD)) {
            statusLabel.setText("Login successful! Opening main system...");
            statusLabel.setForeground(Color.GREEN);
            
            // Close login window and open main frame
            SwingUtilities.invokeLater(() -> {
                dispose();
                new MainFrame();
            });
        } else {
            statusLabel.setText("Invalid email or password!");
            statusLabel.setForeground(Color.RED);
            passwordField.setText("");
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage());
    }
}