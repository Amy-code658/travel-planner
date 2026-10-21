package com.tourism.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.tourism.model.Booking;
import com.tourism.model.ItineraryDay;
import com.tourism.model.Package;
import com.tourism.model.User;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Builds the printable itinerary and the booking invoice as PDF files.
 */
public class PDFExportService {

    private static final String CURRENCY = "Rs. ";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final Color HEADER_COLOR = new Color(0x2E86DE);

    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
    private static final Font SUBTITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13);
    private static final Font BODY_FONT = FontFactory.getFont(FontFactory.HELVETICA, 11);
    private static final Font HEADER_CELL_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);

    public void exportItineraryPDF(Package pkg, List<ItineraryDay> days, File target)
            throws IOException {
        try (FileOutputStream out = new FileOutputStream(target)) {
            Document document = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("Travel Itinerary", TITLE_FONT));
            document.add(new Paragraph(pkg.getTitle(), SUBTITLE_FONT));
            document.add(new Paragraph("Duration: " + pkg.getDurationDays() + " days", BODY_FONT));
            document.add(new Paragraph(" "));

            // One row per day, in the order the DAO returned them.
            PdfPTable table = new PdfPTable(new float[] {1f, 3f, 6f});
            table.setWidthPercentage(100);
            addHeaderCell(table, "Day");
            addHeaderCell(table, "Plan");
            addHeaderCell(table, "Activities");
            for (ItineraryDay day : days) {
                addBodyCell(table, String.valueOf(day.getDayNumber()));
                addBodyCell(table, day.getTitle());
                addBodyCell(table, day.getActivityDescription());
            }
            document.add(table);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Generated on " + LocalDate.now().format(DATE_FORMAT), BODY_FONT));
            document.close();
        } catch (DocumentException ex) {
            throw new IOException("Could not create itinerary PDF", ex);
        }
    }

    public void exportInvoicePDF(Booking booking, User user, File target) throws IOException {
        try (FileOutputStream out = new FileOutputStream(target)) {
            Document document = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("Booking Confirmation & Invoice", TITLE_FONT));
            document.add(new Paragraph("Invoice No: INV-" + booking.getBookingId(), BODY_FONT));
            document.add(new Paragraph("Issued on: " + LocalDate.now().format(DATE_FORMAT), BODY_FONT));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Customer", SUBTITLE_FONT));
            document.add(new Paragraph(user.getFullName() + "  |  " + user.getEmail(), BODY_FONT));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(new float[] {4f, 6f});
            table.setWidthPercentage(100);
            addHeaderCell(table, "Detail");
            addHeaderCell(table, "Value");
            addRow(table, "Booking ID", String.valueOf(booking.getBookingId()));
            addRow(table, "Package ID", String.valueOf(booking.getPackageId()));
            addRow(table, "Travel date", booking.getTravelDate().format(DATE_FORMAT));
            addRow(table, "Guests", String.valueOf(booking.getNumberOfGuests()));
            addRow(table, "Status", String.valueOf(booking.getBookingStatus()));
            addRow(table, "Total paid", money(booking.getTotalCost()));
            if (booking.getRefundAmount() > 0) {
                addRow(table, "Refund issued", money(booking.getRefundAmount()));
            }
            document.add(table);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Thank you for booking with us.", BODY_FONT));
            document.close();
        } catch (DocumentException ex) {
            throw new IOException("Could not create invoice PDF", ex);
        }
    }

    private String money(Number amount) {
        return CURRENCY + String.format("%.2f", amount.doubleValue());
    }

    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_CELL_FONT));
        cell.setBackgroundColor(HEADER_COLOR);
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
    }

    private void addBodyCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text == null ? "" : text, BODY_FONT));
        cell.setPadding(6);
        table.addCell(cell);
    }

    private void addRow(PdfPTable table, String label, String value) {
        addBodyCell(table, label);
        addBodyCell(table, value);
    }
}
