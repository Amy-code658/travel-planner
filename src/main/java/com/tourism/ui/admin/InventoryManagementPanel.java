package com.tourism.ui.admin;

import com.tourism.config.ThemeConstants;
import com.tourism.dao.DestinationDAO;
import com.tourism.dao.PackageDAO;
import com.tourism.model.Destination;
import com.tourism.model.Package;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/** Administrator CRUD screen for destinations and travel packages. */
public class InventoryManagementPanel extends JPanel {
    private enum InventoryType { DESTINATIONS, PACKAGES }

    private final DestinationDAO destinationDAO = new DestinationDAO();
    private final PackageDAO packageDAO = new PackageDAO();
    private final JComboBox<InventoryType> typeSelector = new JComboBox<>(InventoryType.values());
    private final JTextField searchField = new JTextField(22);
    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable table = new JTable(tableModel);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
    private final JButton addButton = new JButton("Add");
    private final JButton editButton = new JButton("Edit Selected");
    private final JButton deleteButton = new JButton("Delete Selected");
    private final JButton refreshButton = new JButton("Refresh");

    public InventoryManagementPanel() {
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(ThemeConstants.PADDING_MEDIUM, ThemeConstants.PADDING_MEDIUM,
                ThemeConstants.PADDING_MEDIUM, ThemeConstants.PADDING_MEDIUM));
        setBackground(ThemeConstants.BACKGROUND);

        JLabel title = new JLabel("Inventory Management");
        title.setFont(ThemeConstants.FONT_HEADER_LARGE);
        title.setForeground(ThemeConstants.PRIMARY_DARK);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filters.setOpaque(false);
        filters.add(new JLabel("Manage:"));
        filters.add(typeSelector);
        filters.add(new JLabel("Search:"));
        filters.add(searchField);
        filters.add(refreshButton);

        JPanel header = new JPanel(new BorderLayout(0, 10));
        header.setOpaque(false);
        header.add(title, BorderLayout.NORTH);
        header.add(filters, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        table.setRowSorter(sorter);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setRowHeight(26);
        table.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        addButton.setBackground(ThemeConstants.ACCENT_COLOR);
        addButton.setForeground(Color.WHITE);
        deleteButton.setBackground(ThemeConstants.DANGER_COLOR);
        deleteButton.setForeground(Color.WHITE);
        actions.add(addButton);
        actions.add(editButton);
        actions.add(deleteButton);
        add(actions, BorderLayout.SOUTH);

        typeSelector.addActionListener(e -> loadInventory());
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void applyFilter() {
                String text = searchField.getText().trim();
                sorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
            }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
        });
        refreshButton.addActionListener(e -> loadInventory());
        addButton.addActionListener(e -> showEditor(false));
        editButton.addActionListener(e -> showEditor(true));
        deleteButton.addActionListener(e -> deleteSelected());
        loadInventory();
    }

    private InventoryType selectedType() { return (InventoryType) typeSelector.getSelectedItem(); }

    private void loadInventory() {
        InventoryType type = selectedType();
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        if (type == InventoryType.DESTINATIONS) {
            tableModel.setColumnIdentifiers(new Object[]{"ID", "Name", "Country", "Category", "Daily Cost", "Image URL"});
            for (Destination d : destinationDAO.findAll()) {
                tableModel.addRow(new Object[]{d.getDestinationId(), d.getName(), d.getCountry(), d.getCategory(), d.getEstimatedDailyCost(), d.getImageUrl()});
            }
        } else {
            tableModel.setColumnIdentifiers(new Object[]{"ID", "Title", "Destination", "Days", "Base Price", "Season", "Discount %", "Active"});
            for (Package p : packageDAO.findAllForAdmin()) {
                String destination = p.getDestination() == null ? "Destination #" + p.getDestinationId() : p.getDestination().getName();
                tableModel.addRow(new Object[]{p.getPackageId(), p.getTitle(), destination, p.getDurationDays(), p.getBasePrice(), p.getSeason(), p.getDiscountPercentage(), p.isActive()});
            }
        }
        table.setModel(tableModel);
        table.setRowSorter(sorter);
        tableModel.fireTableStructureChanged();
        table.setRowSorter(sorter);
        table.setAutoCreateRowSorter(false);
        sorter.setModel(tableModel);
        sorter.setRowFilter(searchField.getText().trim().isEmpty() ? null : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(searchField.getText().trim())));
    }

    private int selectedId() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return -1;
        return Integer.parseInt(String.valueOf(table.getValueAt(viewRow, 0)));
    }

    private void showEditor(boolean editing) {
        int id = editing ? selectedId() : -1;
        if (editing && id < 0) {
            JOptionPane.showMessageDialog(this, "Select a row to edit.", "Inventory", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (selectedType() == InventoryType.DESTINATIONS) {
            Destination existing = editing ? destinationDAO.findById(id).orElse(null) : new Destination();
            if (existing == null) { showError("The selected destination could not be found."); return; }
            editDestination(existing, editing);
        } else {
            Package existing = editing ? packageDAO.findById(id).orElse(null) : new Package();
            if (existing == null) { showError("The selected package could not be found."); return; }
            editPackage(existing, editing);
        }
    }

    private void editDestination(Destination d, boolean editing) {
        JTextField name = new JTextField(value(d.getName()), 22);
        JTextField country = new JTextField(value(d.getCountry()), 22);
        JComboBox<String> category = new JComboBox<>(new String[]{"Adventure", "Beach", "Culture", "Nature", "Luxury"});
        if (d.getCategory() != null) category.setSelectedItem(d.getCategory());
        JTextField cost = new JTextField(d.getEstimatedDailyCost() > 0 ? String.valueOf(d.getEstimatedDailyCost()) : "", 22);
        JTextField image = new JTextField(value(d.getImageUrl()), 22);
        JTextArea description = new JTextArea(value(d.getDescription()), 4, 22);
        description.setLineWrap(true); description.setWrapStyleWord(true);
        JPanel form = formPanel();
        addField(form, "Name *", name); addField(form, "Country *", country);
        addField(form, "Category *", category); addField(form, "Estimated daily cost *", cost);
        addField(form, "Image URL", image); addField(form, "Description", new JScrollPane(description));
        int result = JOptionPane.showConfirmDialog(this, form, editing ? "Edit Destination" : "Add Destination", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;
        try {
            if (name.getText().isBlank() || country.getText().isBlank()) throw new IllegalArgumentException("Name and country are required.");
            double dailyCost = Double.parseDouble(cost.getText().trim());
            if (!Double.isFinite(dailyCost) || dailyCost < 0) throw new IllegalArgumentException("Daily cost must be a non-negative number.");
            d.setName(name.getText().trim()); d.setCountry(country.getText().trim());
            d.setCategory((String) category.getSelectedItem()); d.setEstimatedDailyCost(dailyCost);
            d.setImageUrl(blankToNull(image.getText())); d.setDescription(blankToNull(description.getText()));
            boolean ok = editing ? destinationDAO.update(d) : destinationDAO.insert(d);
            if (!ok) throw new IllegalStateException("Database update failed. Check the database connection and input values.");
            loadInventory();
        } catch (IllegalArgumentException ex) { showError(ex.getMessage()); }
        catch (IllegalStateException ex) { showError(ex.getMessage()); }
    }

    private void editPackage(Package p, boolean editing) {
        List<Destination> destinations = destinationDAO.findAll();
        if (destinations.isEmpty()) { showError("Add at least one destination before creating a travel package."); return; }
        JComboBox<Destination> destination = new JComboBox<>(destinations.toArray(new Destination[0]));
        for (int i = 0; i < destination.getItemCount(); i++) {
            if (destination.getItemAt(i).getDestinationId() == p.getDestinationId()) { destination.setSelectedIndex(i); break; }
        }
        JTextField title = new JTextField(value(p.getTitle()), 22);
        JTextField days = new JTextField(p.getDurationDays() > 0 ? String.valueOf(p.getDurationDays()) : "", 22);
        JTextField price = new JTextField(p.getBasePrice() > 0 ? String.valueOf(p.getBasePrice()) : "", 22);
        JTextField season = new JTextField(value(p.getSeason()), 22);
        JTextField discount = new JTextField(String.valueOf(p.getDiscountPercentage()), 22);
        JCheckBox active = new JCheckBox("Available for booking", editing ? p.isActive() : true);
        JTextArea description = new JTextArea(value(p.getDescription()), 4, 22);
        description.setLineWrap(true); description.setWrapStyleWord(true);
        JPanel form = formPanel();
        addField(form, "Destination *", destination); addField(form, "Package title *", title);
        addField(form, "Duration (days) *", days); addField(form, "Base price *", price);
        addField(form, "Season", season); addField(form, "Discount (%)", discount);
        addField(form, "Description", new JScrollPane(description)); addField(form, "Status", active);
        int result = JOptionPane.showConfirmDialog(this, form, editing ? "Edit Travel Package" : "Add Travel Package", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;
        try {
            if (title.getText().isBlank()) throw new IllegalArgumentException("Package title is required.");
            int duration = Integer.parseInt(days.getText().trim());
            double basePrice = Double.parseDouble(price.getText().trim());
            double discountValue = Double.parseDouble(discount.getText().trim());
            if (duration < 1) throw new IllegalArgumentException("Duration must be at least one day.");
            if (!Double.isFinite(basePrice) || basePrice < 0) throw new IllegalArgumentException("Price must be a non-negative number.");
            if (!Double.isFinite(discountValue) || discountValue < 0 || discountValue > 100) throw new IllegalArgumentException("Discount must be between 0 and 100.");
            p.setDestinationId(((Destination) destination.getSelectedItem()).getDestinationId());
            p.setTitle(title.getText().trim()); p.setDurationDays(duration); p.setBasePrice(basePrice);
            p.setSeason(blankToNull(season.getText())); p.setDiscountPercentage(discountValue);
            p.setDescription(blankToNull(description.getText())); p.setActive(active.isSelected());
            boolean ok = editing ? packageDAO.update(p) : packageDAO.insert(p);
            if (!ok) throw new IllegalStateException("Database update failed. Check the database connection and input values.");
            loadInventory();
        } catch (IllegalArgumentException ex) { showError(ex.getMessage()); }
        catch (IllegalStateException ex) { showError(ex.getMessage()); }
    }

    private void deleteSelected() {
        int id = selectedId();
        if (id < 0) { JOptionPane.showMessageDialog(this, "Select a row to delete.", "Inventory", JOptionPane.INFORMATION_MESSAGE); return; }
        boolean isDestination = selectedType() == InventoryType.DESTINATIONS;
        String noun = isDestination ? "destination" : "package";
        String message = isDestination
                ? "Delete this destination? Destinations referenced by travel packages cannot be deleted."
                : "Remove this package from the active catalog? Existing booking history will be preserved.";
        if (JOptionPane.showConfirmDialog(this, message, "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
        boolean ok = isDestination ? destinationDAO.delete(id) : packageDAO.delete(id);
        if (!ok) showError("Could not delete the " + noun + ". It may be referenced by another record or already inactive.");
        loadInventory();
    }

    private JPanel formPanel() { JPanel p = new JPanel(new GridBagLayout()); p.setBorder(new EmptyBorder(8, 8, 8, 8)); return p; }
    private void addField(JPanel panel, String label, Component component) {
        GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(5, 5, 5, 5); c.anchor = GridBagConstraints.WEST;
        c.gridx = 0; c.gridy = panel.getComponentCount() / 2; c.weightx = 0; c.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(label), c);
        c.gridx = 1; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, c);
    }
    private String value(String value) { return value == null ? "" : value; }
    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    private void showError(String message) { JOptionPane.showMessageDialog(this, message, "Inventory Management", JOptionPane.ERROR_MESSAGE); }
}
