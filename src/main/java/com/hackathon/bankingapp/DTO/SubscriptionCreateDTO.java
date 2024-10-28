package com.hackathon.bankingapp.DTO;

import jakarta.validation.constraints.NotBlank;

public class SubscriptionCreateDTO {
    @NotBlank(message = "Identifier cannot be blank")
    private String pin;
    private String amount;
    private int intervalSeconds;

    public SubscriptionCreateDTO() {
    }

    public SubscriptionCreateDTO(String pin, String amount, int intervalSeconds) {
        this.pin = pin;
        this.amount = amount;
        this.intervalSeconds = intervalSeconds;
    }

    public @NotBlank(message = "Identifier cannot be blank") String getPin() {
        return pin;
    }

    public void setPin(@NotBlank(message = "Identifier cannot be blank") String pin) {
        this.pin = pin;
    }


    public String getAmount() {
        return this.amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getIntervalSeconds() {
        return intervalSeconds;
    }

    public void setIntervalSeconds(int intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }
}
