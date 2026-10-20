package com.tourism.ui.admin;

import com.tourism.service.AnalyticsService;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Admin reports: top destinations (bar chart) and monthly revenue (line chart).
 * Data is loaded off the Swing thread so the window never freezes.
 */
public class ReportsPanel extends JPanel {

    private static final int TOP_DESTINATION_LIMIT = 5;

    private final AnalyticsService analyticsService = new AnalyticsService();
    private final JPanel chartsContainer = new JPanel(new GridLayout(2, 1, 0, 12));
    private final JButton refreshButton = new JButton("Refresh");

    public ReportsPanel() {
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel heading = new JLabel("Reports & Analytics");
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 20f));

        refreshButton.addActionListener(e -> loadCharts());

        JPanel header = new JPanel(new BorderLayout());
        header.add(heading, BorderLayout.WEST);
        header.add(refreshButton, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
        add(chartsContainer, BorderLayout.CENTER);

        loadCharts();
    }

    private void loadCharts() {
        refreshButton.setEnabled(false);

        new SwingWorker<JFreeChart[], Void>() {
            @Override
            protected JFreeChart[] doInBackground() throws Exception {
                Map<String, Integer> topDestinations =
                        analyticsService.getTopDestinations(TOP_DESTINATION_LIMIT);
                Map<String, BigDecimal> monthlyRevenue = analyticsService.getMonthlyRevenue();
                return new JFreeChart[] {
                        buildTopDestinationsChart(topDestinations),
                        buildRevenueChart(monthlyRevenue)
                };
            }

            @Override
            protected void done() {
                refreshButton.setEnabled(true);
                try {
                    JFreeChart[] charts = get();
                    chartsContainer.removeAll();
                    chartsContainer.add(new ChartPanel(charts[0]));
                    chartsContainer.add(new ChartPanel(charts[1]));
                    chartsContainer.revalidate();
                    chartsContainer.repaint();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    JOptionPane.showMessageDialog(ReportsPanel.this,
                            "Could not load reports: " + ex.getCause().getMessage(),
                            "Reports", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private JFreeChart buildTopDestinationsChart(Map<String, Integer> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        data.forEach((destination, bookings) ->
                dataset.addValue(bookings, "Bookings", destination));

        JFreeChart chart = ChartFactory.createBarChart(
                "Top 5 Most Booked Destinations", "Destination", "Bookings",
                dataset, PlotOrientation.VERTICAL, false, true, false);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.getRenderer().setSeriesPaint(0, new Color(0x2E86DE));
        // Booking counts are whole numbers, so hide fractional ticks.
        ((NumberAxis) plot.getRangeAxis()).setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        return chart;
    }

    private JFreeChart buildRevenueChart(Map<String, BigDecimal> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        data.forEach((month, revenue) -> dataset.addValue(revenue, "Revenue", month));

        JFreeChart chart = ChartFactory.createLineChart(
                "Monthly Revenue Trend", "Month", "Revenue",
                dataset, PlotOrientation.VERTICAL, false, true, false);

        chart.getCategoryPlot().getRenderer().setSeriesPaint(0, new Color(0x10AC84));
        return chart;
    }
}
