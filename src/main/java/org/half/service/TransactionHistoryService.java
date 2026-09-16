package org.half.service;

import org.half.model.Account;
import org.half.model.TransactionModel;
import org.half.repository.TransactionModelRepository;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.half.utility.ANSI;


public class TransactionHistoryService {
    public static boolean attemptPrintOutTransactions(Account chosenAccount){
        List<TransactionModel> transactionList = TransactionModelRepository.printOutTransactions(chosenAccount.getAccountNumber());

        if(transactionList.isEmpty()){
            System.out.println("No transactions found.");
            return false;
        }

        for(TransactionModel transactions : transactionList){
            System.out.printf(ANSI.MAGENTA + ANSI.ITALIC + "%-20s" + ANSI.CYAN + ANSI.ITALIC + " %-10s" +
                    ANSI.YELLOW + ANSI.ITALIC + "$%-14.2f" + ANSI.RED + ANSI.ITALIC + "%-20s"
                    + ANSI.GREEN + ANSI.ITALIC + "%-20s%n", transactions.getDateTime(), transactions.getType(),
                    transactions.getAmount(), transactions.getOriginAccountId(), transactions.getDestinationAccountId());

        }

        return true;
    }

    public static boolean attemptAddDepositOrWithdrawal(String type, double amount, long originAccountId){
        long transactionNumber = ThreadLocalRandom.current().nextLong(100_000_000_000L, 1_000_000_000_000L);
        TransactionModel transactionModel = new TransactionModel(transactionNumber, type, amount, originAccountId);

        if(transactionModel.getType().equals("Transfer")){
            System.out.println("Wrong service method chosen, use attemptAddTransfer with type, amount, originAccountId, and destinationAccountId as parameters.");
            return false;
        }
        if(!transactionModel.getType().equals("Deposit") && !transactionModel.getType().equals("Withdraw")){
            System.out.println("Invalid Transaction type");
            return false;
        }
        if(transactionModel.getAmount() <= 0.00){
            System.out.println("Amount must be greater than $0.00");
            return false;
        }

        return TransactionModelRepository.addDepositOrWithdrawal(transactionModel);
    }

    public static boolean attemptAddTransfer(String type, double amount, long originAccountId, long destinationAccountId){
        long transactionNumber = ThreadLocalRandom.current().nextLong(100_000_000_000L, 1_000_000_000_000L);
        TransactionModel transactionModel = new TransactionModel(transactionNumber, type, amount, originAccountId, destinationAccountId);

        if(transactionModel.getType().equals("Deposit") || transactionModel.getType().equals("Withdraw")){
            System.out.println("Wrong service method chosen, use attemptAddDepositOrWithdrawal with type, amount, and originAccountId as parameters.");
            return false;
        }

        if(!transactionModel.getType().equals("Transfer")){
            System.out.println("Invalid Transaction type");
            return false;
        }

        if(transactionModel.getAmount() <= 0.00){
            System.out.println("Amount must be greater than $0.00");
            return false;
        }

        return TransactionModelRepository.addTransfer(transactionModel);
    }


}
