package com.tourism.ui.auth;

import com.tourism.config.ThemeConstants;
import com.tourism.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modern Swing Registration Frame with FlatLaf styling.
 * Allows new travelers to create a customer account.
 */
public class RegisterFrame extends JFrame {

    private final AuthService authService;
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JCheckBox showPasswordCheck;
    private JButton registerButton;
    private JButton backToLoginButton;

    public RegisterFrame() {
        this.authService = new AuthService();
        initComponents();
    }

    private void initComponents() {
        setTitle("Tourism Planner - Create Account");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 680);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(ThemeConstants.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(30, 45, 30, 45));

        // --- Header ---
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        headerPanel.setOpaque(false);

        JLabel brandLabel = new JLabel("Start Your Journey", SwingConstants.CENTER);
        brandLabel.setFont(ThemeConstants.FONT_HEADER_LARGE);
        brandLabel.setForeground(ThemeConstants.PRIMARY_COLOR);

        JLabel subtitleLabel = new JLabel("Create an account to explore personalized trips", SwingConstants.CENTER);
        subtitleLabel.setFont(ThemeConstants.FONT_BODY);
        subtitleLabel.setForeground(ThemeConstants.TEXT_MUTED);

        headerPanel.add(brandLabel);
        headerPanel.add(subtitleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- Card Form ---
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(ThemeConstants.CARD_BG);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 235), 1),
                new EmptyBorder(20, 25, 20, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 0;

        // 1. Full Name
        JLabel nameLabel = new JLabel("Full Name");
        nameLabel.setFont(ThemeConstants.FONT_BOLD);
        gbc.gridy = 0;
        formCard.add(nameLabel, gbc);

        fullNameField = new JTextField(20);
        fullNameField.setFont(ThemeConstants.FONT_BODY);
        fullNameField.putClientProperty("JTextField.placeholderText", "e.g. Jane Doe");
        gbc.gridy = 1;
        formCard.add(fullNameField, gbc);

        // 2. Email Address
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(ThemeConstants.FONT_BOLD);
        gbc.gridy = 2;
        formCard.add(emailLabel, gbc);

        emailField = new JTextField(20);
        emailField.setFont(ThemeConstants.FONT_BODY);
        emailField.putClientProperty("JTextField.placeholderText", "e.g. jane@example.com");
        gbc.gridy = 3;
        formCard.add(emailField, gbc);

        // 3. Phone Number
        JLabel phoneLabel = new JLabel("Phone Number");
        phoneLabel.setFont(ThemeConstants.FONT_BOLD);
        gbc.gridy = 4;
        formCard.add(phoneLabel, gbc);

        phoneField = new JTextField(20);
        phoneField.setFont(ThemeConstants.FONT_BODY);
        phoneField.putClientProperty("JTextField.placeholderText", "e.g. 9876543210");
        gbc.gridy = 5;
        formCard.add(phoneField, gbc);

        // 4. Password
        JLabel passwordLabel = new JLabel("Password (min 6 chars)");
        passwordLabel.setFont(ThemeConstants.FONT_BOLD);
        gbc.gridy = 6;
        formCard.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(ThemeConstants.FONT_BODY);
        passwordField.putClientProperty("JTextField.placeholderText", "Create password");
        gbc.gridy = 7;
        formCard.add(passwordField, gbc);

        // 5. Confirm Password
        JLabel confirmPasswordLabel = new JLabel("Confirm Password");
        confirmPasswordLabel.setFont(ThemeConstants.FONT_BOLD);
        gbc.gridy = 8;
        formCard.add(confirmPasswordLabel, gbc);

        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(ThemeConstants.FONT_BODY);
        confirmPasswordField.putClientProperty("JTextField.placeholderText", "Re-enter password");
        gbc.gridy = 9;
        formCard.add(confirmPasswordField, gbc);

        // Show Password Checkbox
        showPasswordCheck = new JCheckBox("Show Passwords");
        showPasswordCheck.setFont(ThemeConstants.FONT_SMALL);
        showPasswordCheck.setOpaque(false);
        showPasswordCheck.addActionListener(e -> {
            char echoChar = showPasswordCheck.isSelected() ? (char) 0 : '•';
            passwordField.setEchoChar(echoChar);
            confirmPasswordField.setEchoChar(echoChar);
        });
        gbc.gridy = 10;
        formCard.add(showPasswordCheck, gbc);

        // Register Button
        registerButton = new JButton("Create Account");
        registerButton.setFont(ThemeConstants.FONT_BOLD);
        registerButton.setBackground(ThemeConstants.ACCENT_COLOR);
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.setPreferredSize(new Dimension(150, 40));
        registerButton.addActionListener(e -> handleRegister());
        gbc.gridy = 11;
        gbc.insets = new Insets(14, 4, 6, 4);
        formCard.add(registerButton, gbc);

        // Back to Login Link
        JPanel loginRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        loginRow.setOpaque(false);
        JLabel hasAccountLabel = new JLabel("Already have an account?");
        hasAccountLabel.setFont(ThemeConstants.FONT_SMALL);
        hasAccountLabel.setForeground(ThemeConstants.TEXT_MUTED);

        backToLoginButton = new JButton("Sign In");
        backToLoginButton.setFont(ThemeConstants.FONT_SMALL);
        backToLoginButton.setForeground(ThemeConstants.PRIMARY_COLOR);
        backToLoginButton.setBorderPainted(false);
        backToLoginButton.setContentAreaFilled(false);
        backToLoginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backToLoginButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        loginRow.add(hasAccountLabel);
        loginRow.add(backToLoginButton);
        gbc.gridy = 12;
        gbc.insets = new Insets(8, 4, 4, 4);
        formCard.add(loginRow, gbc);

        mainPanel.add(formCard, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void handleRegister() {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        registerButton.setEnabled(false);
        registerButton.setText("Registering...");

        AuthService.AuthResult result = authService.register(fullName, email, phone, password, confirmPassword);

        if (result.isSuccess()) {
            JOptionPane.showMessageDialog(this,
                    "Account created successfully!\nYou can now log in.",
                    "Registration Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            new LoginFrame().setVisible(true);
            dispose();
        } else {
            registerButton.setEnabled(true);
            registerButton.setText("Create Account");
            JOptionPane.showMessageDialog(this,
                    result.getMessage(),
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}
