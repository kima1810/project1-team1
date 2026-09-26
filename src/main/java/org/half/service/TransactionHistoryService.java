package org.half.service;

import org.half.model.TransactionModel;
import org.half.repository.TransactionModelRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TransactionHistoryService {
    //logger
    private static final Logger logger = LoggerFactory.getLogger(TransactionHistoryService.class);

    private final TransactionModelRepository transactionModelRepository;

    public TransactionHistoryService(TransactionModelRepository transactionModelRepository) {
        this.transactionModelRepository = transactionModelRepository;
    }

    //runs the repository method to create a deposit or withdrawal
    public boolean attemptAddDepositOrWithdrawal(String type, double amount, long originAccountId){
        //creates the transaction object
        TransactionModel transactionModel = new TransactionModel(type, amount, originAccountId);

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
        return transactionModelRepository.addDepositOrWithdrawal(transactionModel);
    }

    //runs the repository method to create a transfer
    public boolean attemptAddTransfer(String type, double amount, long originAccountId, long destinationAccountId){
        //creates the transaction object
        TransactionModel transactionModel = new TransactionModel(type, amount, originAccountId, destinationAccountId);

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
        return transactionModelRepository.addTransfer(transactionModel);
    }
}
