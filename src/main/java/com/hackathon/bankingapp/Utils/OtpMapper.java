package com.hackathon.bankingapp.Utils;

import com.hackathon.bankingapp.DTO.OtpCreateDTO;
import com.hackathon.bankingapp.DTO.OtpDTO;
import com.hackathon.bankingapp.Entities.Otp;

public class OtpMapper {

    // Convierte la entidad Otp a OtpDTO
    public OtpDTO toDTO(Otp otp) {
        if (otp == null) {
            return null;
        }
        return new OtpDTO(
                otp.getIdentifier(),
                otp.getOtp()
        );
    }

    // Convierte OtpCreateDTO a la entidad Otp
    public Otp toEntity(OtpCreateDTO otpCreateDTO) {
        if (otpCreateDTO == null) {
            return null;
        }
        return new Otp(
                null, // id es null ya que se generará automáticamente
                generateOtp(), // Aquí puedes llamar a un método para generar el OTP
                otpCreateDTO.getIdentifier()
        );
    }

    // Método para generar un OTP (puedes implementarlo como desees)
    private String generateOtp() {
        // Genera un OTP aleatorio (por simplicidad, aquí se usa un número de 6 dígitos)
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }
}
