package com.hackathon.bankingapp.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "otps")
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "otp", nullable = false)
    private String otp;

    @Column(name = "identifier", nullable = false, unique = true)
    private String identifier;

    @Column(name = "password_reset_token", nullable = true)
    private String passwordResetToken;

    public Otp() {
    }

    public Otp(String otp, String identifier, String passwordResetToken) {
        this.otp = otp;
        this.identifier = identifier;
        this.passwordResetToken = passwordResetToken;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPasswordResetToken() {
        return passwordResetToken;
    }

    public void setPasswordResetToken(String passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
    }
}
