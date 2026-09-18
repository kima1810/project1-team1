package org.half.view;
import org.half.model.Account;

import org.half.utility.ANSI;
import org.half.utility.BankScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainMenu {
    private static final Logger log = LoggerFactory.getLogger(MainMenu.class);

    public static void mainMenu(Account activeAccount) {
        mainMenu:
        while (true) {
            log.info("Displaying main menu...");

            // Main menu title
            System.out.println(ANSI.RESET + "\n" + ANSI.rgb(255, 255, 100) +
                    "┌────────────────────────────────┐\n" +
                    "│  Main Menu                     │\n" +
                    "└────────────────────────────────┘\n" +
                    ANSI.RESET
            );

            // Print main menu options
            System.out.print(ANSI.rgb(100, 255, 100));
            System.out.println("[1] View balance");
            System.out.println("[2] Withdraw");
            System.out.println("[3] Deposit");
            System.out.println("[4] Transfer");
            System.out.println("[5] Transaction history");
            System.out.println(ANSI.rgb(255,100,100) + "[0] Switch Account");
            System.out.print(ANSI.RESET);

            System.out.println("\n──────────────────────────────────");

            // Ask user to select an option
            System.out.print("Select an option: ");
            int userInput = BankScanner.promptUserSelection();

            log.info("User selected option: {}", userInput);

            // Check if option is valid
            while (userInput < 0 || userInput > 5) {
                System.out.print("Please enter a number between 0 and 5: " );
                userInput = BankScanner.promptUserSelection();
            }

            // Check which menu option the user selected
            switch (userInput) {
                case 1:
                    // View balance
                    CheckBalance.showBalance(activeAccount);
                    break;
                case 2:
                    // Withdraw
                    Withdraw.Withdraw_View(activeAccount);
                    break;
                case 3:
                    // Deposit
                    Deposit.Deposit_View(activeAccount);
                    break;
                case 4:
                    // Transfer
                    Transfer.transfer(activeAccount);
                    break;
                case 5:
                    // Transaction history
                    TransactionHistory.displayTransactions(activeAccount);
                    break;
                case 0:
                    // Switch accounts
                    break mainMenu;
            }
        }
    }
}
