package org.half.service;

import org.half.model.Account;
import org.half.model.TransactionModel;
import org.half.repository.TransactionModelRepository;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.half.utility.ANSI;


public class TransactionHistoryService {
    public static void attemptPrintOutTransactions(Account chosenAccount){
        List<TransactionModel> transactionList = TransactionModelRepository.printOutTransactions(chosenAccount.getAccountNumber());

        for(TransactionModel transactions : transactionList){
            System.out.printf(ANSI.MAGENTA + ANSI.ITALIC + "%-20s" + ANSI.CYAN + ANSI.ITALIC + " %-10s" +
                    ANSI.YELLOW + ANSI.ITALIC + "$%-14.2f" + ANSI.RED + ANSI.ITALIC + "%-20s"
                    + ANSI.GREEN + ANSI.ITALIC + "%-20s%n", transactions.getDateTime(), transactions.getType(),
                    transactions.getAmount(), transactions.getOriginAccountId(), transactions.getDestinationAccountId());
            /*
            System.out.println(ANSI.MAGENTA + ANSI.HIGH_INTENSITY +  + "  " + ANSI.CYAN + ANSI.HIGH_INTENSITY + transactions.getType()
                    + "  " + ANSI.YELLOW + ANSI.HIGH_INTENSITY + String.format("$%.2f",transactions.getAmount()) + "  "
                    + ANSI.RED + ANSI.HIGH_INTENSITY + transactions.getOriginAccountId() + "  "
                    + ANSI.GREEN + ANSI.HIGH_INTENSITY + transactions.getDestinationAccountId());

             */
        }
    }

    public static void attemptAddDepositOrWithdrawal(String type, double amount, long originAccountId){
        long accountNumber = ThreadLocalRandom.current().nextLong(100_000_000_000L, 1_000_000_000_000L);
        TransactionModel transactionModel = new TransactionModel(accountNumber, type, amount, originAccountId);

        if(transactionModel.getType().equals("Transfer")){
            System.out.println("Wrong service method chosen, use attemptAddTransfer with type, amount, originAccountId, and destinationAccountId as parameters.");
            return;
        }
        if(!transactionModel.getType().equals("Deposit") && !transactionModel.getType().equals("Withdraw")){
            System.out.println("Invalid Transaction type");
            return;
        }
        if(transactionModel.getAmount() < 0.00){
            System.out.println("Amount must be greater than $0.00");
            return;
        }

        TransactionModelRepository.addDepositOrWithdrawal(transactionModel);
    }

    public static void attemptAddTransfer(String type, double amount, long originAccountId, long destinationAccountId){
        long accountNumber = ThreadLocalRandom.current().nextLong(100_000_000_000L, 1_000_000_000_000L);
        TransactionModel transactionModel = new TransactionModel(accountNumber, type, amount, originAccountId, destinationAccountId);

        if(transactionModel.getType().equals("Deposit") || transactionModel.getType().equals("Withdraw")){
            System.out.println("Wrong service method chosen, use attemptAddDepositOrWithdrawal with type, amount, and originAccountId as parameters.");
            return;
        }

        if(!transactionModel.getType().equals("Transfer")){
            System.out.println("Invalid Transaction type");
            return;
        }

        if(transactionModel.getAmount() < 0.00){
            System.out.println("Amount must be greater than $0.00");
            return;
        }

        TransactionModelRepository.addTransfer(transactionModel);
    }


}
