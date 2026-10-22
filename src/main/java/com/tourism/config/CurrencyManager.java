package com.tourism.config;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton managing currency selection, live exchange rates, and price formatting.
 */
public class CurrencyManager {

    private static CurrencyManager instance;

    public enum Currency {
        USD("$", 1.0, "USD ($)"),
        INR("₹", 83.2, "INR (₹)"),
        EUR("€", 0.92, "EUR (€)"),
        GBP("£", 0.79, "GBP (£)"),
        JPY("¥", 152.0, "JPY (¥)");

        private final String symbol;
        private final double rate;
        private final String displayName;

        Currency(String symbol, double rate, String displayName) {
            this.symbol = symbol;
            this.rate = rate;
            this.displayName = displayName;
        }

        public String getSymbol() {
            return symbol;
        }

        public double getRate() {
            return rate;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    private Currency currentCurrency = Currency.USD;
    private final List<Runnable> listeners = new ArrayList<>();

    private CurrencyManager() {}

    public static synchronized CurrencyManager getInstance() {
        if (instance == null) {
            instance = new CurrencyManager();
        }
        return instance;
    }

    public Currency getCurrentCurrency() {
        return currentCurrency;
    }

    public void setCurrency(Currency currency) {
        if (currency != null && currency != this.currentCurrency) {
            this.currentCurrency = currency;
            notifyListeners();
        }
    }

    public double convert(double usdAmount) {
        return usdAmount * currentCurrency.getRate();
    }

    public String format(double usdAmount) {
        double converted = convert(usdAmount);
        DecimalFormat df = (currentCurrency == Currency.JPY)
                ? new DecimalFormat("#,##0")
                : new DecimalFormat("#,##0.00");
        return currentCurrency.getSymbol() + " " + df.format(converted);
    }

    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {
        for (Runnable r : listeners) {
            try {
                r.run();
            } catch (Exception ignored) {}
        }
    }
}
