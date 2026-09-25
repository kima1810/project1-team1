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
            System.out.println("\n" + ANSI.title(
                    """
                            ┌────────────────────────────────┐
                            │  Main Menu                     │
                            └────────────────────────────────┘
                            """));

            // Print main menu options
            System.out.println(ANSI.optionPositive("[1] View balance"));
            System.out.println(ANSI.optionPositive("[2] Withdraw"));
            System.out.println(ANSI.optionPositive("[3] Deposit"));
            System.out.println(ANSI.optionPositive("[4] Transfer"));
            System.out.println(ANSI.optionPositive("[5] Transaction history"));
            System.out.println(ANSI.optionPositive("[6] Show account number"));
            System.out.println(ANSI.optionNegative("[0] Switch Account"));

            System.out.println("\n──────────────────────────────────");

            // Ask user to select an option
            System.out.print("Select an option: ");
            int userInput = BankScanner.promptUserSelection();

            log.info("User selected option: {}", userInput);

            // Check if option is valid
            while (userInput < 0 || userInput > 6) {
                System.out.print(ANSI.userWarning("Please enter a number between 0 and 6: " ));
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
                case 6:
                    // View account number
                    CheckAccountNumber.showAccountNumber(activeAccount);
                    break;
                case 0:
                    // Switch accounts
                    System.out.println(ANSI.userWarning("Switching accounts..."));
                    break mainMenu;
            }
        }
    }
}
