package com.tourism.ui.customer;

import com.tourism.config.ThemeConstants;
import com.tourism.dao.ItineraryDAO;
import com.tourism.model.ItineraryDay;
import com.tourism.model.Package;
import com.tourism.model.User;
import com.tourism.service.AuthService;
import com.tourism.service.ItineraryService;
import com.tourism.ui.auth.LoginFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Main window for logged-in Customers.
 * Integrates Catalog browsing, Personalized Recommendations, and Booking History tabs.
 */
public class CustomerMainFrame extends JFrame {

    private final User customer;
    private final AuthService authService;
    private final ItineraryDAO itineraryDAO;
    private final ItineraryService itineraryService;

    private JTabbedPane tabbedPane;
    private CatalogPanel catalogPanel;
    private RecommendationsPanel recommendationsPanel;
    private BookingHistoryPanel bookingHistoryPanel;

    public CustomerMainFrame(User customer) {
        this.customer = customer;
        this.authService = new AuthService();
        this.itineraryDAO = new ItineraryDAO();
        this.itineraryService = new ItineraryService();
        initComponents();
    }

    private void initComponents() {
        setTitle("Tourism Planner - " + customer.getFullName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setMinimumSize(new Dimension(850, 600));
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(ThemeConstants.BACKGROUND);

        // --- Top Navigation Header ---
        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setBackground(ThemeConstants.PRIMARY_COLOR);
        headerBar.setBorder(new EmptyBorder(12, 20, 12, 20));

        // Brand Title
        JLabel brandLabel = new JLabel("Wanderlust Travel Planner");
        brandLabel.setFont(ThemeConstants.FONT_HEADER);
        brandLabel.setForeground(Color.WHITE);
        headerBar.add(brandLabel, BorderLayout.WEST);

        // User Greeting & Logout
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Welcome, " + customer.getFullName());
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

        kpiBar.add(createKPICard("🌴", "Destinations", "6 Global Spots", "Bali, Alps, Kyoto & more", new Color(41, 128, 185)));
        kpiBar.add(createKPICard("📦", "Curated Tours", "6 Packages", "Discounts up to 15% off", new Color(39, 174, 96)));
        kpiBar.add(createKPICard("✈️", "Reservations", "Active Bookings", "View & manage in Tab 3", new Color(243, 156, 18)));
        kpiBar.add(createKPICard("⭐", "Traveler Rating", "4.8 / 5.0", "Based on verified reviews", new Color(142, 68, 173)));

        JPanel northWrapper = new JPanel(new BorderLayout());
        northWrapper.add(headerBar, BorderLayout.NORTH);
        northWrapper.add(kpiBar, BorderLayout.SOUTH);

        contentPane.add(northWrapper, BorderLayout.NORTH);

        // --- Tabbed Navigation ---
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(ThemeConstants.FONT_BOLD);

        // 1. Explore Catalog Tab
        catalogPanel = new CatalogPanel();
        catalogPanel.setOnBookPackageListener(this::openBookingDialog);
        catalogPanel.setOnPackageSelectedListener(this::openItineraryDialog);
        tabbedPane.addTab("  🌴 Explore Packages  ", catalogPanel);

        // 2. Recommendations Tab
        recommendationsPanel = new RecommendationsPanel();
        tabbedPane.addTab("  🎯 Personalized Recommendations  ", recommendationsPanel);

        // 3. My Bookings Tab
        bookingHistoryPanel = new BookingHistoryPanel(customer.getUserId());
        tabbedPane.addTab("  📅 My Bookings & History  ", bookingHistoryPanel);

        // Auto-refresh bookings when switching to the Bookings tab
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() == bookingHistoryPanel) {
                bookingHistoryPanel.loadBookings();
            }
        });

        contentPane.add(tabbedPane, BorderLayout.CENTER);
        add(contentPane);
    }

    private void openBookingDialog(Package pkg) {
        BookingDialog dialog = new BookingDialog(this, pkg, customer.getUserId());
        dialog.setVisible(true);
        // Refresh bookings panel after booking dialog closes
        bookingHistoryPanel.loadBookings();
    }

    private void openItineraryDialog(Package pkg) {
        List<ItineraryDay> days = itineraryService.getItineraryForPackage(pkg);
        ItineraryService.ItemizedCost cost = itineraryService.calculateCostBreakdown(pkg, 1, pkg.getDurationDays());
        ItineraryDialog dialog = new ItineraryDialog(this, pkg, days, cost);
        dialog.setVisible(true);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out?",
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
