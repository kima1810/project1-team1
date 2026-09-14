package org.half.view;

import org.half.model.User;
import org.half.model.enums.AccountType;
import org.half.service.AccountService;
import org.half.utility.ANSI;
import org.half.utility.BankScanner;

public class AccountCreation {
    public static void createAccount(User user) {
        System.out.println(ANSI.RESET + "\n" + ANSI.rgb(255, 255, 100) +
                "Let's create your account..." +
                ANSI.RESET);
        while (true) {
            System.out.println("Choose account type: " +
                    ANSI.rgb(100, 255, 100) + "CHECKING" +
                    ANSI.RESET + " or " +
                    ANSI.rgb(100, 255, 100) + "SAVINGS" + ANSI.RESET);

            AccountType accountType;
            while (true) {
                String accountTypeInput = BankScanner.getString();

                try {
                    accountType = AccountType.valueOf(accountTypeInput.toUpperCase());
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid account type. Acceptable values: CHECKING, SAVINGS");
                }
            }

            System.out.println("Create a 4-digit PIN for your account:");
            int pin = promptUserForPIN();

            System.out.println("Re-enter your PIN:");
            int pinConfirmation = promptUserForPIN();
            while (pin != pinConfirmation) {
                System.out.println("PINs do not match. Try again:");
                pinConfirmation = promptUserForPIN();
            }

            long accountNumber;
            try {
                accountNumber = AccountService.createAccount(user, pin, accountType);
                if (accountNumber == -1) {
                    System.out.println("Account creation failed. Please try again...");
                    continue;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Something went wrong. Please try again...");
                continue;
            }

            System.out.println(ANSI.RESET + "\n" + ANSI.rgb(255, 255, 100) +
                    "Account successfully created!" +
                    ANSI.RESET);
            System.out.println("Please note the account number for your information:");
            System.out.println(ANSI.MAGENTA + accountNumber + ANSI.RESET);

            System.out.print(ANSI.rgb(100, 255, 100) + "\nPress enter to continue..." + ANSI.RESET);
            BankScanner.freeze();
            break;
        }
    }

    private static int promptUserForPIN() {
        int pin;

        while (true) {
            String pinInput = BankScanner.getString();

            try {
                pin = Integer.parseInt(pinInput.trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid PIN number. Must be an integer.");
                continue;
            }

            if (pinInput.trim().length() != 4) {
                System.out.println("Invalid PIN. Must be exactly 4 digits long.");
                continue;
            }

            break;
        }

        return pin;
    }
}
