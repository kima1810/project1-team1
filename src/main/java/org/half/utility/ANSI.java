package org.half.utility;

public class ANSI {
    // Color code strings from:
    // http://www.topmudsites.com/forums/mud-coding/413-java-ansi.html
    public static final String	RESET				= "\u001B[0m";

    public static final String	HIGH_INTENSITY		= "\u001B[1m";
    public static final String	LOW_INTENSITY		= "\u001B[2m";

    public static final String	ITALIC				= "\u001B[3m";
    public static final String	UNDERLINE			= "\u001B[4m";

    public static final String	BLACK				= "\u001B[30m";
    public static final String	RED					= "\u001B[31m";
    public static final String	GREEN				= "\u001B[32m";
    public static final String	YELLOW				= "\u001B[33m";
    public static final String	BLUE				= "\u001B[34m";
    public static final String	MAGENTA				= "\u001B[35m";
    public static final String	CYAN				= "\u001B[36m";
    public static final String	WHITE				= "\u001B[37m";

    public static final String	BACKGROUND_BLACK	= "\u001B[40m";
    public static final String	BACKGROUND_RED		= "\u001B[41m";
    public static final String	BACKGROUND_GREEN	= "\u001B[42m";
    public static final String	BACKGROUND_YELLOW	= "\u001B[43m";
    public static final String	BACKGROUND_BLUE		= "\u001B[44m";
    public static final String	BACKGROUND_MAGENTA	= "\u001B[45m";
    public static final String	BACKGROUND_CYAN		= "\u001B[46m";
    public static final String	BACKGROUND_WHITE	= "\u001B[47m";

    // Private constructor, utility design pattern
    private ANSI() {}

    public static String rgb(int red, int green, int blue) {
        validateRgb(red, green, blue);
        return "\u001B[38;2;" + red + ";" + green + ";" + blue + "m";
    }

    public static String bgRgb(int red, int green, int blue) {
        validateRgb(red, green, blue);
        return "\u001B[48;2;" + red + ";" + green + ";" + blue + "m";
    }

    private static void validateRgb(int red, int green, int blue) {
        if (red < 0 || red > 255 ||
                green < 0 || green > 255 ||
                blue < 0 || blue > 255) {
            throw new IllegalArgumentException(
                    "RGB values must be between 0 and 255"
            );
        }
    }
}
