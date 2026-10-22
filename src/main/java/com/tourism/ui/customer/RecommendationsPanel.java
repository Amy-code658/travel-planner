package com.tourism.ui.customer;

import com.formdev.flatlaf.FlatLightLaf;
import com.tourism.config.ThemeConstants;
import com.tourism.model.Destination;
import com.tourism.model.ItineraryDay;
import com.tourism.model.Package;
import com.tourism.service.ItineraryService;
import com.tourism.service.RecommendationService;
import com.tourism.service.RecommendationService.ScoredPackage;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * RecommendationsPanel provides an interactive questionnaire for customers
 * to input their budget, duration, guest count, and theme interests,
 * displaying ranked packages calculated by the rule-based recommendation engine.
 */
public class RecommendationsPanel extends JPanel {

    private final RecommendationService recommendationService;
    private final ItineraryService itineraryService;

    // Filter controls
    private JSpinner budgetSpinner;
    private JSpinner durationSpinner;
    private JSpinner guestsSpinner;
    private JCheckBox cbBeach;
    private JCheckBox cbAdventure;
    private JCheckBox cbCulture;
    private JCheckBox cbNature;
    private JCheckBox cbLuxury;

    // Results container
    private JPanel resultsContainer;
    private JLabel statusLabel;

    public RecommendationsPanel() {
        this.recommendationService = new RecommendationService();
        this.itineraryService = new ItineraryService();
        initComponents();
        executeRecommendationSearch();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(ThemeConstants.BACKGROUND);

        // Header Banner
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ThemeConstants.PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel titleLabel = new JLabel("Personalized Package Recommendations");
        titleLabel.setFont(ThemeConstants.FONT_HEADER_LARGE);
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel("Our rule-based engine scores and ranks trips based on your personal travel preferences.");
        subLabel.setFont(ThemeConstants.FONT_BODY);
        subLabel.setForeground(new Color(235, 245, 255));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Main layout: Left filter sidebar, Right results list
        JPanel mainContent = new JPanel(new BorderLayout(16, 0));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(16, 16, 16, 16));

        mainContent.add(createFilterSidebar(), BorderLayout.WEST);
        mainContent.add(createResultsView(), BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createFilterSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(ThemeConstants.CARD_BG);
        sidebar.setPreferredSize(new Dimension(300, 0));
        sidebar.setBorder(new CompoundBorder(
            new LineBorder(new Color(225, 230, 235), 1, true),
            new EmptyBorder(18, 16, 18, 16)
        ));

        JLabel filterHeader = new JLabel("Travel Preferences");
        filterHeader.setFont(ThemeConstants.FONT_HEADER);
        filterHeader.setForeground(ThemeConstants.TEXT_PRIMARY);
        sidebar.add(filterHeader);
        sidebar.add(Box.createRigidArea(new Dimension(0, 16)));

        // 1. Budget Filter
        sidebar.add(createFieldLabel("Maximum Total Budget ($)"));
        budgetSpinner = new JSpinner(new SpinnerNumberModel(1500.0, 100.0, 20000.0, 100.0));
        styleSpinner(budgetSpinner);
        sidebar.add(budgetSpinner);
        sidebar.add(Box.createRigidArea(new Dimension(0, 14)));

        // 2. Duration Filter
        sidebar.add(createFieldLabel("Trip Duration (Days)"));
        durationSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 30, 1));
        styleSpinner(durationSpinner);
        sidebar.add(durationSpinner);
        sidebar.add(Box.createRigidArea(new Dimension(0, 14)));

        // 3. Guests Filter
        sidebar.add(createFieldLabel("Travelers / Party Size"));
        guestsSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
        styleSpinner(guestsSpinner);
        sidebar.add(guestsSpinner);
        sidebar.add(Box.createRigidArea(new Dimension(0, 16)));

        // 4. Interests / Themes
        sidebar.add(createFieldLabel("Preferred Travel Themes"));
        cbBeach = new JCheckBox("Beach & Coastal", true);
        cbAdventure = new JCheckBox("Adventure & Trekking", false);
        cbCulture = new JCheckBox("Culture & Heritage", false);
        cbNature = new JCheckBox("Nature & Scenic", false);
        cbLuxury = new JCheckBox("Luxury & Wellness", false);

        styleCheckBox(cbBeach);
        styleCheckBox(cbAdventure);
        styleCheckBox(cbCulture);
        styleCheckBox(cbNature);
        styleCheckBox(cbLuxury);

        sidebar.add(cbBeach);
        sidebar.add(cbAdventure);
        sidebar.add(cbCulture);
        sidebar.add(cbNature);
        sidebar.add(cbLuxury);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        // Search Button
        JButton searchBtn = new JButton("Find Best Matches");
        searchBtn.setFont(ThemeConstants.FONT_BOLD);
        searchBtn.setBackground(ThemeConstants.PRIMARY_COLOR);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        searchBtn.addActionListener(e -> executeRecommendationSearch());
        sidebar.add(searchBtn);

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JPanel createResultsView() {
        JPanel view = new JPanel(new BorderLayout(0, 10));
        view.setOpaque(false);

        // Top Status Bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setOpaque(false);

        statusLabel = new JLabel("Evaluating personalized packages...");
        statusLabel.setFont(ThemeConstants.FONT_SUBHEADER);
        statusLabel.setForeground(ThemeConstants.TEXT_PRIMARY);
        statusBar.add(statusLabel, BorderLayout.WEST);

        view.add(statusBar, BorderLayout.NORTH);

        // Results Container inside ScrollPane
        resultsContainer = new JPanel();
        resultsContainer.setLayout(new BoxLayout(resultsContainer, BoxLayout.Y_AXIS));
        resultsContainer.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(resultsContainer);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        view.add(scrollPane, BorderLayout.CENTER);
        return view;
    }

    public void executeRecommendationSearch() {
        double maxBudget = ((Number) budgetSpinner.getValue()).doubleValue();
        int desiredDays = ((Number) durationSpinner.getValue()).intValue();
        int guests = ((Number) guestsSpinner.getValue()).intValue();

        List<String> selectedThemes = new ArrayList<>();
        if (cbBeach.isSelected()) selectedThemes.add("Beach");
        if (cbAdventure.isSelected()) selectedThemes.add("Adventure");
        if (cbCulture.isSelected()) selectedThemes.add("Culture");
        if (cbNature.isSelected()) selectedThemes.add("Nature");
        if (cbLuxury.isSelected()) selectedThemes.add("Luxury");

        resultsContainer.removeAll();

        List<ScoredPackage> recommendations = recommendationService.getRecommendations(
            maxBudget, desiredDays, guests, selectedThemes
        );

        // Fallback to sample packages if database is empty during offline review
        if (recommendations == null || recommendations.isEmpty()) {
            recommendations = recommendationService.evaluateAndRankPackages(
                getOfflineFallbackPackages(), maxBudget, desiredDays, guests, selectedThemes
            );
        }

        statusLabel.setText("Found " + recommendations.size() + " matches ranked by recommendation score:");

        for (ScoredPackage sp : recommendations) {
            Package pkg = sp.getTravelPackage();
            PackageCard card = new PackageCard(pkg);
            card.setRecommendationScore(sp.getScorePercentage(), sp.getMatchSummary());

            // Dev 4 logic: Hook into "View Itinerary" to open ItineraryDialog
            card.setOnViewDetailsListener(selectedPkg -> {
                List<ItineraryDay> daysList = itineraryService.getItineraryForPackage(selectedPkg);
                ItineraryService.ItemizedCost cost = itineraryService.calculateCostBreakdown(selectedPkg, guests, desiredDays);
                Window parentWindow = SwingUtilities.getWindowAncestor(this);
                ItineraryDialog dialog = new ItineraryDialog(parentWindow, selectedPkg, daysList, cost);
                dialog.setVisible(true);
            });

            // Hook into "Book Now" to trigger booking flow
            card.setOnBookNowListener(selectedPkg -> {
                int uid = (com.tourism.model.UserSession.getInstance().getCurrentUser() != null)
                        ? com.tourism.model.UserSession.getInstance().getCurrentUser().getUserId()
                        : 2;
                Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
                new BookingDialog(owner, selectedPkg, uid).setVisible(true);
            });

            resultsContainer.add(card);
            resultsContainer.add(Box.createRigidArea(new Dimension(0, 14)));
        }

        resultsContainer.revalidate();
        resultsContainer.repaint();
    }

    private Color getScoreColor(double score) {
        if (score >= 80.0) return ThemeConstants.ACCENT_COLOR;
        if (score >= 60.0) return ThemeConstants.WARNING_COLOR;
        return ThemeConstants.TEXT_MUTED;
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(ThemeConstants.FONT_BOLD);
        lbl.setForeground(ThemeConstants.TEXT_PRIMARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(ThemeConstants.FONT_BODY);
        spinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        spinner.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void styleCheckBox(JCheckBox cb) {
        cb.setFont(ThemeConstants.FONT_BODY);
        cb.setForeground(ThemeConstants.TEXT_PRIMARY);
        cb.setOpaque(false);
        cb.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    /**
     * Fallback package catalog in case DB is offline during testing.
     */
    private List<Package> getOfflineFallbackPackages() {
        List<Package> list = new ArrayList<>();

        Destination bali = new Destination(1, "Bali", "Indonesia", "Tropical beaches and temples", "Beach", 60.0, "bali.jpg");
        Destination alps = new Destination(2, "Swiss Alps", "Switzerland", "Alpine trails and scenery", "Adventure", 180.0, "alps.jpg");
        Destination kyoto = new Destination(3, "Kyoto", "Japan", "Ancient shrines and gardens", "Culture", 110.0, "kyoto.jpg");
        Destination manali = new Destination(4, "Manali", "India", "Mountain peaks and valleys", "Nature", 40.0, "manali.jpg");
        Destination paris = new Destination(5, "Paris", "France", "Luxury city of arts", "Luxury", 160.0, "paris.jpg");

        Package p1 = new Package(1, 1, "Bali Tropical Island Escape", "Kuta beach and Ubud forest", 5, 450.0, "Summer", 10.0, true);
        p1.setDestination(bali);

        Package p2 = new Package(2, 2, "Swiss Alpine Explorer", "Matterhorn trails and trains", 6, 1200.0, "Winter", 5.0, true);
        p2.setDestination(alps);

        Package p3 = new Package(3, 3, "Kyoto Heritage & Zen Trails", "Temples and tea ceremonies", 4, 650.0, "Spring", 0.0, true);
        p3.setDestination(kyoto);

        Package p4 = new Package(4, 4, "Himalayan Adventure in Manali", "Paragliding and rafting", 4, 220.0, "Summer", 0.0, true);
        p4.setDestination(manali);

        Package p5 = new Package(5, 5, "Romantic Paris Luxury Tour", "Louvre and Seine cruise", 5, 1100.0, "All Season", 15.0, true);
        p5.setDestination(paris);

        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);
        list.add(p5);
        return list;
    }

    /**
     * Standalone launcher to preview the Recommendations Panel directly.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new FlatLightLaf());
            } catch (Exception ignored) {}

            JFrame frame = new JFrame("Smart Recommendation Engine - Dev 4 Preview");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 720);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new RecommendationsPanel());
            frame.setVisible(true);
        });
    }
}
