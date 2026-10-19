package com.tourism.config;

import java.awt.*;

/**
 * Standard UI design constants for the Personalized Tourism Management System.
 * All team members should reference these constants to ensure visual consistency.
 */
public final class ThemeConstants {

    private ThemeConstants() {} // Prevent instantiation

    // Primary Colors
    public static final Color PRIMARY_COLOR = new Color(41, 128, 185);     // Ocean Blue
    public static final Color PRIMARY_DARK  = new Color(31, 97, 141);      // Deep Blue
    public static final Color ACCENT_COLOR  = new Color(39, 174, 96);      // Emerald Green (Success)
    public static final Color DANGER_COLOR  = new Color(192, 57, 43);      // Crimson Red (Cancel / Error)
    public static final Color WARNING_COLOR = new Color(243, 156, 18);     // Amber / Gold (Rating / Alerts)
    public static final Color BACKGROUND    = new Color(248, 249, 250);    // Light Off-White
    public static final Color CARD_BG       = Color.WHITE;                 // Card background
    public static final Color TEXT_PRIMARY  = new Color(44, 62, 80);       // Dark Slate
    public static final Color TEXT_MUTED    = new Color(127, 140, 141);    // Cool Gray

    // Fonts
    public static final Font FONT_HEADER_LARGE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER       = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SUBHEADER    = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY         = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD         = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL        = new Font("Segoe UI", Font.PLAIN, 11);

    // Padding & Insets
    public static final int PADDING_SMALL  = 8;
    public static final int PADDING_MEDIUM = 16;
    public static final int PADDING_LARGE  = 24;
}
