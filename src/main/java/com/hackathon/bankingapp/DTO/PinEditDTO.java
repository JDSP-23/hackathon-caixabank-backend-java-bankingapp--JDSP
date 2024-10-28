package com.hackathon.bankingapp.DTO;

import jakarta.validation.constraints.NotBlank;

public class PinEditDTO {

    @NotBlank(message = "Pin cannot be blank")
    private String oldPin;
    @NotBlank(message = "Password cannot be blank")
    private String password;
    @NotBlank(message = "NewPin cannot be blank")
    private String newPin;

    public PinEditDTO() {
    }

    public PinEditDTO(String oldPin, String password, String newPin) {
        this.oldPin = oldPin;
        this.password = password;
        this.newPin = newPin;
    }

    public @NotBlank(message = "Pin cannot be blank") String getOldPin() {
        return oldPin;
    }

    public void setOldPin(@NotBlank(message = "Pin cannot be blank") String oldPin) {
        this.oldPin = oldPin;
    }

    public @NotBlank(message = "Password cannot be blank") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "Password cannot be blank") String password) {
        this.password = password;
    }

    public @NotBlank(message = "NewPin cannot be blank") String getNewPin() {
        return newPin;
    }

    public void setNewPin(@NotBlank(message = "NewPin cannot be blank") String newPin) {
        this.newPin = newPin;
    }
}
