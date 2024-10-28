package com.hackathon.bankingapp.DTO;

public class TransactionCreateDTO {
    private String pin;
    private double amount;

    public TransactionCreateDTO() {
    }

    public TransactionCreateDTO(String pin, double amount) {
        this.pin = pin;
        this.amount = amount;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
