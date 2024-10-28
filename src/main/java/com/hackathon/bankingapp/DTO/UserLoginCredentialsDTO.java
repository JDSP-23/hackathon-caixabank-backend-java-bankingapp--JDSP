package com.hackathon.bankingapp.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.regex.Pattern;

public class UserLoginCredentialsDTO {
    @NotBlank(message = "Email cannot be blank")
    private String identifier;
    @NotBlank(message = "Password cannot be blank")
    private String password;

    public UserLoginCredentialsDTO() {
    }

    public UserLoginCredentialsDTO(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
