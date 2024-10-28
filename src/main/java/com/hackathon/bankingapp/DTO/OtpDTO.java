package com.hackathon.bankingapp.DTO;

public class OtpDTO {
    private String identifier;
    private String otp;

    public OtpDTO() {
    }

    public OtpDTO(String identifier, String otp) {
        this.identifier = identifier;
        this.otp = otp;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }


}
