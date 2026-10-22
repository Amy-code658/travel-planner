package com.tourism.ui.customer;

import com.tourism.config.ThemeConstants;
import com.tourism.model.Destination;
import com.tourism.model.ItineraryDay;
import com.tourism.model.Package;
import com.tourism.service.ItineraryService.ItemizedCost;
import com.tourism.service.PDFExportService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
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

        // Header Panel with Real Destination Photograph
        Destination dest = (pkg != null) ? pkg.getDestination() : null;
        String imageUrl = (dest != null && dest.getImageUrl() != null) ? dest.getImageUrl().trim() : "bali.jpg";
        Image heroImage = null;
        try {
            java.net.URL res = getClass().getResource("/images/" + imageUrl);
            if (res != null) {
                heroImage = new ImageIcon(res).getImage();
            }
        } catch (Exception ignored) {}

        final Image finalHero = heroImage;
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                if (finalHero != null) {
                    g2.drawImage(finalHero, 0, 0, w, h, this);
                    GradientPaint overlay = new GradientPaint(
                            0, 0, new Color(0, 0, 0, 110),
                            0, h, new Color(0, 0, 0, 210)
                    );
                    g2.setPaint(overlay);
                    g2.fillRect(0, 0, w, h);
                } else {
                    GradientPaint grad = new GradientPaint(0, 0, ThemeConstants.PRIMARY_DARK, 0, h, ThemeConstants.PRIMARY_COLOR);
                    g2.setPaint(grad);
                    g2.fillRect(0, 0, w, h);
                }
                g2.dispose();
            }
        };
        headerPanel.setPreferredSize(new Dimension(750, 145));
        headerPanel.setBorder(new EmptyBorder(22, 24, 22, 24));

        String destInfo = (dest != null) ? dest.getName() + ", " + dest.getCountry() + " (" + dest.getCategory() + ")" : "Custom Tour";
        String titleText = (pkg != null) ? pkg.getTitle() : "Travel Itinerary";

        JLabel titleLabel = new JLabel(titleText);
        titleLabel.setFont(ThemeConstants.FONT_HEADER_LARGE);
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel("📸 " + destInfo + " • ⏱ " + (pkg != null ? pkg.getDurationDays() : 0) + " Days");
        subLabel.setFont(ThemeConstants.FONT_SUBHEADER);
        subLabel.setForeground(new Color(240, 245, 255));

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
                daysListPanel.add(createDayCard(day, imageUrl));
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

            com.tourism.config.CurrencyManager cm = com.tourism.config.CurrencyManager.getInstance();
            costGrid.add(createMetricLabel("Package Base", cm.format(cost.getNetPackageCost())));
            costGrid.add(createMetricLabel("Daily Allowance", cm.format(cost.getTotalAllowance())));
            costGrid.add(createMetricLabel("Estimated Total", cm.format(cost.getGrandTotal()), ThemeConstants.ACCENT_COLOR));

            costGrid.add(createSubMetricLabel("(" + cost.getGuests() + " guests @ " + cm.format(cost.getBasePricePerPerson()) + "/person)"));
            costGrid.add(createSubMetricLabel("(" + cost.getDays() + " days @ " + cm.format(cost.getDailyAllowancePerPerson()) + "/day/person)"));
            costGrid.add(createSubMetricLabel("All-inclusive projection"));

            footerPanel.add(costGrid, BorderLayout.CENTER);
        }

        JButton downloadPdfBtn = new JButton("📄 Download PDF Itinerary");
        downloadPdfBtn.setFont(ThemeConstants.FONT_BOLD);
        downloadPdfBtn.setBackground(new Color(235, 245, 251));
        downloadPdfBtn.setForeground(ThemeConstants.PRIMARY_DARK);
        downloadPdfBtn.setFocusPainted(false);
        downloadPdfBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        downloadPdfBtn.addActionListener(e -> exportItineraryPDF(pkg, itineraryDays));

        JButton closeButton = new JButton("Close");
        closeButton.setFont(ThemeConstants.FONT_BOLD);
        closeButton.setBackground(ThemeConstants.PRIMARY_COLOR);
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(downloadPdfBtn);
        buttonPanel.add(closeButton);
        footerPanel.add(buttonPanel, BorderLayout.EAST);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private void exportItineraryPDF(Package pkg, List<ItineraryDay> days) {
        if (pkg == null) {
            JOptionPane.showMessageDialog(this, "No package selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Itinerary PDF");
        String sanitizedTitle = pkg.getTitle().replaceAll("[^a-zA-Z0-9_-]", "_");
        fileChooser.setSelectedFile(new File(sanitizedTitle + "_Itinerary.pdf"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File targetFile = fileChooser.getSelectedFile();
            if (!targetFile.getName().toLowerCase().endsWith(".pdf")) {
                targetFile = new File(targetFile.getParentFile(), targetFile.getName() + ".pdf");
            }
            try {
                PDFExportService pdfService = new PDFExportService();
                pdfService.exportItineraryPDF(pkg, days, targetFile);
                JOptionPane.showMessageDialog(
                        this,
                        "Itinerary exported successfully!\nSaved to: " + targetFile.getAbsolutePath(),
                        "PDF Export Successful",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to export itinerary PDF:\n" + ex.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private JPanel createDayCard(ItineraryDay day, String imageUrl) {
        JPanel card = new JPanel(new BorderLayout(14, 8));
        card.setBackground(ThemeConstants.CARD_BG);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(225, 230, 235), 1, true),
            new EmptyBorder(12, 14, 12, 14)
        ));

        // Left Side: Day Badge + Scenic Photo Thumbnail
        JPanel leftSide = new JPanel();
        leftSide.setLayout(new BoxLayout(leftSide, BoxLayout.Y_AXIS));
        leftSide.setOpaque(false);

        JLabel badge = new JLabel("Day " + day.getDayNumber(), SwingConstants.CENTER);
        badge.setOpaque(true);
        badge.setBackground(ThemeConstants.PRIMARY_COLOR);
        badge.setForeground(Color.WHITE);
        badge.setFont(ThemeConstants.FONT_BOLD);
        badge.setPreferredSize(new Dimension(75, 26));
        badge.setMaximumSize(new Dimension(75, 26));
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Thumbnail image
        Image placeThumb = null;
        try {
            java.net.URL res = getClass().getResource("/images/" + imageUrl);
            if (res != null) {
                placeThumb = new ImageIcon(res).getImage().getScaledInstance(75, 55, Image.SCALE_SMOOTH);
            }
        } catch (Exception ignored) {}

        JLabel thumbLabel;
        if (placeThumb != null) {
            thumbLabel = new JLabel(new ImageIcon(placeThumb));
            thumbLabel.setBorder(new LineBorder(new Color(210, 215, 225), 1, true));
        } else {
            thumbLabel = new JLabel("📍 Tour", SwingConstants.CENTER);
            thumbLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        }
        thumbLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftSide.add(badge);
        leftSide.add(Box.createRigidArea(new Dimension(0, 6)));
        leftSide.add(thumbLabel);
        card.add(leftSide, BorderLayout.WEST);

        // Content
        JPanel textPanel = new JPanel(new BorderLayout(0, 4));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(day.getTitle());
        titleLabel.setFont(ThemeConstants.FONT_SUBHEADER);
        titleLabel.setForeground(ThemeConstants.PRIMARY_DARK);

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
