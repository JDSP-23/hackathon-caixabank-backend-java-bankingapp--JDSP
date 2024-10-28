package com.hackathon.bankingapp.DTO;

public class TransactionCreateTransferDTO {

    private double amount;
    private String pin;
    private String targetAccountNumber;

    public TransactionCreateTransferDTO() {
    }

    public TransactionCreateTransferDTO(double amount, String pin, String targetAccountNumber) {
        this.amount = amount;
        this.pin = pin;
        this.targetAccountNumber = targetAccountNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getTargetAccountNumber() {
        return targetAccountNumber;
    }

    public void setTargetAccountNumber(String targetAccountNumber) {
        this.targetAccountNumber = targetAccountNumber;
    }
}
