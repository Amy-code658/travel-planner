package com.tourism;

import com.formdev.flatlaf.FlatLightLaf;
import com.tourism.dao.UserDAO;
import com.tourism.model.User;
import com.tourism.ui.admin.AdminMainFrame;
import com.tourism.ui.auth.LoginFrame;
import com.tourism.ui.customer.CustomerMainFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class RenderUIScreenshots {
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        FlatLightLaf.setup();

        String outDir = "/home/amy/.gemini/antigravity/brain/8fcc3241-130b-4d08-ae76-8c65598edd0c/";
        new File(outDir).mkdirs();

        try {
            // 1. Render Login Frame
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.addNotify();
            loginFrame.setSize(480, 560);
            loginFrame.validate();
            saveComponentImage(loginFrame.getContentPane(), new File(outDir + "ui_login_frame.png"));
            loginFrame.dispose();

            // 2. Render Customer Main Frame (Catalog)
            UserDAO userDAO = new UserDAO();
            User customer = userDAO.findByEmail("john@gmail.com");
            if (customer == null) {
                customer = new User(2, "John Doe", "john@gmail.com", "", "CUSTOMER", "9876543210");
            }

            CustomerMainFrame customerFrame = new CustomerMainFrame(customer);
            customerFrame.addNotify();
            customerFrame.setSize(1100, 750);
            customerFrame.validate();
            Thread.sleep(800); // Allow async worker or layout to finish
            saveComponentImage(customerFrame.getContentPane(), new File(outDir + "ui_customer_catalog.png"));

            // Switch to Recommendations Tab
            JTabbedPane tabbedPane = findTabbedPane(customerFrame);
            if (tabbedPane != null && tabbedPane.getTabCount() > 1) {
                tabbedPane.setSelectedIndex(1);
                customerFrame.validate();
                Thread.sleep(600);
                saveComponentImage(customerFrame.getContentPane(), new File(outDir + "ui_customer_recommendations.png"));
            }

            // 3. Render Itinerary Dialog for Package 1 (Bali Tropical Escape)
            com.tourism.dao.PackageDAO packageDAO = new com.tourism.dao.PackageDAO();
            com.tourism.model.Package baliPkg = packageDAO.findById(1).orElse(null);
            if (baliPkg != null) {
                com.tourism.service.ItineraryService itineraryService = new com.tourism.service.ItineraryService();
                java.util.List<com.tourism.model.ItineraryDay> days = itineraryService.getItineraryForPackage(baliPkg);
                com.tourism.service.ItineraryService.ItemizedCost cost = itineraryService.calculateCostBreakdown(baliPkg, 1, baliPkg.getDurationDays());
                com.tourism.ui.customer.ItineraryDialog itinDialog = new com.tourism.ui.customer.ItineraryDialog(customerFrame, baliPkg, days, cost);
                itinDialog.addNotify();
                itinDialog.setSize(750, 680);
                itinDialog.validate();
                Thread.sleep(500);
                saveComponentImage(itinDialog.getContentPane(), new File(outDir + "ui_itinerary_dialog.png"));
                itinDialog.dispose();
            }

            customerFrame.dispose();

            // 3. Render Admin Main Frame (Reports & Analytics)
            User admin = userDAO.findByEmail("admin@tourism.com");
            if (admin == null) {
                admin = new User(1, "System Administrator", "admin@tourism.com", "", "ADMIN", "9998887770");
            }
            AdminMainFrame adminFrame = new AdminMainFrame(admin);
            adminFrame.addNotify();
            adminFrame.setSize(1100, 750);
            adminFrame.validate();
            Thread.sleep(800);
            saveComponentImage(adminFrame.getContentPane(), new File(outDir + "ui_admin_reports.png"));
            adminFrame.dispose();

            System.out.println("✅ All UI screenshots captured successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveComponentImage(Container container, File dest) throws Exception {
        container.validate();
        int width = container.getWidth();
        int height = container.getHeight();
        if (width <= 0) width = 1000;
        if (height <= 0) height = 700;

        container.setSize(width, height);
        container.doLayout();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        container.printAll(g2);
        g2.dispose();

        ImageIO.write(image, "png", dest);
        System.out.println("Saved " + dest.getName() + " (" + width + "x" + height + ")");
    }

    private static JTabbedPane findTabbedPane(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof JTabbedPane) {
                return (JTabbedPane) c;
            } else if (c instanceof Container) {
                JTabbedPane found = findTabbedPane((Container) c);
                if (found != null) return found;
            }
        }
        return null;
    }
}
