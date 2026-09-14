package org.half.view;

import org.half.model.User;
import org.half.model.enums.AccountType;
import org.half.service.AccountService;
import org.half.utility.BankScanner;

public class AccountCreation {
    public static void createAccount(User user) {
        System.out.println("Let's create your account...");
        while (true) {
            System.out.println("Choose account type: CHECKING or SAVINGS");

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

            System.out.println("Account successfully created!");
            System.out.println("Please note the account number for your information:");
            System.out.println(accountNumber);

            System.out.println("\nPress enter to continue...");
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
