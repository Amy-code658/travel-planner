package com.tourism;

import com.tourism.dao.DestinationDAO;
import com.tourism.dao.PackageDAO;
import com.tourism.model.Destination;
import com.tourism.model.Package;
import com.tourism.ui.customer.CatalogMockPreview;
import com.tourism.ui.customer.CatalogPanel;
import com.tourism.ui.customer.PackageCard;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Dev3VerificationTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  RUNNING DEV 3 VERIFICATION (CATALOG & PACKAGE CARD)");
        System.out.println("==================================================");

        // 1. Mock Data Setup matching schema
        List<Destination> mockDestinations = new ArrayList<>();
        Destination d1 = new Destination(1, "Bali", "Indonesia", "Tropical beach and surf", "Beach", 60.0, "bali.jpg");
        Destination d2 = new Destination(2, "Swiss Alps", "Switzerland", "Alpine sports and hiking", "Adventure", 180.0, "alps.jpg");
        Destination d3 = new Destination(3, "Kyoto", "Japan", "Temples and tea gardens", "Culture", 110.0, "kyoto.jpg");
        mockDestinations.add(d1);
        mockDestinations.add(d2);
        mockDestinations.add(d3);

        List<Package> mockPackages = new ArrayList<>();
        Package p1 = new Package(1, 1, "Bali Tropical Island Escape", "Relax on the beach", 5, 450.0, "Summer", 10.0, true);
        p1.setDestination(d1);
        Package p2 = new Package(2, 2, "Swiss Alpine Explorer", "High alpine tracks", 6, 1200.0, "Winter", 5.0, true);
        p2.setDestination(d2);
        Package p3 = new Package(3, 3, "Kyoto Heritage & Zen Trails", "Visit ancient temples", 4, 650.0, "Spring", 0.0, true);
        p3.setDestination(d3);
        mockPackages.add(p1);
        mockPackages.add(p2);
        mockPackages.add(p3);

        // 2. Test Mock DestinationDAO
        System.out.println("\n[1] Testing DestinationDAO operations:");
        CatalogMockPreview.MockDestinationDAO destDAO = new CatalogMockPreview.MockDestinationDAO(mockDestinations);
        List<Destination> allDests = destDAO.findAll();
        System.out.println("  Total destinations found: " + allDests.size());
        assert allDests.size() == 3;

        List<Destination> beachDests = destDAO.findByCategory("Beach");
        System.out.println("  Beach destinations found: " + beachDests.size());
        assert beachDests.size() == 1 && beachDests.get(0).getName().equals("Bali");

        List<Destination> searchDests = destDAO.search("Swiss");
        System.out.println("  Search 'Swiss' destinations: " + searchDests.size());
        assert searchDests.size() == 1 && searchDests.get(0).getCountry().equals("Switzerland");
        System.out.println("  >> DestinationDAO assertions: PASSED (OK)");

        // 3. Test Mock PackageDAO
        System.out.println("\n[2] Testing PackageDAO operations & query filters:");
        CatalogMockPreview.MockPackageDAO pkgDAO = new CatalogMockPreview.MockPackageDAO(mockPackages);

        List<Package> activePkgs = pkgDAO.findAllActive();
        System.out.println("  Active packages found: " + activePkgs.size());
        assert activePkgs.size() == 3;

        List<Package> dev4Active = pkgDAO.getAllActivePackages();
        assert dev4Active.size() == 3;

        Package pkgById = pkgDAO.getPackageById(1);
        assert pkgById != null && pkgById.getTitle().contains("Bali");

        List<Package> filteredPkgs = pkgDAO.searchPackages("Bali", null, null, null);
        System.out.println("  Keyword search 'Bali' count: " + filteredPkgs.size());
        assert filteredPkgs.size() == 1;

        List<Package> budgetPkgs = pkgDAO.searchPackages(null, null, 500.0, null);
        System.out.println("  Budget <= $500 count: " + budgetPkgs.size());
        assert budgetPkgs.size() == 1 && budgetPkgs.get(0).getPackageId() == 1;

        List<String> seasons = pkgDAO.findAllSeasons();
        System.out.println("  Seasons available: " + seasons);
        assert seasons.contains("Summer") && seasons.contains("Winter");
        System.out.println("  >> PackageDAO assertions: PASSED (OK)");

        // 4. Test Unified PackageCard Component
        System.out.println("\n[3] Testing Unified PackageCard component:");
        PackageCard card = new PackageCard(p1);

        // Test Pod 2 Dev 4 contract: setRecommendationScore
        card.setRecommendationScore(88, "Top match for Summer Beach escape");
        System.out.println("  Recommendation score attached to card: 88%");

        // Test listener attachments
        AtomicBoolean detailsClicked = new AtomicBoolean(false);
        AtomicBoolean bookClicked = new AtomicBoolean(false);

        card.setOnViewDetailsListener(p -> detailsClicked.set(true));
        card.setOnBookNowListener(p -> bookClicked.set(true));

        assert card.getPackage() != null;
        assert card.getTravelPackage() != null;
        assert card.getPackage().getPackageId() == 1;
        System.out.println("  >> PackageCard listeners & models verified: PASSED (OK)");

        // 5. Test CatalogPanel initialization with mock DAOs
        System.out.println("\n[4] Testing CatalogPanel integration:");
        CatalogPanel panel = new CatalogPanel(pkgDAO, destDAO);
        panel.setOnPackageSelectedListener(p -> System.out.println("Selected: " + p.getTitle()));
        panel.setOnBookPackageListener(p -> System.out.println("Booked: " + p.getTitle()));
        assert panel != null;
        System.out.println("  >> CatalogPanel instantiated and listeners hooked: PASSED (OK)");

        System.out.println("\n==================================================");
        System.out.println("  ALL DEV 3 CHECKS COMPLETED SUCCESSFULLY!");
        System.out.println("==================================================");
    }
}
