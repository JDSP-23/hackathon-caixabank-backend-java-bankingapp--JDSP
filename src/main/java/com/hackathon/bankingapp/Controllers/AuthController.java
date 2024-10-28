package com.hackathon.bankingapp.Controllers;

import com.hackathon.bankingapp.DTO.*;
import com.hackathon.bankingapp.Entities.Otp;
import com.hackathon.bankingapp.Repositories.AccountRepository;
import com.hackathon.bankingapp.Repositories.OtpRepository;
import com.hackathon.bankingapp.Repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static java.util.UUID.randomUUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, AccountRepository accountRepository, OtpRepository otpRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
        this.otpRepository = otpRepository;
    }

    @PostMapping("/password-reset/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody OtpCreateDTO otpCreateDTO) {
        var user = userRepository.findByEmail(otpCreateDTO.getIdentifier());

        if (user == null) {
            return ResponseEntity.noContent().build();
        }

        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
        Otp otpEntity = new Otp();
        otpEntity.setOtp(otp);
        otpEntity.setIdentifier(user.getEmail());

        String message = "OTP:" + otp;

        sendEmail(user.getEmail(), message);

        otpRepository.save(otpEntity);

        return ResponseEntity.ok("OTP sent successfully to: " + otpCreateDTO.getIdentifier());
    }

    //test2
    @PostMapping("/password-reset/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpDTO otpDTO) {
        var otp = otpRepository.findByOtp(otpDTO.getOtp());
        if (otp == null || otp.getPasswordResetToken() != null) {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }

        String resetToken = randomUUID().toString();
        otp.setPasswordResetToken(resetToken);
        otpRepository.save(otp);

        Map<String, String> response = new HashMap<>();
        response.put("passwordResetToken", resetToken);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/password-reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        try {
            resetPasswordDTO.validatePassword();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        var otp = otpRepository.findByPasswordResetToken(resetPasswordDTO.getResetToken());
        if (otp == null) {
            return ResponseEntity.badRequest().body("Invalid reset token");
        }
        var user = userRepository.findByEmail(otp.getIdentifier());
        if (user == null) {
            return ResponseEntity.badRequest().body("Invalid reset token");
        }


        user.setHashedPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        userRepository.save(user);

        otpRepository.delete(otp);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset successfully");
        return ResponseEntity.ok(response);
    }

    private void sendEmail(String to, String message) {
        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setTo(to);
        emailMessage.setSubject("Password Reset OTP");
        emailMessage.setText(message);
        mailSender.send(emailMessage);
    }
}
