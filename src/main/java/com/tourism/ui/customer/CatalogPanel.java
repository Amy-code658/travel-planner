package com.tourism.ui.customer;

import com.formdev.flatlaf.FlatLightLaf;
import com.tourism.config.ThemeConstants;
import com.tourism.dao.DestinationDAO;
import com.tourism.dao.PackageDAO;
import com.tourism.model.Package;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Customer Catalog Panel displaying a searchable and filterable grid of travel package cards.
 * Implements asynchronous data loading via SwingWorker and follows FlatLaf and ThemeConstants design guidelines.
 */
public class CatalogPanel extends JPanel {

    private final PackageDAO packageDAO;
    private final DestinationDAO destinationDAO;

    // Filter UI components
    private JTextField searchField;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> seasonCombo;
    private JComboBox<String> priceCombo;
    private JButton searchButton;
    private JButton resetButton;
    private JLabel resultCountLabel;

    // Content container components
    private JPanel gridPanel;
    private JPanel centerContentWrapper;
    private JScrollPane scrollPane;
    private int currentColumns = 3;

    // Callbacks for team integration (Dev 4 Itinerary & Dev 5 Booking)
    private Consumer<Package> onPackageSelectedListener;
    private Consumer<Package> onBookPackageListener;

    public CatalogPanel() {
        this(new PackageDAO(), new DestinationDAO());
    }

    public CatalogPanel(PackageDAO packageDAO, DestinationDAO destinationDAO) {
        this.packageDAO = packageDAO;
        this.destinationDAO = destinationDAO;

        initUI();
        loadFilterOptionsAsync();
        applyFiltersAsync();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, ThemeConstants.PADDING_MEDIUM));
        setBackground(ThemeConstants.BACKGROUND);
        setBorder(new EmptyBorder(ThemeConstants.PADDING_MEDIUM, ThemeConstants.PADDING_LARGE,
                                  ThemeConstants.PADDING_LARGE, ThemeConstants.PADDING_LARGE));

        // 1. Header & Filter Panel (NORTH)
        JPanel northPanel = createNorthPanel();
        add(northPanel, BorderLayout.NORTH);

        // 2. Responsive Card Grid inside ScrollPane (CENTER)
        JPanel centerPanel = createCenterGridPanel();
        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Creates the top section containing the page title and the search/filter controls card.
     */
    private JPanel createNorthPanel() {
        JPanel northContainer = new JPanel();
        northContainer.setLayout(new BoxLayout(northContainer, BoxLayout.Y_AXIS));
        northContainer.setOpaque(false);

        // Header Title and Counter Row
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        titleRow.setBorder(new EmptyBorder(0, 0, ThemeConstants.PADDING_SMALL, 0));

        JPanel titleTextPanel = new JPanel();
        titleTextPanel.setLayout(new BoxLayout(titleTextPanel, BoxLayout.Y_AXIS));
        titleTextPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Explore Travel Packages");
        titleLabel.setFont(ThemeConstants.FONT_HEADER_LARGE);
        titleLabel.setForeground(ThemeConstants.PRIMARY_DARK);

        JLabel subtitleLabel = new JLabel("Discover handpicked vacation packages, seasonal deals, and unforgettable destinations.");
        subtitleLabel.setFont(ThemeConstants.FONT_BODY);
        subtitleLabel.setForeground(ThemeConstants.TEXT_MUTED);

        titleTextPanel.add(titleLabel);
        titleTextPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        titleTextPanel.add(subtitleLabel);

        titleRow.add(titleTextPanel, BorderLayout.WEST);

        resultCountLabel = new JLabel("Loading packages...");
        resultCountLabel.setFont(ThemeConstants.FONT_BOLD);
        resultCountLabel.setForeground(ThemeConstants.PRIMARY_COLOR);
        titleRow.add(resultCountLabel, BorderLayout.EAST);

        northContainer.add(titleRow);
        northContainer.add(Box.createRigidArea(new Dimension(0, 8)));

        // Filter Controls Card
        JPanel filterCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        filterCard.setBackground(ThemeConstants.CARD_BG);
        filterCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(225, 230, 238), 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));

        // Search Field
        JLabel searchIconLabel = new JLabel("Search:");
        searchIconLabel.setFont(ThemeConstants.FONT_BOLD);
        searchIconLabel.setForeground(ThemeConstants.TEXT_PRIMARY);
        filterCard.add(searchIconLabel);

        searchField = new JTextField(14);
        searchField.putClientProperty("JTextField.placeholderText", "Package or city...");
        searchField.setFont(ThemeConstants.FONT_BODY);
        searchField.addActionListener(e -> applyFiltersAsync());
        filterCard.add(searchField);

        // Category Filter
        JLabel catLabel = new JLabel("Category:");
        catLabel.setFont(ThemeConstants.FONT_BOLD);
        catLabel.setForeground(ThemeConstants.TEXT_PRIMARY);
        filterCard.add(catLabel);

        categoryCombo = new JComboBox<>(new String[]{"All Categories"});
        categoryCombo.setFont(ThemeConstants.FONT_BODY);
        categoryCombo.addActionListener(e -> applyFiltersAsync());
        filterCard.add(categoryCombo);

        // Season Filter
        JLabel seasonLabel = new JLabel("Season:");
        seasonLabel.setFont(ThemeConstants.FONT_BOLD);
        seasonLabel.setForeground(ThemeConstants.TEXT_PRIMARY);
        filterCard.add(seasonLabel);

        seasonCombo = new JComboBox<>(new String[]{"All Seasons"});
        seasonCombo.setFont(ThemeConstants.FONT_BODY);
        seasonCombo.addActionListener(e -> applyFiltersAsync());
        filterCard.add(seasonCombo);

        // Price Filter
        JLabel priceLabel = new JLabel("Budget:");
        priceLabel.setFont(ThemeConstants.FONT_BOLD);
        priceLabel.setForeground(ThemeConstants.TEXT_PRIMARY);
        filterCard.add(priceLabel);

        priceCombo = new JComboBox<>(new String[]{
                "Any Budget",
                "Under $300",
                "Under $500",
                "Under $800",
                "Under $1200",
                "Under $2000"
        });
        priceCombo.setFont(ThemeConstants.FONT_BODY);
        priceCombo.addActionListener(e -> applyFiltersAsync());
        filterCard.add(priceCombo);

        // Action Buttons
        searchButton = new JButton("Filter");
        searchButton.setFont(ThemeConstants.FONT_BOLD);
        searchButton.setBackground(ThemeConstants.PRIMARY_COLOR);
        searchButton.setForeground(Color.WHITE);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.addActionListener(e -> applyFiltersAsync());
        filterCard.add(searchButton);

        resetButton = new JButton("Reset");
        resetButton.setFont(ThemeConstants.FONT_BODY);
        resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetButton.addActionListener(e -> resetFilters());
        filterCard.add(resetButton);

        northContainer.add(filterCard);

        return northContainer;
    }

    /**
     * Creates the responsive scrollable grid container for package cards.
     */
    private JPanel createCenterGridPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);

        // Responsive grid panel
        gridPanel = new JPanel(new GridLayout(0, currentColumns, 18, 18));
        gridPanel.setOpaque(false);

        // Place grid at the NORTH of wrapper so it doesn't vertically stretch cards
        centerContentWrapper = new JPanel(new BorderLayout());
        centerContentWrapper.setOpaque(false);
        centerContentWrapper.add(gridPanel, BorderLayout.NORTH);

        scrollPane = new JScrollPane(centerContentWrapper);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Component listener for responsive column count on window resize
        scrollPane.getViewport().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateGridColumns();
            }
        });

        centerPanel.add(scrollPane, BorderLayout.CENTER);
        return centerPanel;
    }

    /**
     * Dynamically adjusts grid column count based on available viewport width.
     */
    private void updateGridColumns() {
        int width = scrollPane.getViewport().getWidth();
        if (width <= 0) return;

        int newCols;
        if (width >= 1020) {
            newCols = 3;
        } else if (width >= 670) {
            newCols = 2;
        } else {
            newCols = 1;
        }

        if (newCols != currentColumns) {
            currentColumns = newCols;
            gridPanel.setLayout(new GridLayout(0, currentColumns, 18, 18));
            gridPanel.revalidate();
            gridPanel.repaint();
        }
    }

    /**
     * Asynchronously loads distinct categories and seasons for filter dropdowns.
     */
    private void loadFilterOptionsAsync() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private List<String> categories = new ArrayList<>();
            private List<String> seasons = new ArrayList<>();

            @Override
            protected Void doInBackground() {
                categories = destinationDAO.findAllCategories();
                seasons = packageDAO.findAllSeasons();
                return null;
            }

            @Override
            protected void done() {
                // Populate categories
                for (String cat : categories) {
                    categoryCombo.addItem(cat);
                }
                // Populate seasons
                for (String s : seasons) {
                    seasonCombo.addItem(s);
                }
            }
        };
        worker.execute();
    }

    /**
     * Asynchronously retrieves packages matching the active filter criteria using SwingWorker.
     */
    public void applyFiltersAsync() {
        String keyword = searchField != null ? searchField.getText().trim() : "";
        String category = categoryCombo != null ? (String) categoryCombo.getSelectedItem() : "All Categories";
        String season = seasonCombo != null ? (String) seasonCombo.getSelectedItem() : "All Seasons";
        Double maxPrice = parseSelectedMaxPrice();

        // Show loading status
        showLoadingState();

        SwingWorker<List<Package>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Package> doInBackground() {
                String catParam = "All Categories".equalsIgnoreCase(category) ? null : category;
                String seasonParam = "All Seasons".equalsIgnoreCase(season) ? null : season;
                return packageDAO.searchPackages(keyword, catParam, maxPrice, seasonParam);
            }

            @Override
            protected void done() {
                try {
                    List<Package> packages = get();
                    renderPackageCards(packages);
                } catch (Exception e) {
                    showErrorState("Error connecting to database: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    /**
     * Parses the max budget from the price combo box selection.
     */
    private Double parseSelectedMaxPrice() {
        if (priceCombo == null) return null;
        String selected = (String) priceCombo.getSelectedItem();
        if (selected == null || "Any Budget".equals(selected)) {
            return null;
        }
        try {
            String cleaned = selected.replace("Under", "").replace("$", "").trim();
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Resets all search and filter inputs to their default states.
     */
    private void resetFilters() {
        if (searchField != null) searchField.setText("");
        if (categoryCombo != null) categoryCombo.setSelectedIndex(0);
        if (seasonCombo != null) seasonCombo.setSelectedIndex(0);
        if (priceCombo != null) priceCombo.setSelectedIndex(0);
        applyFiltersAsync();
    }

    /**
     * Renders package cards into the responsive grid container on the EDT.
     */
    private void renderPackageCards(List<Package> packages) {
        gridPanel.removeAll();
        centerContentWrapper.removeAll();

        if (packages == null || packages.isEmpty()) {
            resultCountLabel.setText("0 Packages Found");
            centerContentWrapper.add(createEmptyStatePanel(), BorderLayout.CENTER);
        } else {
            resultCountLabel.setText("Showing " + packages.size() + " Package" + (packages.size() > 1 ? "s" : ""));

            for (Package pkg : packages) {
                PackageCard card = new PackageCard(pkg);

                // Wire up callbacks for team members
                card.setOnViewDetailsListener(p -> {
                    if (onPackageSelectedListener != null) {
                        onPackageSelectedListener.accept(p);
                    }
                });

                card.setOnBookNowListener(p -> {
                    if (onBookPackageListener != null) {
                        onBookPackageListener.accept(p);
                    }
                });

                gridPanel.add(card);
            }

            centerContentWrapper.add(gridPanel, BorderLayout.NORTH);
        }

        centerContentWrapper.revalidate();
        centerContentWrapper.repaint();
    }

    /**
     * Displays a clean loading state indicator while background query executes.
     */
    private void showLoadingState() {
        resultCountLabel.setText("Searching...");
        centerContentWrapper.removeAll();

        JPanel loadingPanel = new JPanel(new GridBagLayout());
        loadingPanel.setOpaque(false);
        loadingPanel.setBorder(new EmptyBorder(50, 20, 50, 20));

        JLabel loadingLabel = new JLabel("⏳ Loading travel packages from database...");
        loadingLabel.setFont(ThemeConstants.FONT_HEADER);
        loadingLabel.setForeground(ThemeConstants.TEXT_MUTED);
        loadingPanel.add(loadingLabel);

        centerContentWrapper.add(loadingPanel, BorderLayout.CENTER);
        centerContentWrapper.revalidate();
        centerContentWrapper.repaint();
    }

    /**
     * Displays an empty state panel when no packages match the selected criteria.
     */
    private JPanel createEmptyStatePanel() {
        JPanel emptyPanel = new JPanel();
        emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
        emptyPanel.setOpaque(false);
        emptyPanel.setBorder(new EmptyBorder(60, 20, 60, 20));

        JLabel iconLabel = new JLabel("🧭", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("No Packages Found", SwingConstants.CENTER);
        titleLabel.setFont(ThemeConstants.FONT_HEADER);
        titleLabel.setForeground(ThemeConstants.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><center>We couldn't find any packages matching your search filters.<br>Try choosing a different category or clearing search terms.</center></html>", SwingConstants.CENTER);
        descLabel.setFont(ThemeConstants.FONT_BODY);
        descLabel.setForeground(ThemeConstants.TEXT_MUTED);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton clearBtn = new JButton("Reset All Filters");
        clearBtn.setFont(ThemeConstants.FONT_BOLD);
        clearBtn.setBackground(ThemeConstants.PRIMARY_COLOR);
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> resetFilters());

        emptyPanel.add(iconLabel);
        emptyPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        emptyPanel.add(titleLabel);
        emptyPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        emptyPanel.add(descLabel);
        emptyPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        emptyPanel.add(clearBtn);

        return emptyPanel;
    }

    /**
     * Displays an error message panel when database retrieval encounters an issue.
     */
    private void showErrorState(String errorMsg) {
        resultCountLabel.setText("Database Offline");
        centerContentWrapper.removeAll();

        JPanel errorPanel = new JPanel();
        errorPanel.setLayout(new BoxLayout(errorPanel, BoxLayout.Y_AXIS));
        errorPanel.setOpaque(false);
        errorPanel.setBorder(new EmptyBorder(60, 20, 60, 20));

        JLabel errorIcon = new JLabel("⚠️", SwingConstants.CENTER);
        errorIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        errorIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel errorTitle = new JLabel("Database Connection Error", SwingConstants.CENTER);
        errorTitle.setFont(ThemeConstants.FONT_HEADER);
        errorTitle.setForeground(ThemeConstants.DANGER_COLOR);
        errorTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel errorDesc = new JLabel("<html><center>" + errorMsg + "<br>Please ensure MySQL is running on localhost:3306.</center></html>", SwingConstants.CENTER);
        errorDesc.setFont(ThemeConstants.FONT_BODY);
        errorDesc.setForeground(ThemeConstants.TEXT_MUTED);
        errorDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton retryBtn = new JButton("Retry Query");
        retryBtn.setFont(ThemeConstants.FONT_BOLD);
        retryBtn.setBackground(ThemeConstants.PRIMARY_COLOR);
        retryBtn.setForeground(Color.WHITE);
        retryBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        retryBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        retryBtn.addActionListener(e -> applyFiltersAsync());

        errorPanel.add(errorIcon);
        errorPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        errorPanel.add(errorTitle);
        errorPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        errorPanel.add(errorDesc);
        errorPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        errorPanel.add(retryBtn);

        centerContentWrapper.add(errorPanel, BorderLayout.CENTER);
        centerContentWrapper.revalidate();
        centerContentWrapper.repaint();
    }

    // Callbacks & Integration Hooks

    public void setOnPackageSelectedListener(Consumer<Package> listener) {
        this.onPackageSelectedListener = listener;
    }

    public void setOnBookPackageListener(Consumer<Package> listener) {
        this.onBookPackageListener = listener;
    }

    /**
     * Standalone runner for testing and previewing the CatalogPanel independently.
     */
    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Personalized Tourism Management System - Package Catalog");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1040, 720);
            frame.setLocationRelativeTo(null);

            CatalogPanel catalogPanel = new CatalogPanel();
            frame.setContentPane(catalogPanel);
            frame.setVisible(true);
        });
    }
}
