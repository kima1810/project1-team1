package org.half.security;

import org.half.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountVerificationService {
    private static final Logger log = LoggerFactory.getLogger(AccountVerificationService.class);

    public static Account verifyAccount(Account account, int userInputPIN) {
        if (userInputPIN > 9999) {
            throw new IllegalArgumentException("Invalid pin. Cannot be more than 4 digits.");
        }

        if (PasswordService.verifyPassword(String.valueOf(userInputPIN), account.getPinHash())) {
            return account;
        }

        return null;
    }
}
