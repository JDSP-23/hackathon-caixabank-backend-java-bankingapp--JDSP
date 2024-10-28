package com.hackathon.bankingapp.DTO;

public class AutoInvestCreateDTO {
    private String pin;

    public AutoInvestCreateDTO() {
    }

    public AutoInvestCreateDTO(String pin) {
        this.pin = pin;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
