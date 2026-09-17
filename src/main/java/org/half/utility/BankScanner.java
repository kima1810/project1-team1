package org.half.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

// Utility class for a scanner to be shared
public class BankScanner {
    // Class specific Logger for logging
    private static final Logger log = LoggerFactory.getLogger(BankScanner.class);

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

    // Function to ask user to select an option by inputting a number
    public static int promptUserSelection () {
        // Store the selected option
        int optionSelected;

        // Keep prompting user until a valid integer input is accepted
        while (true) {
            try {
                // Get a string input from user
                String userInput = BankScanner.getString().trim();

                // Check if the input is empty
                if (userInput.isBlank()) {
                    // Re-prompt user
                    System.out.println("Please select an option:");
                    log.warn("User inputted nothing. Prompting again...");
                    continue;
                }

                // Parse the selected option to an int
                optionSelected = Integer.parseInt(userInput);
                break;
            } catch (NumberFormatException e) {
                // Input was not an integer, ask user to input a number
                System.out.println("Not a number. Please try again...");
                log.warn("User did not input a number. Prompting again...");
            }

        }

        // Return the selected option
        return optionSelected;
    }
}