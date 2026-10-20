package com.tourism.ui.customer;

import com.tourism.model.Booking;
import com.tourism.model.Package;
import com.tourism.service.BookingException;
import com.tourism.service.BookingService;
import com.tourism.service.CardPayment;
import com.tourism.service.PaymentMethod;
import com.tourism.service.UpiPayment;

import javax.swing.*;
import java.awt.*;
import java.time.DateTimeException;
import java.time.LocalDate;

/**
 * Booking window: choose date and guests, see the cost, then do a simulated payment.
 * Open it from the catalog with:  new BookingDialog(frame, pkg, userId).setVisible(true);
 * Concepts: inheritance (extends JDialog), layout managers, event handling (listeners),
 * multithreading (payment runs in a separate thread), interface + polymorphism (PaymentMethod).
 */
public class BookingDialog extends JDialog {

    private final Package pkg;
    private final int userId;
    private final BookingService bookingService = new BookingService();

    private JComboBox<Integer> dayBox;
    private JComboBox<Integer> monthBox;
    private JComboBox<Integer> yearBox;
    private JSpinner guestSpinner;
    private JLabel totalLabel;
    private JLabel statusLabel;

    private JComboBox<String> methodBox;
    private final CardLayout cardLayout = new CardLayout();
    private JPanel paymentCards;
    private JTextField holderField;
    private JTextField cardField;
    private JTextField expiryField;
    private JPasswordField cvvField;
    private JTextField upiField;

    private JButton payButton;
    private JButton cancelButton;

    private Booking confirmedBooking;      // stays null if the customer closes without paying

    public BookingDialog(Frame owner, Package pkg, int userId) {
        super(owner, "Book: " + pkg.getTitle(), true);
        this.pkg = pkg;
        this.userId = userId;

        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(buildSummaryPanel(), BorderLayout.NORTH);
        add(buildFormPanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        updateTotal();
        getRootPane().setDefaultButton(payButton);
        pack();
        setLocationRelativeTo(owner);
    }

    /** The booking saved by this dialog, or null if nothing was booked. The caller can use it to refresh. */
    public Booking getConfirmedBooking() {
        return confirmedBooking;
    }

    // ---------- building the screen ----------

    private JPanel buildSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 4));

        JLabel title = new JLabel(pkg.getTitle());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        panel.add(title);
        panel.add(new JLabel("Duration: " + pkg.getDurationDays() + " days"));
        panel.add(new JLabel(String.format("Price per person: Rs. %.2f", pkg.getBasePrice())));
        panel.add(new JLabel(String.format("Discount: %.0f%%", pkg.getDiscountPercentage())));
        return panel;
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());

        // Travel date: three combo boxes (day / month / year)
        LocalDate defaultDate = LocalDate.now().plusDays(7);
        int thisYear = LocalDate.now().getYear();

        dayBox = new JComboBox<>();
        for (int d = 1; d <= 31; d++) {
            dayBox.addItem(d);
        }
        monthBox = new JComboBox<>();
        for (int m = 1; m <= 12; m++) {
            monthBox.addItem(m);
        }
        yearBox = new JComboBox<>();
        for (int y = thisYear; y <= thisYear + 2; y++) {
            yearBox.addItem(y);
        }
        dayBox.setSelectedItem(defaultDate.getDayOfMonth());
        monthBox.setSelectedItem(defaultDate.getMonthValue());
        yearBox.setSelectedItem(defaultDate.getYear());

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        datePanel.add(dayBox);
        datePanel.add(monthBox);
        datePanel.add(yearBox);
        datePanel.add(new JLabel("(DD / MM / YYYY)"));
        addRow(panel, 0, "Travel date", datePanel);

        // Guest selector
        guestSpinner = new JSpinner(new SpinnerNumberModel(1, 1, BookingService.MAX_GUESTS, 1));
        guestSpinner.addChangeListener(e -> updateTotal());
        addRow(panel, 1, "Guests", guestSpinner);

        totalLabel = new JLabel();
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 15f));
        addRow(panel, 2, "Total cost", totalLabel);

        // Payment section
        methodBox = new JComboBox<>(new String[]{"Card", "UPI"});
        methodBox.addActionListener(e -> cardLayout.show(paymentCards, (String) methodBox.getSelectedItem()));
        addRow(panel, 3, "Pay using", methodBox);

        paymentCards = new JPanel(cardLayout);
        paymentCards.add(buildCardFields(), "Card");
        paymentCards.add(buildUpiFields(), "UPI");

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(8, 4, 4, 4);
        panel.add(paymentCards, c);
        return panel;
    }

    private JPanel buildCardFields() {
        JPanel panel = new JPanel(new GridBagLayout());
        holderField = new JTextField(18);
        cardField = new JTextField(18);
        expiryField = new JTextField(6);
        cvvField = new JPasswordField(4);

        cardField.putClientProperty("JTextField.placeholderText", "16 digits");
        expiryField.putClientProperty("JTextField.placeholderText", "MM/YY");

        addRow(panel, 0, "Name on card", holderField);
        addRow(panel, 1, "Card number", cardField);
        addRow(panel, 2, "Expiry", expiryField);
        addRow(panel, 3, "CVV", cvvField);
        return panel;
    }

    private JPanel buildUpiFields() {
        JPanel panel = new JPanel(new GridBagLayout());
        upiField = new JTextField(18);
        upiField.putClientProperty("JTextField.placeholderText", "name@upi");
        addRow(panel, 0, "UPI id", upiField);
        return panel;
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        statusLabel = new JLabel(" ");
        cancelButton = new JButton("Close");
        payButton = new JButton("Pay");

        cancelButton.addActionListener(e -> dispose());
        payButton.addActionListener(e -> onPay());

        panel.add(statusLabel);
        panel.add(cancelButton);
        panel.add(payButton);
        return panel;
    }

    // Puts "label  field" on one row of a GridBagLayout panel
    private void addRow(JPanel panel, int row, String labelText, JComponent field) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = row;
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0;
        panel.add(new JLabel(labelText), c);

        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, c);
    }

    // ---------- behaviour ----------

    private void updateTotal() {
        int guests = (Integer) guestSpinner.getValue();
        double total = bookingService.calculateTotal(pkg.getBasePrice(), guests, pkg.getDiscountPercentage());
        totalLabel.setText(String.format("Rs. %.2f", total));
        payButton.setText(String.format("Pay Rs. %.2f", total));
    }

    // Reads the three combo boxes and builds a LocalDate (31 Feb throws DateTimeException)
    private LocalDate readTravelDate() throws BookingException {
        int day = (Integer) dayBox.getSelectedItem();
        int month = (Integer) monthBox.getSelectedItem();
        int year = (Integer) yearBox.getSelectedItem();
        try {
            return LocalDate.of(year, month, day);
        } catch (DateTimeException e) {
            throw new BookingException("That date does not exist. Please pick a valid date.");
        }
    }

    // Polymorphism: the rest of the code only sees the PaymentMethod type
    private PaymentMethod buildPayment() {
        if ("Card".equals(methodBox.getSelectedItem())) {
            return new CardPayment(holderField.getText(), cardField.getText(),
                    expiryField.getText(), new String(cvvField.getPassword()));
        }
        return new UpiPayment(upiField.getText());
    }

    private void onPay() {
        try {
            LocalDate travelDate = readTravelDate();
            int guests = (Integer) guestSpinner.getValue();
            PaymentMethod payment = buildPayment();

            // Check everything first so mistakes are shown immediately
            bookingService.validateBookingRequest(travelDate, guests, payment);
            startPayment(travelDate, guests, payment);

        } catch (BookingException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Please check your details", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Simulated payment. It runs in a separate thread so the window does not freeze
     * while we "wait for the bank". Swing components must only be changed on the
     * Swing thread, so results go back through SwingUtilities.invokeLater().
     */
    private void startPayment(LocalDate travelDate, int guests, PaymentMethod payment) {
        payButton.setEnabled(false);
        statusLabel.setText("Processing payment...");

        Thread worker = new Thread(() -> {
            try {
                Thread.sleep(1500);                                   // pretend to contact the bank
                Booking booking = bookingService.bookTrip(userId, pkg.getPackageId(),
                        pkg.getBasePrice(), pkg.getDiscountPercentage(), travelDate, guests, payment);
                SwingUtilities.invokeLater(() -> showSuccess(booking, payment));
            } catch (BookingException ex) {
                SwingUtilities.invokeLater(() -> showFailure(ex.getMessage()));
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                SwingUtilities.invokeLater(() -> showFailure("Payment was interrupted. Please try again."));
            }
        });
        worker.start();
    }

    private void showSuccess(Booking booking, PaymentMethod payment) {
        confirmedBooking = booking;
        JOptionPane.showMessageDialog(this,
                String.format("Payment successful!%nBooking ID: %d%nAmount paid: Rs. %.2f%nPaid using: %s",
                        booking.getBookingId(), booking.getTotalCost(), payment.getDescription()),
                "Booking confirmed", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private void showFailure(String message) {
        statusLabel.setText(" ");
        payButton.setEnabled(true);
        JOptionPane.showMessageDialog(this, message, "Booking failed", JOptionPane.ERROR_MESSAGE);
    }
}
