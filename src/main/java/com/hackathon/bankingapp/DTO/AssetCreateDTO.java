package com.hackathon.bankingapp.DTO;

public class AssetCreateDTO {

    private String assetSymbol;
    private String pin;
    private double amount;

    public AssetCreateDTO() {
    }

    public AssetCreateDTO(String assetSymbol, String pin, double amount) {
        this.assetSymbol = assetSymbol;
        this.pin = pin;
        this.amount = amount;
    }

    public String getAssetSymbol() {
        return assetSymbol;
    }

    public void setAssetSymbol(String assetSymbol) {
        this.assetSymbol = assetSymbol;
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
