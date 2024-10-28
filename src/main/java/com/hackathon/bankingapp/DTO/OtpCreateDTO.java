package com.hackathon.bankingapp.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class OtpCreateDTO {
    @NotBlank(message = "Identifier cannot be blank")
    @Email(message = "Identifier must be a valid email format")
    private String identifier;

    public OtpCreateDTO() {
    }

    public OtpCreateDTO(String identifier) {
        this.identifier = identifier;
    }

    public @NotBlank() @Email() String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(@NotBlank() @Email() String identifier) {
        this.identifier = identifier;
    }
}
