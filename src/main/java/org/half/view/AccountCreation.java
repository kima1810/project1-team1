package org.half.view;

import org.half.model.User;
import org.half.model.enums.AccountType;
import org.half.service.AccountService;
import org.half.utility.BankScanner;

public class AccountCreation {
    public static void createAccount(User user) {
        System.out.println("Let's create your account...");
        System.out.println("Choose account type: CHECKING or SAVINGS");
        String accountTypeInput = BankScanner.getString();

        AccountType accountType = AccountType.valueOf(accountTypeInput);

        System.out.println("Create a 4-digit PIN for your account:");
        int pinInput = BankScanner.getInt();
        // TODO: Fix 4-digit pin bug. Right now 0001 to 0999 is invalid for a PIN.
        while (pinInput < 1000 || pinInput > 9999) {
            System.out.println("Invalid PIN number. Must be exactly 4 digits long.");
            System.out.println("Please enter a valid PIN:");
            pinInput = BankScanner.getInt();
        }

        System.out.println("Re-enter your PIN:");
        int pinInputConfirmation = BankScanner.getInt();
        while ((pinInputConfirmation < 1000 || pinInputConfirmation > 9999) || pinInput != pinInputConfirmation) {
            if (pinInputConfirmation < 1000 || pinInputConfirmation > 9999) {
                System.out.println("Invalid PIN. Must be exactly 4 digits long.");
            } else {
                System.out.println("PIN numbers do not match. Please try again.");
            }

            System.out.println("Re-enter your PIN:");
            pinInputConfirmation = BankScanner.getInt();
        }

        AccountService.createAccount(user, pinInput, accountType);
    }
}
