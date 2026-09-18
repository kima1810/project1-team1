package org.half.view;

import org.half.model.Account;
import org.half.model.User;
import org.half.repository.AccountRepository;
import org.half.repository.TransactionModelRepository;
import org.half.security.AccountVerificationService;
import org.half.service.AccountService;
import org.half.service.TransactionHistoryService;
import org.half.utility.ANSI;
import org.half.utility.BankScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

// Account selection view
public class AccountSelection {
    // Class specific Logger for logging
    private static final Logger log = LoggerFactory.getLogger(AccountSelection.class);

    private static final AccountRepository accountRepository = new AccountRepository();
    private static final TransactionModelRepository transactionModelRepository = new TransactionModelRepository();

    private static final TransactionHistoryService transactionHistoryService = new TransactionHistoryService(transactionModelRepository);

    private static final AccountService accountService = new AccountService(accountRepository, transactionHistoryService);

    // View for user to select their account
    public static void selectAccount(User user) {
        // Welcome the user
        System.out.println("\n**********************************");
        System.out.println(ANSI.MAGENTA + ANSI.HIGH_INTENSITY + "Welcome, " +
                ANSI.CYAN + ANSI.ITALIC + user.getFirstName() + ANSI.RESET +
                ANSI.MAGENTA + ANSI.HIGH_INTENSITY + "!" + ANSI.RESET);
        System.out.println("**********************************");

        // Show the account selection menu
        while (true) {
            // Get all the accounts for the logged-in user
            List<Account> accounts = accountService.getAccounts(user);

            // If user has no account, keep prompting them to create a new account
            while (accounts == null || accounts.isEmpty()) {
                System.out.println(ANSI.userWarning("No accounts found."));

                // Call the account creation view
                AccountCreation.createAccount(user);

                // Retry getting accounts
                accounts = accountService.getAccounts(user);
            }

            // Selecting accounts title
            System.out.println(ANSI.RESET + "\n" + ANSI.rgb(255, 255, 100) +
                    "┌────────────────────────────────┐\n" +
                    "│  Your Accounts:                │\n" +
                    "└────────────────────────────────┘\n" +
                    ANSI.RESET
            );

            log.info("Printing all the accounts for user: {}", user.getUsername());

            // Print each account and their options
            System.out.print(ANSI.rgb(100, 255, 100));
            for (int i = 1; i <= accounts.size(); i++) {
                Account account = accounts.get(i - 1);
                System.out.printf("[" + i + "] " + account.getAccountType() + " ****%04d%n", (account.getAccountNumber() % 10000));
            }

            // Print other options
            System.out.println(ANSI.rgb(50, 245, 245) + "[-1] Open a new account.");
            System.out.println(ANSI.rgb(255, 125, 100) + "[0] Logout");

            System.out.print(ANSI.RESET);
            System.out.println("\n──────────────────────────────────");

            // Prompt user to input an option
            System.out.print("Please select your account: ");
            int userInput = BankScanner.promptUserSelection();

            // Check if a valid option was selected
            while (userInput > accounts.size()) {
                System.out.print("Please enter a number between -1 and " + accounts.size() +": ");
                log.warn("User selection is invalid.");
                userInput = BankScanner.promptUserSelection();
            }

            log.info("User selected option: {}", userInput);

            // Check if user wants to log out
            if (userInput == 0) {
                // Log out the user
                log.info("Logging out user: {}", user.getUsername());
                break;
            }

            // Check if the user wants to create a new account
            else if (userInput == -1) {
                // Call the account creation view
                AccountCreation.createAccount(user);
                continue;
            }

            // Find the account user selected
            Account selectedAccount = accounts.get(userInput - 1);

            log.info("User attempting to log into account: {user: {}, account: {}}",
                    user.getUsername(),
                    selectedAccount.getAccountType() +  String.format(" ****%04d", selectedAccount.getAccountNumber() % 10000));

            // Keep asking for account PIN until a valid PIN is entered
            while (true) {
                // Prompt the user to enter account PIN
                System.out.print("Enter your account PIN: ");
                int accountPinInput = BankScanner.promptUserForPIN();

                try {
                    // Attempt to log into the account
                    if (AccountVerificationService.verifyAccount(accounts.get(userInput - 1), accountPinInput)) {
                        break;
                    } else {
                        System.out.println(ANSI.userWarning("Invalid credentials."));
                        log.warn("Account login failed: {user: {}, account: {}}",
                                user.getUsername(),
                                selectedAccount.getAccountType() +  String.format(" ****%04d", selectedAccount.getAccountNumber() % 10000));
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Something went wrong.");
                    log.error("Something went wrong: {}", e.getMessage());
                }
            }
            System.out.println(ANSI.success("Success! Logging into your account..."));
            log.info("Successfully logged into account: {user: {}, account: {}}",
                    user.getUsername(),
                    selectedAccount.getAccountType() +  String.format(" ****%04d", selectedAccount.getAccountNumber() % 10000));

            // Call the main menu view
            MainMenu.mainMenu(selectedAccount);
        }
    }
}
