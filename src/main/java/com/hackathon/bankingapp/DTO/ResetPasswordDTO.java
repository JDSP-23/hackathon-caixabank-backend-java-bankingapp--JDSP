package com.hackathon.bankingapp.DTO;

import java.util.regex.Pattern;

public class ResetPasswordDTO {
    private String identifier;
    private String resetToken;
    private String newPassword;

    public ResetPasswordDTO() {
    }

    public ResetPasswordDTO(String identifier, String resetToken, String newPassword) {
        this.identifier = identifier;
        this.resetToken = resetToken;
        this.newPassword = newPassword;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void validatePassword() {
        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        if (newPassword.length() > 128) {
            throw new IllegalArgumentException("Password must be less than 128 characters long");
        }

        if (newPassword.contains(" ")) {
            throw new IllegalArgumentException("Password cannot contain whitespace");
        }

        if (!Pattern.compile("[A-Z]").matcher(newPassword).find()) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }

        boolean hasDigit = Pattern.compile("[0-9]").matcher(newPassword).find();

        boolean hasSpecialCharacter = Pattern.compile("[^a-zA-Z0-9]").matcher(newPassword).find();

        if (!hasDigit && !hasSpecialCharacter) {
            throw new IllegalArgumentException("Password must contain at least one digit and one special character");
        }

        if (!hasDigit) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }

        if (!hasSpecialCharacter) {
            throw new IllegalArgumentException("Password must contain at least one special character");
        }
    }
}
