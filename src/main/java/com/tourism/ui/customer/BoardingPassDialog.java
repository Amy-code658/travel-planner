package com.tourism.ui.customer;

import com.tourism.config.ThemeConstants;
import com.tourism.model.Booking;
import com.tourism.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Path2D;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Visual Digital Boarding Pass & Travel Voucher dialog.
 * Renders an airline/expedition ticket styled modal complete with
 * route codes, flight numbers, gate, seat assignment, perforation cutouts,
 * and a realistic rendered barcode.
 */
public class BoardingPassDialog extends JDialog {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final Booking booking;
    private final User customer;

    public BoardingPassDialog(Window owner, Booking booking, User customer) {
        super(owner, "Digital Boarding Pass - Wanderlust Expeditions", ModalityType.APPLICATION_MODAL);
        this.booking = booking;
        this.customer = customer;

        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(780, 480);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBackground(new Color(241, 245, 249));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // The Custom Boarding Pass Card
        BoardingPassCard passCard = new BoardingPassCard(booking, customer);
        contentPanel.add(passCard, BorderLayout.CENTER);

        // Action Buttons at Bottom
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        JButton printBtn = new JButton("🖨️ Print Pass / Voucher");
        printBtn.setFont(ThemeConstants.FONT_BOLD);
        printBtn.setBackground(ThemeConstants.PRIMARY_COLOR);
        printBtn.setForeground(Color.WHITE);
        printBtn.setFocusPainted(false);
        printBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        printBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Pass successfully sent to system printer / digital wallet!\n"
                            + "Booking Ref: WL-" + booking.getBookingId() + "X9\n"
                            + "Passenger: " + (customer != null ? customer.getFullName() : "Valued Traveler"),
                    "Digital Pass Ready",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(ThemeConstants.FONT_BOLD);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());

        actionPanel.add(printBtn);
        actionPanel.add(closeBtn);
        contentPanel.add(actionPanel, BorderLayout.SOUTH);

        add(contentPanel);
    }

    /**
     * Custom component rendering a realistic airline boarding pass.
     */
    public static class BoardingPassCard extends JPanel {

        private final Booking booking;
        private final User customer;
        private final int bookingId;
        private final String pnr;
        private final String flightNo;
        private final String gate;
        private final String seat;
        private final String destCode;

        public BoardingPassCard(Booking booking, User customer) {
            this.booking = booking;
            this.customer = customer;
            this.setOpaque(false);
            this.setPreferredSize(new Dimension(740, 360));

            // Generate deterministic but realistic flight details based on booking ID
            this.bookingId = booking != null ? booking.getBookingId() : 101;
            this.pnr = "WL" + (1000 + (bookingId * 37) % 9000);
            this.flightNo = "WL-" + (200 + (bookingId * 13) % 700);
            this.gate = "G-" + (char) ('A' + (bookingId % 6)) + (1 + (bookingId % 9));
            this.seat = (12 + (bookingId % 18)) + "" + (char) ('A' + (bookingId % 4));
            this.destCode = resolveAirportCode(booking != null ? booking.getPackageTitle() : "");
        }

        private String resolveAirportCode(String title) {
            if (title == null) return "WLD";
            String t = title.toLowerCase();
            if (t.contains("bali")) return "DPS";
            if (t.contains("alps") || t.contains("swiss")) return "GVA";
            if (t.contains("kyoto") || t.contains("japan")) return "KIX";
            if (t.contains("goa")) return "GOI";
            if (t.contains("paris") || t.contains("france")) return "CDG";
            if (t.contains("manali") || t.contains("kullu")) return "KUU";
            if (t.contains("shimla")) return "SLV";
            if (t.contains("kerala")) return "COK";
            if (t.contains("vietnam")) return "HAN";
            if (t.contains("maldives")) return "MLE";
            if (t.contains("egypt") || t.contains("cairo")) return "CAI";
            return "WLD";
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int splitX = w - 210; // Stub boundary

            // Draw white card background
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, w, h, 20, 20);

            // Card shadow / outline
            g2.setColor(new Color(218, 225, 233));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 20, 20);

            // 1. Top Header Banner across whole pass
            GradientPaint headerGrad = new GradientPaint(
                    0, 0, new Color(27, 42, 74),
                    w, 0, new Color(41, 128, 185)
            );
            g2.setPaint(headerGrad);
            // Header with top-rounded corners
            Path2D headerPath = new Path2D.Double();
            headerPath.moveTo(0, 20);
            headerPath.quadTo(0, 0, 20, 0);
            headerPath.lineTo(w - 20, 0);
            headerPath.quadTo(w, 0, w, 20);
            headerPath.lineTo(w, 54);
            headerPath.lineTo(0, 54);
            headerPath.closePath();
            g2.fill(headerPath);

            // Header Text: Left side
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
            g2.drawString("✈️  WANDERLUST AIRWAYS & EXPEDITIONS", 22, 33);

            // Header Text: Right side (Stub)
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.drawString("BOARDING PASS", splitX + 22, 33);

            // 2. Perforation cutouts and dashed divider between Main Pass and Stub
            int notchRadius = 14;
            g2.setColor(new Color(241, 245, 249)); // Match dialog background
            g2.fillOval(splitX - notchRadius, -notchRadius, notchRadius * 2, notchRadius * 2);
            g2.fillOval(splitX - notchRadius, h - notchRadius, notchRadius * 2, notchRadius * 2);

            // Dashed perforation line
            Stroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{6}, 0);
            g2.setStroke(dashed);
            g2.setColor(new Color(189, 195, 199));
            g2.drawLine(splitX, 16, splitX, h - 16);
            g2.setStroke(new BasicStroke(1));

            // ================= MAIN PASS SECTION (LEFT) =================
            String passengerName = customer != null && customer.getFullName() != null
                    ? customer.getFullName().toUpperCase() : "JOHN DOE";
            String travelDate = booking != null && booking.getTravelDate() != null
                    ? booking.getTravelDate().format(DATE_FMT) : "15 NOV 2026";
            String pkgTitle = booking != null && booking.getPackageTitle() != null
                    ? booking.getPackageTitle() : "Global Tour";
            int guests = booking != null ? booking.getNumberOfGuests() : 1;

            // Route Banner: DEL/BOM -> DEST
            g2.setColor(new Color(44, 62, 80));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
            g2.drawString("DEL", 25, 100);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(new Color(127, 140, 141));
            g2.drawString("NEW DELHI, IND", 25, 116);

            // Flight Arrow & Icon
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2.setColor(ThemeConstants.PRIMARY_COLOR);
            g2.drawString("✈️ ─────────────── ➔", 140, 98);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g2.setColor(new Color(52, 152, 219));
            g2.drawString("NON-STOP EXPEDITION", 175, 116);

            // Destination Airport Code
            g2.setColor(new Color(44, 62, 80));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
            g2.drawString(destCode, 370, 100);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(new Color(127, 140, 141));
            String destSubtitle = pkgTitle.length() > 20 ? pkgTitle.substring(0, 20) + "..." : pkgTitle;
            g2.drawString(destSubtitle.toUpperCase(), 370, 116);

            // Horizontal separator
            g2.setColor(new Color(236, 240, 241));
            g2.drawLine(25, 134, splitX - 25, 134);

            // Field Blocks: Row 1
            drawMetaField(g2, "PASSENGER NAME", passengerName, 25, 160);
            drawMetaField(g2, "BOOKING REF / PNR", pnr, 210, 160);
            drawMetaField(g2, "CABIN CLASS", "FIRST / EXECUTIVE", 370, 160);

            // Field Blocks: Row 2
            drawMetaField(g2, "TRAVEL DATE", travelDate, 25, 215);
            drawMetaField(g2, "FLIGHT NO", flightNo, 210, 215);
            drawMetaField(g2, "GATE", gate, 370, 215);

            // Field Blocks: Row 3
            drawMetaField(g2, "BOARDING TIME", "07:45 AM", 25, 270);
            drawMetaField(g2, "SEAT(S)", seat + " (" + guests + " Pax)", 210, 270);
            drawMetaField(g2, "STATUS", "CONFIRMED • PRIORITY", 370, 270);

            // Bottom note
            g2.setColor(new Color(149, 165, 166));
            g2.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            g2.drawString("Notice: Please arrive at gate at least 45 minutes prior to departure with valid government ID.", 25, 335);

            // ================= STUB SECTION (RIGHT) =================
            int stubLeft = splitX + 20;

            drawMetaField(g2, "PASSENGER", passengerName.length() > 14 ? passengerName.substring(0, 14) + "." : passengerName, stubLeft, 85);
            drawMetaField(g2, "FLIGHT", flightNo, stubLeft, 135);
            drawMetaField(g2, "GATE", gate, stubLeft + 85, 135);
            drawMetaField(g2, "SEAT", seat, stubLeft, 185);
            drawMetaField(g2, "DEST", destCode, stubLeft + 85, 185);

            // Simulated Barcode
            paintSimulatedBarcode(g2, stubLeft, 225, 165, 65, pnr + bookingId);

            g2.setColor(new Color(127, 140, 141));
            g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
            g2.drawString("* " + pnr + "-" + flightNo + " *", stubLeft + 15, 305);

            g2.dispose();
        }

        private void drawMetaField(Graphics2D g2, String label, String value, int x, int y) {
            g2.setColor(new Color(127, 140, 141));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.drawString(label, x, y);

            g2.setColor(new Color(33, 47, 61));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g2.drawString(value, x, y + 18);
        }

        private void paintSimulatedBarcode(Graphics2D g2, int x, int y, int width, int height, String seedStr) {
            g2.setColor(Color.WHITE);
            g2.fillRect(x, y, width, height);

            Random r = new Random(seedStr.hashCode());
            int curX = x + 6;
            int endX = x + width - 6;

            g2.setColor(new Color(30, 39, 46));
            while (curX < endX) {
                int barW = 1 + r.nextInt(3);
                int gapW = 1 + r.nextInt(3);
                g2.fillRect(curX, y + 4, barW, height - 8);
                curX += barW + gapW;
            }
        }
    }
}
