package com.tourism;

import com.formdev.flatlaf.FlatLightLaf;
import com.tourism.config.DBConnection;
import com.tourism.config.ThemeConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Main application entry point for Personalized Tourism Management System.
 * Configures FlatLaf UI, validates database connectivity, and bootstraps the desktop client.
 */
public class Main {

    public static void main(String[] args) {
        // Set modern FlatLaf Look and Feel
        FlatLightLaf.setup();

        // Run UI on Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Personalized Tourism Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(620, 380);
        frame.setLocationRelativeTo(null); // Center on screen

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(ThemeConstants.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(ThemeConstants.PADDING_LARGE, ThemeConstants.PADDING_LARGE, ThemeConstants.PADDING_LARGE, ThemeConstants.PADDING_LARGE));

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Personalized Tourism Management System", SwingConstants.CENTER);
        titleLabel.setFont(ThemeConstants.FONT_HEADER_LARGE);
        titleLabel.setForeground(ThemeConstants.PRIMARY_DARK);

        JLabel subtitleLabel = new JLabel("Java Swing + FlatLaf + MySQL Architecture", SwingConstants.CENTER);
        subtitleLabel.setFont(ThemeConstants.FONT_BODY);
        subtitleLabel.setForeground(ThemeConstants.TEXT_MUTED);

        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Status Card
        JPanel cardPanel = new JPanel(new BorderLayout(10, 10));
        cardPanel.setBackground(ThemeConstants.CARD_BG);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Database status check
        String dbHtmlStatus;
        int destinationCount = 0;
        int packageCount = 0;

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn != null && !conn.isClosed()) {
                Statement stmt = conn.createStatement();
                ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) FROM destinations");
                if (rs1.next()) destinationCount = rs1.getInt(1);

                ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) FROM travel_packages");
                if (rs2.next()) packageCount = rs2.getInt(1);

                dbHtmlStatus = "<html><center>"
                        + "<font color='#27ae60' size='+1'><b>✔ Database Connected Successfully</b></font><br><br>"
                        + "<font color='#2c3e50'>Connected to <b>tourism_db</b> on localhost:3306 as <code>tourism_user</code>.<br>"
                        + "Loaded <b>" + destinationCount + " Destinations</b> and <b>" + packageCount + " Travel Packages</b>."
                        + "</font></center></html>";
            } else {
                dbHtmlStatus = "<html><center><font color='#c0392b'><b>✖ Database Disconnected</b></font></center></html>";
            }
        } catch (Exception e) {
            dbHtmlStatus = "<html><center><font color='#c0392b'><b>✖ Database Error:</b> " + e.getMessage() + "</font></center></html>";
        }

        JLabel statusLabel = new JLabel(dbHtmlStatus, SwingConstants.CENTER);
        statusLabel.setFont(ThemeConstants.FONT_BODY);
        cardPanel.add(statusLabel, BorderLayout.CENTER);

        mainPanel.add(cardPanel, BorderLayout.CENTER);

        // Footer note for development team
        JLabel footerLabel = new JLabel("Core Foundation complete. Ready for Dev 2 (Auth) & Dev 3 (Catalog).", SwingConstants.CENTER);
        footerLabel.setFont(ThemeConstants.FONT_SMALL);
        footerLabel.setForeground(ThemeConstants.TEXT_MUTED);
        mainPanel.add(footerLabel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }
}
