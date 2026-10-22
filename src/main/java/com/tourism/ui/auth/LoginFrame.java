package com.tourism.ui.auth;

import com.tourism.config.ThemeConstants;
import com.tourism.model.User;
import com.tourism.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Modern Swing Login Frame with FlatLaf styling.
 * Handles customer and administrator authentication.
 */
public class LoginFrame extends JFrame {

    private final AuthService authService;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheck;
    private JButton loginButton;
    private JButton registerButton;

    public LoginFrame() {
        this.authService = new AuthService();
        initComponents();
    }

    private void initComponents() {
        setTitle("Tourism Planner - Sign In");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(ThemeConstants.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(35, 40, 35, 40));

        // --- Header Section ---
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        headerPanel.setOpaque(false);

        JLabel brandLabel = new JLabel("Wanderlust Planner", SwingConstants.CENTER);
        brandLabel.setFont(ThemeConstants.FONT_HEADER_LARGE);
        brandLabel.setForeground(ThemeConstants.PRIMARY_COLOR);

        JLabel subtitleLabel = new JLabel("Sign in to manage and plan your trips", SwingConstants.CENTER);
        subtitleLabel.setFont(ThemeConstants.FONT_BODY);
        subtitleLabel.setForeground(ThemeConstants.TEXT_MUTED);

        headerPanel.add(brandLabel);
        headerPanel.add(subtitleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- Card Form Section ---
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(ThemeConstants.CARD_BG);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 235), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.gridx = 0;

        // Email Label & Field
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(ThemeConstants.FONT_BOLD);
        emailLabel.setForeground(ThemeConstants.TEXT_PRIMARY);
        gbc.gridy = 0;
        formCard.add(emailLabel, gbc);

        emailField = new JTextField(20);
        emailField.setFont(ThemeConstants.FONT_BODY);
        emailField.putClientProperty("JTextField.placeholderText", "e.g. john@gmail.com");
        gbc.gridy = 1;
        formCard.add(emailField, gbc);

        // Password Label & Field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(ThemeConstants.FONT_BOLD);
        passwordLabel.setForeground(ThemeConstants.TEXT_PRIMARY);
        gbc.gridy = 2;
        formCard.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(ThemeConstants.FONT_BODY);
        passwordField.putClientProperty("JTextField.placeholderText", "Enter your password");
        gbc.gridy = 3;
        formCard.add(passwordField, gbc);

        // Show Password Checkbox
        showPasswordCheck = new JCheckBox("Show Password");
        showPasswordCheck.setFont(ThemeConstants.FONT_SMALL);
        showPasswordCheck.setOpaque(false);
        showPasswordCheck.addActionListener(e -> {
            if (showPasswordCheck.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });
        gbc.gridy = 4;
        formCard.add(showPasswordCheck, gbc);

        // Sign In Button
        loginButton = new JButton("Sign In");
        loginButton.setFont(ThemeConstants.FONT_BOLD);
        loginButton.setBackground(ThemeConstants.PRIMARY_COLOR);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setPreferredSize(new Dimension(150, 40));
        loginButton.addActionListener(e -> handleLogin());
        gbc.gridy = 5;
        gbc.insets = new Insets(15, 4, 6, 4);
        formCard.add(loginButton, gbc);

        // Register Link Button
        JPanel registerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        registerRow.setOpaque(false);
        JLabel noAccountLabel = new JLabel("New traveler?");
        noAccountLabel.setFont(ThemeConstants.FONT_SMALL);
        noAccountLabel.setForeground(ThemeConstants.TEXT_MUTED);

        registerButton = new JButton("Create an account");
        registerButton.setFont(ThemeConstants.FONT_SMALL);
        registerButton.setForeground(ThemeConstants.PRIMARY_COLOR);
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });

        registerRow.add(noAccountLabel);
        registerRow.add(registerButton);
        gbc.gridy = 6;
        gbc.insets = new Insets(10, 4, 4, 4);
        formCard.add(registerRow, gbc);

        mainPanel.add(formCard, BorderLayout.CENTER);

        // --- Demo Accounts Footer ---
        JPanel footerPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        footerPanel.setOpaque(false);

        JLabel demoCustomer = new JLabel("Customer Demo: john@gmail.com / user123", SwingConstants.CENTER);
        demoCustomer.setFont(ThemeConstants.FONT_SMALL);
        demoCustomer.setForeground(ThemeConstants.TEXT_MUTED);

        JLabel demoAdmin = new JLabel("Admin Demo: admin@tourism.com / admin123", SwingConstants.CENTER);
        demoAdmin.setFont(ThemeConstants.FONT_SMALL);
        demoAdmin.setForeground(ThemeConstants.TEXT_MUTED);

        footerPanel.add(demoCustomer);
        footerPanel.add(demoAdmin);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        // Trigger login on ENTER key press
        KeyAdapter enterKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        };
        emailField.addKeyListener(enterKeyAdapter);
        passwordField.addKeyListener(enterKeyAdapter);

        add(mainPanel);
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        loginButton.setEnabled(false);
        loginButton.setText("Signing in...");

        AuthService.AuthResult result = authService.login(email, password);

        if (result.isSuccess()) {
            User user = result.getUser();
            JOptionPane.showMessageDialog(this,
                    "Welcome back, " + user.getFullName() + "!\nLogged in as: " + user.getRole(),
                    "Sign In Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            // Role-based routing
            if (user.isAdmin()) {
                openAdminDashboard(user);
            } else {
                openCustomerHome(user);
            }
            dispose();
        } else {
            loginButton.setEnabled(true);
            loginButton.setText("Sign In");
            JOptionPane.showMessageDialog(this,
                    result.getMessage(),
                    "Sign In Failed",
                    JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }

    private void openAdminDashboard(User admin) {
        new com.tourism.ui.admin.AdminMainFrame(admin).setVisible(true);
    }

    private void openCustomerHome(User customer) {
        new com.tourism.ui.customer.CustomerMainFrame(customer).setVisible(true);
    }

    public static void main(String[] args) {
        com.formdev.flatlaf.FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
