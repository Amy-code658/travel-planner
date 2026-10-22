package com.tourism.ui.auth;

import com.tourism.config.ThemeConstants;
import com.tourism.model.User;
import com.tourism.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;

/**
 * Modern Swing Login Frame with vibrant travel-themed background and FlatLaf styling.
 * Handles customer and administrator authentication with uncluttered floating card layout
 * and 1-click quick demo credentials.
 */
public class LoginFrame extends JFrame {

    private final AuthService authService;
    private LoginPanel loginPanel;

    public LoginFrame() {
        this.authService = new AuthService();
        initComponents();
    }

    private void initComponents() {
        setTitle("Wanderlust Planner - Sign In");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 640);
        setLocationRelativeTo(null);
        setResizable(false);

        loginPanel = new LoginPanel(this, authService);
        add(loginPanel);
    }

    public LoginPanel getLoginPanel() {
        return loginPanel;
    }

    public static class LoginPanel extends JPanel {
        private final Window parentWindow;
        private final AuthService authService;
        private JTextField emailField;
        private JPasswordField passwordField;
        private JCheckBox showPasswordCheck;
        private JButton loginButton;
        private JButton registerButton;

        public LoginPanel(Window parentWindow, AuthService authService) {
            this.parentWindow = parentWindow;
            this.authService = authService != null ? authService : new AuthService();
            initPanelUI();
        }

        private void initPanelUI() {
            setLayout(new GridBagLayout());

            // Load background travel image
            Image bgImg = null;
            try {
                URL imgUrl = getClass().getResource("/images/maldives.jpg");
                if (imgUrl == null) {
                    imgUrl = getClass().getResource("/images/bali.jpg");
                }
                if (imgUrl != null) {
                    bgImg = new ImageIcon(imgUrl).getImage();
                }
            } catch (Exception ignored) {
            }
            final Image travelBg = bgImg;

            // Background drawing with scenic travel photo + rich sunset/ocean gradient wash
            // Override paintComponent of this LoginPanel
            setOpaque(false);

            // Floating Glassmorphic/Elevated Card
            JPanel floatingCard = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    int w = getWidth();
                    int h = getHeight();

                    // Semi-transparent clean white card
                    g2.setColor(new Color(255, 255, 255, 246));
                    g2.fill(new RoundRectangle2D.Double(0, 0, w, h, 24, 24));

                    // Subtle border
                    g2.setColor(new Color(255, 255, 255, 180));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.draw(new RoundRectangle2D.Double(0.75, 0.75, w - 1.5, h - 1.5, 24, 24));

                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            floatingCard.setOpaque(false);
            floatingCard.setPreferredSize(new Dimension(420, 530));
            floatingCard.setLayout(new BoxLayout(floatingCard, BoxLayout.Y_AXIS));
            floatingCard.setBorder(new EmptyBorder(24, 28, 20, 28));

            // 1. Header Emblem & Branding
            JPanel brandPanel = new JPanel();
            brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
            brandPanel.setOpaque(false);
            brandPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel emblemLabel = new JLabel("✈️", SwingConstants.CENTER);
            emblemLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
            emblemLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            brandPanel.add(emblemLabel);

            JLabel titleLabel = new JLabel("WANDERLUST", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
            titleLabel.setForeground(new Color(24, 43, 73));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            brandPanel.add(titleLabel);

            JLabel subtitleLabel = new JLabel("Explore • Plan • Experience", SwingConstants.CENTER);
            subtitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            subtitleLabel.setForeground(ThemeConstants.PRIMARY_COLOR);
            subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            brandPanel.add(subtitleLabel);

            floatingCard.add(brandPanel);
            floatingCard.add(Box.createRigidArea(new Dimension(0, 20)));

            // 2. Input Fields Section
            JLabel emailLabel = new JLabel("Email Address");
            emailLabel.setFont(ThemeConstants.FONT_BOLD);
            emailLabel.setForeground(ThemeConstants.TEXT_PRIMARY);
            floatingCard.add(createLeftAlignedRow(emailLabel));
            floatingCard.add(Box.createRigidArea(new Dimension(0, 4)));

            emailField = new JTextField(20);
            emailField.setFont(ThemeConstants.FONT_BODY);
            emailField.putClientProperty("JTextField.placeholderText", "✉️  e.g. john@gmail.com");
            emailField.putClientProperty("JComponent.roundRect", true);
            emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            emailField.setPreferredSize(new Dimension(360, 38));
            floatingCard.add(emailField);

            floatingCard.add(Box.createRigidArea(new Dimension(0, 12)));

            JLabel passwordLabel = new JLabel("Password");
            passwordLabel.setFont(ThemeConstants.FONT_BOLD);
            passwordLabel.setForeground(ThemeConstants.TEXT_PRIMARY);
            floatingCard.add(createLeftAlignedRow(passwordLabel));
            floatingCard.add(Box.createRigidArea(new Dimension(0, 4)));

            passwordField = new JPasswordField(20);
            passwordField.setFont(ThemeConstants.FONT_BODY);
            passwordField.putClientProperty("JTextField.placeholderText", "🔒  Enter password");
            passwordField.putClientProperty("JComponent.roundRect", true);
            passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            passwordField.setPreferredSize(new Dimension(360, 38));
            floatingCard.add(passwordField);

            floatingCard.add(Box.createRigidArea(new Dimension(0, 6)));

            showPasswordCheck = new JCheckBox("Show Password");
            showPasswordCheck.setFont(ThemeConstants.FONT_SMALL);
            showPasswordCheck.setOpaque(false);
            showPasswordCheck.setForeground(ThemeConstants.TEXT_MUTED);
            showPasswordCheck.addActionListener(e -> {
                if (showPasswordCheck.isSelected()) {
                    passwordField.setEchoChar((char) 0);
                } else {
                    passwordField.setEchoChar('•');
                }
            });
            floatingCard.add(createLeftAlignedRow(showPasswordCheck));

            floatingCard.add(Box.createRigidArea(new Dimension(0, 14)));

            loginButton = new JButton("Start Journey  ➔");
            loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            loginButton.setBackground(ThemeConstants.PRIMARY_COLOR);
            loginButton.setForeground(Color.WHITE);
            loginButton.setFocusPainted(false);
            loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            loginButton.putClientProperty("JButton.buttonType", "roundRect");
            loginButton.putClientProperty("JComponent.roundRect", true);
            loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            loginButton.setPreferredSize(new Dimension(360, 42));
            loginButton.addActionListener(e -> handleLogin());
            floatingCard.add(loginButton);

            floatingCard.add(Box.createRigidArea(new Dimension(0, 16)));

            // 3. Quick 1-Click Demo Login Bar
            JPanel demoBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
            demoBar.setOpaque(false);

            JLabel quickLabel = new JLabel("Demo:");
            quickLabel.setFont(ThemeConstants.FONT_SMALL);
            quickLabel.setForeground(ThemeConstants.TEXT_MUTED);
            demoBar.add(quickLabel);

            JButton fillCustomerBtn = createDemoPill("👤 Customer", () -> {
                emailField.setText("john@gmail.com");
                passwordField.setText("user123");
                emailField.requestFocus();
            });
            demoBar.add(fillCustomerBtn);

            JButton fillAdminBtn = createDemoPill("🛡️ Admin", () -> {
                emailField.setText("admin@tourism.com");
                passwordField.setText("admin123");
                emailField.requestFocus();
            });
            demoBar.add(fillAdminBtn);

            floatingCard.add(demoBar);
            floatingCard.add(Box.createRigidArea(new Dimension(0, 14)));

            // 4. Register Link Footer
            JPanel registerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
            registerRow.setOpaque(false);

            JLabel noAccountLabel = new JLabel("New traveler?");
            noAccountLabel.setFont(ThemeConstants.FONT_SMALL);
            noAccountLabel.setForeground(ThemeConstants.TEXT_MUTED);

            registerButton = new JButton("Create account");
            registerButton.setFont(ThemeConstants.FONT_BOLD);
            registerButton.setForeground(ThemeConstants.PRIMARY_COLOR);
            registerButton.setBorderPainted(false);
            registerButton.setContentAreaFilled(false);
            registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            registerButton.addActionListener(e -> {
                if (parentWindow != null) {
                    parentWindow.dispose();
                }
                new RegisterFrame().setVisible(true);
            });

            registerRow.add(noAccountLabel);
            registerRow.add(registerButton);
            floatingCard.add(registerRow);

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

            add(floatingCard);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int w = getWidth();
            int h = getHeight();

            Image bgImg = null;
            try {
                URL imgUrl = getClass().getResource("/images/maldives.jpg");
                if (imgUrl == null) {
                    imgUrl = getClass().getResource("/images/bali.jpg");
                }
                if (imgUrl != null) {
                    bgImg = new ImageIcon(imgUrl).getImage();
                }
            } catch (Exception ignored) {
            }

            if (bgImg != null) {
                g2.drawImage(bgImg, 0, 0, w, h, this);
                GradientPaint overlay = new GradientPaint(
                        0, 0, new Color(15, 32, 67, 195),
                        w, h, new Color(230, 81, 0, 160)
                );
                g2.setPaint(overlay);
                g2.fillRect(0, 0, w, h);
            } else {
                GradientPaint fallbackGrad = new GradientPaint(
                        0, 0, new Color(26, 42, 108),
                        w, h, new Color(253, 187, 45)
                );
                g2.setPaint(fallbackGrad);
                g2.fillRect(0, 0, w, h);
            }
            g2.dispose();
        }

        private JPanel createLeftAlignedRow(JComponent comp) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, comp.getPreferredSize().height));
            row.add(comp);
            return row;
        }

        private JButton createDemoPill(String text, Runnable onClick) {
            JButton btn = new JButton(text);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btn.setForeground(new Color(41, 128, 185));
            btn.setBackground(new Color(235, 245, 251));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.putClientProperty("JComponent.roundRect", true);
            btn.putClientProperty("JButton.buttonType", "roundRect");
            btn.setBorder(new EmptyBorder(4, 10, 4, 10));
            btn.addActionListener(e -> onClick.run());
            return btn;
        }

        private void handleLogin() {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            loginButton.setEnabled(false);
            loginButton.setText("Signing in...");

            try {
                AuthService.AuthResult result = authService.login(email, password);

                if (result.isSuccess()) {
                    User user = result.getUser();
                    JOptionPane.showMessageDialog(this,
                            "Welcome back, " + user.getFullName() + "!\nLogged in as: " + user.getRole(),
                            "Sign In Successful",
                            JOptionPane.INFORMATION_MESSAGE);

                    if (user.isAdmin()) {
                        new com.tourism.ui.admin.AdminMainFrame(user).setVisible(true);
                    } else {
                        new com.tourism.ui.customer.CustomerMainFrame(user).setVisible(true);
                    }

                    if (parentWindow != null) {
                        parentWindow.dispose();
                    }
                } else {
                    loginButton.setEnabled(true);
                    loginButton.setText("Start Journey  ➔");
                    JOptionPane.showMessageDialog(this,
                            result.getMessage(),
                            "Sign In Failed",
                            JOptionPane.ERROR_MESSAGE);
                    passwordField.setText("");
                    passwordField.requestFocus();
                }
            } catch (Throwable t) {
                loginButton.setEnabled(true);
                loginButton.setText("Start Journey  ➔");
                JOptionPane.showMessageDialog(this,
                        "An unexpected error occurred during sign in:\n" + t.getMessage()
                                + "\n\nPlease check your MySQL database connection (port 3306).",
                        "Sign In Error",
                        JOptionPane.ERROR_MESSAGE);
                t.printStackTrace();
            }
        }

        public JTextField getEmailField() {
            return emailField;
        }

        public JPasswordField getPasswordField() {
            return passwordField;
        }

        public JButton getLoginButton() {
            return loginButton;
        }
    }

    public static void main(String[] args) {
        com.formdev.flatlaf.FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
