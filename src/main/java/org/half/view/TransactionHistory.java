package org.half.view;
import org.half.model.Account;
import org.half.utility.BankScanner;
import org.half.service.TransactionHistoryService;
import org.half.utility.ANSI;


public class TransactionHistory {
    public static void displayTransactions(Account chosenAccount){
        System.out.println("Here is your list of all of your past transactions:");
        System.out.println(ANSI.MAGENTA + ANSI.ITALIC + "Date " + ANSI.CYAN + ANSI.ITALIC + " Type " +
                ANSI.YELLOW + ANSI.ITALIC + " Amount " + ANSI.RED + ANSI.ITALIC + " Origin account ID "
                + ANSI.GREEN + ANSI.ITALIC + " Destination account ID");
        //run the service method to display the transactions
        TransactionHistoryService.attemptPrintOutTransactions(chosenAccount);
        BankScanner.freeze();
    }

}

