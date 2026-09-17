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
            System.out.println(ANSI.RESET + "\n" + ANSI.rgb(255, 255, 100) +
                    "┌────────────────────────────────┐\n" +
                    "│  Main Menu                     │\n" +
                    "└────────────────────────────────┘\n" +
                    ANSI.RESET
            );
            System.out.print(ANSI.rgb(100, 255, 100));
            System.out.println("[1] View balance");
            System.out.println("[2] Withdraw");
            System.out.println("[3] Deposit");
            System.out.println("[4] Transaction history");
            System.out.println("[5] Transfer money");
            System.out.println(ANSI.rgb(255,100,100) + "[0] Chose Different Bank Account");
            System.out.print(ANSI.RESET);

            System.out.println("\n──────────────────────────────────");

            System.out.print("Select an option: ");
            int userInput = BankScanner.promptUserSelection();

            switch (userInput) {
                case 1:
                    CheckBalance.showBalance(activeAccount);
                    break;
                case 2:
                    Withdraw.Withdraw_View(activeAccount);
                    break;
                case 3:
                    Deposit.Deposit_View(activeAccount);
                    break;
                case 4:
                    TransactionHistory.displayTransactions(activeAccount);
                    break;
                case 5:
                    Transfer.transfer(activeAccount);
                    break;
                case 0:
                    break mainMenu;
            }
        }
    }
}
