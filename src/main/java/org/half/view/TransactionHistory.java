package org.half.view;
import org.half.model.Account;
import org.half.utility.BankScanner;
import org.half.service.TransactionHistoryService;
import org.half.utility.ANSI;


public class TransactionHistory {
    public static void displayTransactions(Account chosenAccount){
        System.out.println("Here is your list of all of your past transactions:");
        System.out.printf(ANSI.MAGENTA + ANSI.ITALIC + "%-20s" + ANSI.CYAN + ANSI.ITALIC + " %-10s" +
                ANSI.YELLOW + ANSI.ITALIC + "%-15s" + ANSI.RED + ANSI.ITALIC + "%-20s"
                + ANSI.GREEN + ANSI.ITALIC + "%-20s%n", "Date", "Type", "Amount", "Origin account ID", "Destination account ID");
        //run the service method to display the transactions
        TransactionHistoryService.attemptPrintOutTransactions(chosenAccount);
        BankScanner.freeze();
    }

}

