package org.half.model;
import org.half.view.TransactionHistory;

public class TransactionModel {
    private String dateTime;
    private String type;
    private double amount;
    private long originAccountId;
    private long destinationAccountId;

    public TransactionModel(String dateTime, String type, double amount, long originAccountId, long destinationAccountId){
        this.dateTime = dateTime;
        this.type = type;
        this.amount = amount;
        this.originAccountId = originAccountId;
        this.destinationAccountId = destinationAccountId;
    }

    public TransactionModel(String type, double amount, long originAccountId, long destinationAccountId){
        this.dateTime = null;
        this.type = type;
        this.amount = amount;
        this.originAccountId = originAccountId;
        this.destinationAccountId = destinationAccountId;
    }

    public TransactionModel(String type, double amount, long originAccountId){
        this.dateTime = null;
        this.type = type;
        this.amount = amount;
        this.originAccountId = originAccountId;
        this.destinationAccountId = -1;
    }

    //getters
    public String getDateTime() {
        return dateTime;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public long getOriginAccountId() {
        return originAccountId;
    }

    public long getDestinationAccountId() {
        return destinationAccountId;
    }

    //setters

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setOriginAccountId(long originAccountId) {
        this.originAccountId = originAccountId;
    }

    public void setDestinationAccountId(long destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
    }
}
