package org.half.view;
import org.half.model.Account;

import org.half.utility.ANSI;
import org.half.utility.BankScanner;

public class MainMenu {

    public static void mainMenu(Account activeAccount) {
        mainMenu:
        while (true) {
            System.out.println(ANSI.MAGENTA + ANSI.HIGH_INTENSITY + "Welcome, " +
                    ANSI.CYAN + ANSI.ITALIC + activeAccount.getUser().getUsername() + ANSI.RESET +
                    ANSI.MAGENTA + ANSI.HIGH_INTENSITY + "!" + ANSI.RESET);
            System.out.print(ANSI.YELLOW);
            System.out.println("1. View balance");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Transaction history");
            System.out.println("5. Transfer money");
            System.out.println(ANSI.rgb(255,100,100) + "0. Logout");
            System.out.print(ANSI.RESET);

            int userInput = BankScanner.getInt();

            switch (userInput) {
                case 1:
                    CheckBalance.showBalance();
                    break;
                case 2:
                    Withdraw.Withdraw_Request();
                    break;
                case 3:
                    Deposit.Deposit_Request();
                    break;
                case 4:
                    TransactionHistory.printTransactions();
                    break;
                case 5:
                    Transfer.transfer();
                    break;
                case 0:
                    break mainMenu;
            }
        }
    }
}
