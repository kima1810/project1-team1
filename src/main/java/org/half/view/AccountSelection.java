package org.half.view;

import org.half.model.Account;
import org.half.model.User;
import org.half.service.AccountService;
import org.half.utility.BankScanner;

import java.util.List;

public class AccountSelection {
    public static void selectAccount(User user) {
        while (true) {
            List<Account> accounts = AccountService.getAccounts(user);

            while (accounts == null) {
                System.out.println("No accounts found.");
                AccountCreation.createAccount(user);

                accounts = AccountService.getAccounts(user);
            }

            System.out.println("Your accounts:");

            for (int i = 1; i <= accounts.size(); i++) {
                Account account = accounts.get(i - 1);
                System.out.printf(i + ". " + account.getAccountType() + " ****%04d%n",(account.getAccountNumber() % 10000));
            }

            System.out.println("Or input -1 to create a new account.");
            System.out.println("Or input 0 to logout.");

            System.out.println("Please select your account:");
            int accountSelected = BankScanner.getInt();

            if (accountSelected == 0) break;
            else if (accountSelected == -1) {
                AccountCreation.createAccount(user);
                continue;
            }

            MainMenu.mainMenu(accounts.get(accountSelected - 1));
        }

    }
}
