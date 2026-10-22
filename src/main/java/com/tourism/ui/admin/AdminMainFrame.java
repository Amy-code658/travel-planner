package com.tourism.ui.admin;

import com.tourism.config.ThemeConstants;
import com.tourism.model.User;
import com.tourism.service.AuthService;
import com.tourism.ui.auth.LoginFrame;
import com.tourism.ui.customer.CatalogPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main window for Administrators.
 * Integrates visual reports, revenue analytics, and package browsing.
 */
public class AdminMainFrame extends JFrame {

    private final User admin;
    private final AuthService authService;

    public AdminMainFrame(User admin) {
        this.admin = admin;
        this.authService = new AuthService();
        initComponents();
    }

    private void initComponents() {
        setTitle("Tourism Admin Console - " + admin.getFullName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setMinimumSize(new Dimension(850, 600));
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(ThemeConstants.BACKGROUND);

        // --- Header Bar ---
        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setBackground(ThemeConstants.PRIMARY_DARK);
        headerBar.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel brandLabel = new JLabel("Administrator Dashboard");
        brandLabel.setFont(ThemeConstants.FONT_HEADER);
        brandLabel.setForeground(Color.WHITE);
        headerBar.add(brandLabel, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Admin: " + admin.getFullName());
        welcomeLabel.setFont(ThemeConstants.FONT_BOLD);
        welcomeLabel.setForeground(Color.WHITE);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(ThemeConstants.FONT_SMALL);
        logoutBtn.setBackground(new Color(231, 76, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> handleLogout());

        userPanel.add(welcomeLabel);
        userPanel.add(logoutBtn);
        headerBar.add(userPanel, BorderLayout.EAST);

        // Top KPI metrics row
        JPanel kpiBar = new JPanel(new GridLayout(1, 4, 12, 0));
        kpiBar.setBackground(ThemeConstants.BACKGROUND);
        kpiBar.setBorder(new EmptyBorder(12, 20, 6, 20));

        kpiBar.add(createKPICard("👥", "Total Users", "3 Accounts", "2 Customers, 1 Admin", new Color(41, 128, 185)));
        kpiBar.add(createKPICard("📦", "Active Packages", "6 Tours", "Available for booking", new Color(39, 174, 96)));
        kpiBar.add(createKPICard("💰", "Gross Volume", "$3,800", "System transaction value", new Color(243, 156, 18)));
        kpiBar.add(createKPICard("📊", "Top Destination", "Bali, Indonesia", "Leading booking volume", new Color(142, 68, 173)));

        JPanel northWrapper = new JPanel(new BorderLayout());
        northWrapper.add(headerBar, BorderLayout.NORTH);
        northWrapper.add(kpiBar, BorderLayout.SOUTH);

        contentPane.add(northWrapper, BorderLayout.NORTH);

        // --- Tabs ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(ThemeConstants.FONT_BOLD);

        // Tab 1: Visual Reports & Charts
        ReportsPanel reportsPanel = new ReportsPanel();
        tabbedPane.addTab("  📊 Reports & Visual Analytics  ", reportsPanel);

        // Tab 2: Catalog Overview
        CatalogPanel catalogPanel = new CatalogPanel();
        tabbedPane.addTab("  🌴 Package Overview  ", catalogPanel);

        contentPane.add(tabbedPane, BorderLayout.CENTER);
        add(contentPane);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out of the Admin Console?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            authService.logout();
            new LoginFrame().setVisible(true);
            dispose();
        }
    }

    private JPanel createKPICard(String icon, String title, String value, String subtitle, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setBackground(ThemeConstants.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, accentColor),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(225, 230, 235), 1),
                        BorderFactory.createEmptyBorder(10, 14, 10, 14)
                )
        ));

        JLabel titleLabel = new JLabel(icon + " " + title);
        titleLabel.setFont(ThemeConstants.FONT_SMALL);
        titleLabel.setForeground(ThemeConstants.TEXT_MUTED);
        card.add(titleLabel, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(ThemeConstants.FONT_HEADER);
        valueLabel.setForeground(ThemeConstants.TEXT_PRIMARY);
        card.add(valueLabel, BorderLayout.CENTER);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(ThemeConstants.FONT_SMALL);
        subLabel.setForeground(accentColor);
        card.add(subLabel, BorderLayout.SOUTH);

        return card;
    }
}
