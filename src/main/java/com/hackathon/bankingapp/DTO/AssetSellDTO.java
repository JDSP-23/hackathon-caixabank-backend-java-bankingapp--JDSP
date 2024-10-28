package com.hackathon.bankingapp.DTO;

public class AssetSellDTO {

    private String assetSymbol;
    private String pin;
    private double quantity;

    public AssetSellDTO() {
    }

    public AssetSellDTO(String assetSymbol, String pin, double quantity) {
        this.assetSymbol = assetSymbol;
        this.pin = pin;
        this.quantity = quantity;
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

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}
