package org.half.security;

import org.half.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountVerificationService {
    private static final Logger log = LoggerFactory.getLogger(AccountVerificationService.class);

    // Method to verify if the entered user PIN matches the account PIN hash
    public static boolean verifyAccount(Account account, int userInputPIN) {
        // Check if user input is valid
        if (userInputPIN > 9999) {
            // Input PIN cannot be more than 4 digits
            log.warn("Invalid pin. Cannot be more than 4 digits.");
            throw new IllegalArgumentException("Invalid pin. Cannot be more than 4 digits.");
        }

        // Return ture if password is correct, else false
        return PasswordService.verifyPassword(String.valueOf(userInputPIN), account.getPinHash());
    }
}
