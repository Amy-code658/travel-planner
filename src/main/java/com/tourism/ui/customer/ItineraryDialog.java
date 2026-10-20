package com.tourism.ui.customer;

import com.tourism.config.ThemeConstants;
import com.tourism.model.Destination;
import com.tourism.model.ItineraryDay;
import com.tourism.model.Package;
import com.tourism.service.ItineraryService.ItemizedCost;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

/**
 * Modal dialog presenting the day-by-day travel schedule and itemized cost breakdown.
 */
public class ItineraryDialog extends JDialog {

    public ItineraryDialog(Window owner, Package pkg, List<ItineraryDay> itineraryDays, ItemizedCost cost) {
        super(owner, "Trip Itinerary & Cost Estimation", ModalityType.APPLICATION_MODAL);
        initComponents(pkg, itineraryDays, cost);
    }

    private void initComponents(Package pkg, List<ItineraryDay> itineraryDays, ItemizedCost cost) {
        setSize(750, 680);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeConstants.BACKGROUND);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ThemeConstants.PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        Destination dest = (pkg != null) ? pkg.getDestination() : null;
        String destInfo = (dest != null) ? dest.getName() + ", " + dest.getCountry() + " (" + dest.getCategory() + ")" : "Custom Tour";
        String titleText = (pkg != null) ? pkg.getTitle() : "Travel Itinerary";

        JLabel titleLabel = new JLabel(titleText);
        titleLabel.setFont(ThemeConstants.FONT_HEADER_LARGE);
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel(destInfo + " • " + (pkg != null ? pkg.getDurationDays() : 0) + " Days");
        subLabel.setFont(ThemeConstants.FONT_SUBHEADER);
        subLabel.setForeground(new Color(230, 240, 255));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Main content: Itinerary Days list
        JPanel daysListPanel = new JPanel();
        daysListPanel.setLayout(new BoxLayout(daysListPanel, BoxLayout.Y_AXIS));
        daysListPanel.setBackground(ThemeConstants.BACKGROUND);
        daysListPanel.setBorder(new EmptyBorder(ThemeConstants.PADDING_MEDIUM, ThemeConstants.PADDING_MEDIUM, ThemeConstants.PADDING_MEDIUM, ThemeConstants.PADDING_MEDIUM));

        if (itineraryDays == null || itineraryDays.isEmpty()) {
            JLabel emptyLabel = new JLabel("No itinerary schedule available for this package.");
            emptyLabel.setFont(ThemeConstants.FONT_BODY);
            emptyLabel.setForeground(ThemeConstants.TEXT_MUTED);
            daysListPanel.add(emptyLabel);
        } else {
            for (ItineraryDay day : itineraryDays) {
                daysListPanel.add(createDayCard(day));
                daysListPanel.add(Box.createRigidArea(new Dimension(0, 12)));
            }
        }

        JScrollPane scrollPane = new JScrollPane(daysListPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Cost Breakdown & Action Footer
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 225, 230), 1),
            new EmptyBorder(14, 20, 14, 20)
        ));

        if (cost != null) {
            JPanel costGrid = new JPanel(new GridLayout(2, 3, 16, 6));
            costGrid.setOpaque(false);

            costGrid.add(createMetricLabel("Package Base", "$" + String.format("%.2f", cost.getNetPackageCost())));
            costGrid.add(createMetricLabel("Daily Allowance", "$" + String.format("%.2f", cost.getTotalAllowance())));
            costGrid.add(createMetricLabel("Estimated Total", "$" + String.format("%.2f", cost.getGrandTotal()), ThemeConstants.ACCENT_COLOR));

            costGrid.add(createSubMetricLabel("(" + cost.getGuests() + " guests @ $" + String.format("%.2f", cost.getBasePricePerPerson()) + "/person)"));
            costGrid.add(createSubMetricLabel("(" + cost.getDays() + " days @ $" + String.format("%.2f", cost.getDailyAllowancePerPerson()) + "/day/person)"));
            costGrid.add(createSubMetricLabel("All-inclusive projection"));

            footerPanel.add(costGrid, BorderLayout.CENTER);
        }

        JButton closeButton = new JButton("Close");
        closeButton.setFont(ThemeConstants.FONT_BOLD);
        closeButton.setBackground(ThemeConstants.PRIMARY_COLOR);
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeButton);
        footerPanel.add(buttonPanel, BorderLayout.EAST);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createDayCard(ItineraryDay day) {
        JPanel card = new JPanel(new BorderLayout(12, 6));
        card.setBackground(ThemeConstants.CARD_BG);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(225, 230, 235), 1, true),
            new EmptyBorder(12, 14, 12, 14)
        ));

        // Day Number Badge
        JLabel badge = new JLabel("Day " + day.getDayNumber(), SwingConstants.CENTER);
        badge.setOpaque(true);
        badge.setBackground(ThemeConstants.PRIMARY_COLOR);
        badge.setForeground(Color.WHITE);
        badge.setFont(ThemeConstants.FONT_BOLD);
        badge.setPreferredSize(new Dimension(65, 30));

        JPanel badgeWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badgeWrap.setOpaque(false);
        badgeWrap.add(badge);
        card.add(badgeWrap, BorderLayout.WEST);

        // Content
        JPanel textPanel = new JPanel(new BorderLayout(0, 4));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(day.getTitle());
        titleLabel.setFont(ThemeConstants.FONT_SUBHEADER);
        titleLabel.setForeground(ThemeConstants.TEXT_PRIMARY);

        JTextArea descArea = new JTextArea(day.getActivityDescription());
        descArea.setFont(ThemeConstants.FONT_BODY);
        descArea.setForeground(ThemeConstants.TEXT_MUTED);
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setBorder(null);

        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(descArea, BorderLayout.CENTER);

        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createMetricLabel(String title, String value) {
        return createMetricLabel(title, value, ThemeConstants.TEXT_PRIMARY);
    }

    private JPanel createMetricLabel(String title, String value, Color valueColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(ThemeConstants.FONT_SMALL);
        titleLbl.setForeground(ThemeConstants.TEXT_MUTED);

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(ThemeConstants.FONT_SUBHEADER);
        valLbl.setForeground(valueColor);

        panel.add(titleLbl, BorderLayout.NORTH);
        panel.add(valLbl, BorderLayout.CENTER);
        return panel;
    }

    private JLabel createSubMetricLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(ThemeConstants.FONT_SMALL);
        lbl.setForeground(ThemeConstants.TEXT_MUTED);
        return lbl;
    }
}
