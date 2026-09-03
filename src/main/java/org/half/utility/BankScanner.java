package org.half.utility;

import java.util.Scanner;

// Utility class for a scanner to be shared
public class BankScanner {
    // Store the only instance of the scanner
    private static final Scanner instance = new Scanner(System.in);

    // Private constructor, singleton pattern
    private BankScanner() {}

    // Get the instance of the scanner
    public static Scanner getInstance() {
        return instance;
    }

    // Close the scanner
    public static void closeScanner() {
        instance.close();
    }

    // Get a string input from user
    public static String getString() {
        return instance.nextLine();
    }

    // Get an integer input from user
    public static int getInt() {
        return Integer.parseInt(instance.nextLine());
    }

    // Get a decimal number from user
    public static double getDouble() {
        return Double.parseDouble(instance.nextLine());
    }

    // Freeze the screen and wait for user to continue
    public static void freeze() {
        instance.nextLine();
    }
}