package com.hackathon.bankingapp.DTO;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.validation.constraints.*;

import java.util.regex.Pattern;

public class UserCreateDTO {

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank(message = "Address cannot be blank")
    private String address;

    @NotBlank(message = "Phone number cannot be blank")
    private String phoneNumber;

    public UserCreateDTO() {
    }

    public UserCreateDTO(String name, String password, String email, String address, String phoneNumber) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        if (!isValidEmail(email)) {
            return "Invalid email: " + email;
        }

        return null;
    }

    public String validatePassword(String password) {
        if (!hasMinLength(password)) {
            return "Password must be at least 8 characters long";
        }
        if (!hasMaxLength(password)) {
            return "Password must be less than 128 characters long";
        }
        if (containsWhitespace(password)) {
            return "Password cannot contain whitespace";
        }
        if (!hasUppercaseLetter(password)) {
            return "Password must contain at least one uppercase letter";
        }
        if (!hasDigit(password)) {
            return "Password must contain at least one digit and one special character";
        }
        if (!hasSpecialCharacter(password)) {
            return "Password must contain at least one special character";
        }

        return null;
    }

    private boolean isValidEmail(String email) {
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
            return true;
        } catch (AddressException ex) {
            return false;
        }
    }

    private boolean hasMinLength(String password) {
        return password.length() >= 8;
    }

    private boolean hasMaxLength(String password) {
        return password.length() < 128;
    }

    private boolean containsWhitespace(String password) {
        return password.contains(" ");
    }

    private boolean hasUppercaseLetter(String password) {
        return password.chars().anyMatch(Character::isUpperCase);
    }

    private boolean hasDigit(String password) {
        return password.chars().anyMatch(Character::isDigit);
    }

    private boolean hasSpecialCharacter(String password) {
        return password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch));
    }


}
