package com.tourism.ui.customer;

import com.formdev.flatlaf.FlatLightLaf;
import com.tourism.dao.DestinationDAO;
import com.tourism.dao.PackageDAO;
import com.tourism.model.Destination;
import com.tourism.model.Package;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Standalone offline preview runner for CatalogPanel and PackageCard.
 * Uses mock in-memory DAOs pre-populated with schema seed data, allowing complete UI
 * and filter validation without requiring a live MySQL database connection.
 */
public class CatalogMockPreview {

    public static void main(String[] args) {
        // Set modern FlatLaf Look and Feel
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Personalized Tourism Management System - Catalog Preview (Offline Mock)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1060, 740);
            frame.setLocationRelativeTo(null);

            // 1. Create seed data matching tourism_schema.sql
            List<Destination> destinations = createMockDestinations();
            List<Package> packages = createMockPackages(destinations);

            // 2. Instantiate in-memory DAOs that do not connect to MySQL
            DestinationDAO mockDestinationDAO = new MockDestinationDAO(destinations);
            PackageDAO mockPackageDAO = new MockPackageDAO(packages);

            // 3. Instantiate CatalogPanel with mock DAOs
            CatalogPanel catalogPanel = new CatalogPanel(mockPackageDAO, mockDestinationDAO);

            // 4. Attach test preview callbacks clearly marked as preview simulation
            catalogPanel.setOnPackageSelectedListener(pkg -> {
                String destName = (pkg.getDestination() != null)
                        ? pkg.getDestination().getName() + ", " + pkg.getDestination().getCountry()
                        : "N/A";
                String category = (pkg.getDestination() != null) ? pkg.getDestination().getCategory() : "General";

                String message = String.format(
                        "<html><body style='width: 320px; font-family: Segoe UI;'>" +
                        "<h3 style='color: #1f618d;'>[Catalog Preview - Details Hook]</h3>" +
                        "<p><b>Package:</b> %s</p>" +
                        "<p><b>Destination:</b> %s (%s)</p>" +
                        "<p><b>Duration:</b> %d Days | <b>Season:</b> %s</p>" +
                        "<p><b>Base Price:</b> $%.2f | <b>Discount:</b> %.0f%%</p>" +
                        "<p><b>Final Price:</b> <b style='color: #27ae60;'>$%.2f</b></p><br>" +
                        "<p><b>Description:</b><br>%s</p><br>" +
                        "<p style='color: #7f8c8d; font-size: 11px;'><i>(Preview note: In production, this hook will open Dev 4's Day-wise Itinerary view.)</i></p>" +
                        "</body></html>",
                        pkg.getTitle(),
                        destName,
                        category,
                        pkg.getDurationDays(),
                        pkg.getSeason(),
                        pkg.getBasePrice(),
                        pkg.getDiscountPercentage(),
                        pkg.getFinalPrice(),
                        pkg.getDescription()
                );

                JOptionPane.showMessageDialog(frame, new JLabel(message), "Package Details Preview", JOptionPane.INFORMATION_MESSAGE);
            });

            catalogPanel.setOnBookPackageListener(pkg -> {
                String message = String.format(
                        "<html><body style='width: 300px; font-family: Segoe UI;'>" +
                        "<h3 style='color: #27ae60;'>[Catalog Preview - Booking Hook]</h3>" +
                        "<p><b>Selected Package:</b> %s</p>" +
                        "<p><b>Total Price:</b> <font color='#27ae60' size='+1'><b>$%.2f</b></font></p><br>" +
                        "<p style='color: #c0392b;'><b>NOTE: This is a preview simulation.</b><br>No actual booking was created.</p><br>" +
                        "<p style='color: #7f8c8d; font-size: 11px;'><i>(In production, this hook invokes Dev 5's BookingService & payment screen.)</i></p>" +
                        "</body></html>",
                        pkg.getTitle(),
                        pkg.getFinalPrice()
                );

                JOptionPane.showMessageDialog(frame, new JLabel(message), "Booking Preview Demo", JOptionPane.INFORMATION_MESSAGE);
            });

            frame.setContentPane(catalogPanel);
            frame.setVisible(true);
        });
    }

    private static List<Destination> createMockDestinations() {
        List<Destination> list = new ArrayList<>();
        list.add(new Destination(1, "Bali", "Indonesia", "Tropical paradise known for iconic beaches, volcanic mountains, and coral reefs.", "Beach", 60.00, "bali.jpg"));
        list.add(new Destination(2, "Swiss Alps", "Switzerland", "Magnificent alpine region perfect for hiking, skiing, and breathtaking scenic trains.", "Adventure", 180.00, "alps.jpg"));
        list.add(new Destination(3, "Kyoto", "Japan", "Ancient imperial capital famous for classical Buddhist temples, gardens, and shrines.", "Culture", 110.00, "kyoto.jpg"));
        list.add(new Destination(4, "Paris", "France", "World capital of art, fashion, gastronomy, and culture with iconic monuments.", "Luxury", 160.00, "paris.jpg"));
        list.add(new Destination(5, "Manali", "India", "High-altitude Himalayan resort town known for snow sports, pine forests, and rivers.", "Nature", 40.00, "manali.jpg"));
        list.add(new Destination(6, "Goa", "India", "Vibrant coastal state known for pristine beaches, Portuguese heritage, and nightlife.", "Beach", 45.00, "goa.jpg"));
        return list;
    }

    private static List<Package> createMockPackages(List<Destination> destinations) {
        Map<Integer, Destination> destMap = new HashMap<>();
        for (Destination d : destinations) {
            destMap.put(d.getDestinationId(), d);
        }

        List<Package> list = new ArrayList<>();
        list.add(new Package(1, 1, "Bali Tropical Island Escape", "Relax on the sands of Kuta, explore Ubud monkey forest, and visit sacred water temples.", 5, 450.00, "Summer", 10.00, true));
        list.add(new Package(2, 2, "Swiss Alpine Explorer", "Ride scenic mountain railways, hike the Matterhorn trails, and cruise Lake Geneva.", 6, 1200.00, "Winter", 5.00, true));
        list.add(new Package(3, 3, "Kyoto Heritage & Zen Trails", "Experience traditional tea ceremonies, visit the Fushimi Inari shrine and bamboo groves.", 4, 650.00, "Spring", 0.00, true));
        list.add(new Package(4, 4, "Romantic Paris Luxury Tour", "Cruise the River Seine, private tour of the Louvre, and dinner at the Eiffel Tower.", 5, 1100.00, "All Season", 15.00, true));
        list.add(new Package(5, 5, "Himalayan Adventure in Manali", "Solang Valley paragliding, river rafting, and trekking through pine valleys.", 4, 220.00, "Summer", 0.00, true));
        list.add(new Package(6, 6, "Goan Coastal & Sunset Retreat", "Beach hopping in North & South Goa, water sports, and historical church tours.", 3, 180.00, "Winter", 10.00, true));

        // Join destinations
        for (Package pkg : list) {
            pkg.setDestination(destMap.get(pkg.getDestinationId()));
        }

        return list;
    }

    /**
     * In-memory mock implementation of DestinationDAO.
     * Never establishes a MySQL connection.
     */
    public static class MockDestinationDAO extends DestinationDAO {
        private final List<Destination> destinations;

        public MockDestinationDAO(List<Destination> destinations) {
            this.destinations = new ArrayList<>(destinations);
        }

        @Override
        public List<Destination> findAll() {
            return new ArrayList<>(destinations);
        }

        @Override
        public Optional<Destination> findById(int destinationId) {
            return destinations.stream().filter(d -> d.getDestinationId() == destinationId).findFirst();
        }

        @Override
        public List<Destination> findByCategory(String category) {
            if (category == null || category.trim().isEmpty() || "All".equalsIgnoreCase(category.trim())) {
                return findAll();
            }
            return destinations.stream().filter(d -> category.equalsIgnoreCase(d.getCategory())).toList();
        }

        @Override
        public List<Destination> search(String keyword) {
            if (keyword == null || keyword.trim().isEmpty()) {
                return findAll();
            }
            String k = keyword.trim().toLowerCase();
            return destinations.stream().filter(d ->
                    (d.getName() != null && d.getName().toLowerCase().contains(k)) ||
                    (d.getCountry() != null && d.getCountry().toLowerCase().contains(k)) ||
                    (d.getDescription() != null && d.getDescription().toLowerCase().contains(k))
            ).toList();
        }

        @Override
        public List<String> findAllCategories() {
            return destinations.stream()
                    .map(Destination::getCategory)
                    .filter(c -> c != null && !c.trim().isEmpty())
                    .distinct()
                    .sorted()
                    .toList();
        }
    }

    /**
     * In-memory mock implementation of PackageDAO.
     * Never establishes a MySQL connection.
     */
    public static class MockPackageDAO extends PackageDAO {
        private final List<Package> packages;

        public MockPackageDAO(List<Package> packages) {
            this.packages = new ArrayList<>(packages);
        }

        @Override
        public List<Package> findAllActive() {
            return packages.stream().filter(Package::isActive).toList();
        }

        @Override
        public List<Package> findAll() {
            return new ArrayList<>(packages);
        }

        @Override
        public Optional<Package> findById(int packageId) {
            return packages.stream().filter(p -> p.getPackageId() == packageId).findFirst();
        }

        @Override
        public List<Package> findByDestinationId(int destinationId) {
            return packages.stream().filter(p -> p.isActive() && p.getDestinationId() == destinationId).toList();
        }

        @Override
        public List<Package> searchPackages(String keyword, String category, Double maxPrice, String season) {
            return packages.stream().filter(p -> {
                if (!p.isActive()) return false;

                // Keyword search in package title, description, destination name, and country
                if (keyword != null && !keyword.trim().isEmpty()) {
                    String k = keyword.trim().toLowerCase();
                    boolean titleMatch = p.getTitle() != null && p.getTitle().toLowerCase().contains(k);
                    boolean descMatch = p.getDescription() != null && p.getDescription().toLowerCase().contains(k);
                    boolean destNameMatch = p.getDestination() != null && p.getDestination().getName() != null && p.getDestination().getName().toLowerCase().contains(k);
                    boolean countryMatch = p.getDestination() != null && p.getDestination().getCountry() != null && p.getDestination().getCountry().toLowerCase().contains(k);

                    if (!titleMatch && !descMatch && !destNameMatch && !countryMatch) {
                        return false;
                    }
                }

                // Category filter: matches destination category or package title/description
                if (category != null && !category.trim().isEmpty() &&
                    !"All".equalsIgnoreCase(category.trim()) &&
                    !"All Categories".equalsIgnoreCase(category.trim())) {
                    String cat = category.trim().toLowerCase();
                    boolean destCatMatch = p.getDestination() != null &&
                            p.getDestination().getCategory() != null &&
                            p.getDestination().getCategory().trim().equalsIgnoreCase(cat);
                    boolean titleCatMatch = p.getTitle() != null &&
                            p.getTitle().toLowerCase().contains(cat);
                    boolean descCatMatch = p.getDescription() != null &&
                            p.getDescription().toLowerCase().contains(cat);

                    if (!destCatMatch && !titleCatMatch && !descCatMatch) {
                        return false;
                    }
                }

                // Max price filter (checks effective final discounted price)
                if (maxPrice != null && maxPrice > 0) {
                    if (p.getFinalPrice() > maxPrice) {
                        return false;
                    }
                }

                // Season filter
                if (season != null && !season.trim().isEmpty() &&
                    !"All".equalsIgnoreCase(season.trim()) &&
                    !"All Seasons".equalsIgnoreCase(season.trim())) {
                    String s = season.trim().toLowerCase();
                    if (p.getSeason() == null || !p.getSeason().trim().toLowerCase().equals(s)) {
                        return false;
                    }
                }

                return true;
            }).toList();
        }

        @Override
        public List<String> findAllSeasons() {
            return packages.stream()
                    .filter(Package::isActive)
                    .map(Package::getSeason)
                    .filter(s -> s != null && !s.trim().isEmpty())
                    .distinct()
                    .sorted()
                    .toList();
        }
    }
}
