package com.hackathon.bankingapp.Controllers;

import com.hackathon.bankingapp.DTO.PinCreateDTO;
import com.hackathon.bankingapp.DTO.PinEditDTO;
import com.hackathon.bankingapp.Entities.Pin;
import com.hackathon.bankingapp.Repositories.AccountRepository;
import com.hackathon.bankingapp.Repositories.OtpRepository;
import com.hackathon.bankingapp.Repositories.PinRepository;
import com.hackathon.bankingapp.Repositories.UserRepository;
import com.hackathon.bankingapp.Security.AuthService;
import com.hackathon.bankingapp.Services.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/account/pin")
public class PinController {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final OtpRepository otpRepository;
    private final PinRepository pinRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public PinController(UserRepository userRepository, PasswordEncoder passwordEncoder, AccountRepository accountRepository, OtpRepository otpRepository, PinRepository pinRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.otpRepository = otpRepository;
        this.pinRepository = pinRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody PinCreateDTO pinCreateDTO) {

        String identifier = JwtUtil.extractUsername(authService.getToken());

        var pin = pinRepository.findByIdentifier(identifier);
        if (pin != null) {
            return ResponseEntity.badRequest().body("Pin already exists.");
        }

        var user = userRepository.findByEmail(identifier);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found.");
        }

        if (!passwordEncoder.matches(pinCreateDTO.getPassword(), user.getHashedPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bad credentials");
        }

        Pin newPin = new Pin();
        newPin.setPin(pinCreateDTO.getPin());
        newPin.setIdentifier(identifier);
        pinRepository.save(newPin);

        Map<String, String> response = new HashMap<>();
        response.put("msg", "PIN created successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody PinEditDTO pinEditDTO) {


        String identifier = JwtUtil.extractUsername(authService.getToken());

        var pin = pinRepository.findByIdentifier(identifier);
        if (pin == null) {
            return ResponseEntity.badRequest().body("Pin not exists");
        }

        if (!pin.getPin().equals(pinEditDTO.getOldPin())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Current pin is incorrect");
        }

        var user = userRepository.findByEmail(identifier);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!passwordEncoder.matches(pinEditDTO.getPassword(), user.getHashedPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bad credentials");
        }

        pin.setPin(pinEditDTO.getNewPin());
        pinRepository.save(pin);

        Map<String, String> response = new HashMap<>();
        response.put("msg", "PIN updated successfully");
        return ResponseEntity.ok(response);
    }
}
