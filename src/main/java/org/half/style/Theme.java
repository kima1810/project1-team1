package org.half.style;

import com.williamcallahan.tui4j.compat.lipgloss.Style;
import com.williamcallahan.tui4j.compat.lipgloss.color.Color;
import com.williamcallahan.tui4j.compat.lipgloss.Borders;

public class Theme {
    // --- Master Color Palette ---
    // Using standard Xterm-256 color codes
    private static final String BRAND_PRIMARY = "#00FF04";    // Bright Green
    private static final String BRAND_SECONDARY = "63";  // Deep Indigo
    private static final String HIGHLIGHT = "227";       // Electric Yellow
    private static final String DANGER = "203";          // Soft Red
    private static final String MUTED_GRAY = "240";      // Dark Gray
    private static final String PURE_BLACK = "#000000";
    private static final String CYAN = "51";
    private static final String LIGHT_CYAN = "87";

    // --- Global UI Components ---

    // Standard application panel with rounded borders
    public static final Style MAIN_PANEL = Style.newStyle()
            .border(Borders.roundedBorder())
            .borderTopForeground(Color.color(BRAND_SECONDARY))
            .borderBottomForeground(Color.color(BRAND_SECONDARY))
            .borderLeftForeground(Color.color(BRAND_SECONDARY))
            .borderRightForeground(Color.color(BRAND_SECONDARY))
            .padding(1, 4)
            .margin(1, 2);

    // Inner content panel for nested views (like forms or transaction history)
    public static final Style CONTENT_PANEL = Style.newStyle()
            .border(Borders.normalBorder())
            .borderForeground(Color.color(BRAND_PRIMARY))
            .padding(2, 6)
            .margin(1, 2);

    // --- Typography & Elements ---

    public static final Style TITLE = Style.newStyle()
            .foreground(Color.color(HIGHLIGHT))
            .bold(true);

    public static final Style LOGO = Style.newStyle()
            .foreground(Color.color(BRAND_PRIMARY))
            .bold(true);

   public static final Style ACTIVE_ITEM_SELECT = Style.newStyle()
            .foreground(Color.color("0"))
            .background(Color.color(BRAND_PRIMARY))
            .bold(true).padding(0, 1);

    public static final Style ACTIVE_ITEM_INPUT = Style.newStyle()
            .foreground(Color.color(BRAND_PRIMARY))
            .bold(true)
            .padding(0, 1);

    public static final Style INACTIVE_MENU_ITEM = Style.newStyle()
            .foreground(Color.color("255")); // Standard White

    public static final Style TEXT_CURSOR = Style.newStyle()
            .foreground(Color.color(BRAND_PRIMARY))
            .blink(true);

    public static final Style ERROR_TEXT = Style.newStyle()
            .foreground(Color.color(DANGER))
            .bold(true);

    public static final Style FOOTER_TEXT = Style.newStyle()
            .foreground(Color.color(MUTED_GRAY));

    public static final Style USERNAME = Style.newStyle()
            .foreground(Color.color(CYAN));

    public static final Style NEW_ACCOUNT = Style.newStyle()
            .foreground(Color.color(LIGHT_CYAN));
}