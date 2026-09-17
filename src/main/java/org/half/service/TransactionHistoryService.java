package org.half.service;

import org.half.model.Account;
import org.half.model.TransactionModel;
import org.half.repository.TransactionModelRepository;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.half.utility.ANSI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TransactionHistoryService {
    //logger
    private static final Logger logger = LoggerFactory.getLogger(TransactionHistoryService.class);
    //prints all the transactions
    public static boolean attemptPrintOutTransactions(Account chosenAccount){
        //runs the repository method
        List<TransactionModel> transactionList = TransactionModelRepository.printOutTransactions(chosenAccount.getAccountNumber());

        //if there are no transactions found with the associated id then return false
        if(transactionList.isEmpty()){
            System.out.println("No transactions found.");
            logger.warn("No transactions were found for the attemptPrintOutTransactions method with the account number: "
                    + chosenAccount.getAccountNumber());
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

    //runs the repository method to create a deposit or withdrawal
    public static boolean attemptAddDepositOrWithdrawal(String type, double amount, long originAccountId){
        //creates the transaction id
        long transactionNumber = ThreadLocalRandom.current().nextLong(100_000_000_000L, 1_000_000_000_000L);
        //creates the transaction object
        TransactionModel transactionModel = new TransactionModel(transactionNumber, type, amount, originAccountId);

        //done to make sure that the transaction information is valid for this method
        if(transactionModel.getType().equals("Transfer")){
            System.out.println("Wrong service method chosen, use attemptAddTransfer with type, amount, originAccountId, and destinationAccountId as parameters.");
            logger.warn("Type transfer was used for the deposit or withdrawal method");
            return false;
        }
        if(!transactionModel.getType().equals("Deposit") && !transactionModel.getType().equals("Withdraw")){
            System.out.println("Invalid Transaction type");
            logger.warn("Random stuff was imputed for the deposit or withdrawal");
            return false;
        }
        if(transactionModel.getAmount() <= 0.00){
            System.out.println("Amount must be greater than $0.00");
            logger.warn("User tried depositing or withdrawing a value less than or equal to 0");
            return false;
        }
        //run the repository method, which will either return true or false
        return TransactionModelRepository.addDepositOrWithdrawal(transactionModel);
    }

    //runs the repository method to create a transfer
    public static boolean attemptAddTransfer(String type, double amount, long originAccountId, long destinationAccountId){
        //creates the transaction id
        long transactionNumber = ThreadLocalRandom.current().nextLong(100_000_000_000L, 1_000_000_000_000L);
        //creates the transaction object
        TransactionModel transactionModel = new TransactionModel(transactionNumber, type, amount, originAccountId, destinationAccountId);

        //done to make sure transaction information is valid
        if(transactionModel.getType().equals("Deposit") || transactionModel.getType().equals("Withdraw")){
            System.out.println("Wrong service method chosen, use attemptAddDepositOrWithdrawal with type, amount, and originAccountId as parameters.");
            logger.warn("Deposit or withdraw type used for transfer method");
            return false;
        }

        if(!transactionModel.getType().equals("Transfer")){
            System.out.println("Invalid Transaction type");
            logger.warn("random stuff typed for the transaction type for transfer");
            return false;
        }

        if(transactionModel.getAmount() <= 0.00){
            System.out.println("Amount must be greater than $0.00");
            logger.warn("amount less than zero for the transaction");
            return false;
        }
        //will either return true or false
        return TransactionModelRepository.addTransfer(transactionModel);
    }


}
