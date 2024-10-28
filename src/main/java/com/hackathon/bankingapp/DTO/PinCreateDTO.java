package com.hackathon.bankingapp.DTO;

import jakarta.validation.constraints.NotBlank;

public class PinCreateDTO {

    @NotBlank(message = "Identifier cannot be blank")
    private String pin;
    @NotBlank(message = "Identifier cannot be blank")
    private String password;

    public PinCreateDTO() {
    }

    public PinCreateDTO(String pin, String password) {
        this.pin = pin;
        this.password = password;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
