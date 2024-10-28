package com.hackathon.bankingapp.Controllers;

import com.hackathon.bankingapp.DTO.AutoInvestCreateDTO;
import com.hackathon.bankingapp.DTO.SubscriptionCreateDTO;
import com.hackathon.bankingapp.Entities.AutoInvest;
import com.hackathon.bankingapp.Entities.Subscription;
import com.hackathon.bankingapp.Repositories.*;
import com.hackathon.bankingapp.Security.AuthService;
import com.hackathon.bankingapp.Services.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/user-actions")
public class UserActionsController {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PinRepository pinRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final AutoInvestRepository autoInvestRepository;
    private final AuthService authService;

    public UserActionsController(UserRepository userRepository, AccountRepository accountRepository, PinRepository pinRepository, SubscriptionRepository subscriptionRepository, AutoInvestRepository autoInvestRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.autoInvestRepository = autoInvestRepository;
        this.pinRepository = pinRepository;
        this.authService = authService;

    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody SubscriptionCreateDTO subscriptionCreateDTO) {


        var identifier = JwtUtil.extractUsername(authService.getToken());
        var user = userRepository.findByEmail(identifier);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        var pin = pinRepository.findByIdentifier(identifier);
        if (pin == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pin not found");
        }
        if (!pin.getPin().equals(subscriptionCreateDTO.getPin())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid PIN");
        }

        var subscription = new Subscription(Double.parseDouble(subscriptionCreateDTO.getAmount()), subscriptionCreateDTO.getIntervalSeconds(), user.getAccountNumber());
        subscription.setNextPaymentTime(LocalDateTime.now().plusSeconds(subscription.getIntervalSeconds()));
        subscriptionRepository.save(subscription);

        return ResponseEntity.ok("Subscription created successfully.");
    }

    @PostMapping("/enable-auto-invest")
    public ResponseEntity<?> autoInvest(@RequestBody AutoInvestCreateDTO autoInvestCreateDTO) {


        if (autoInvestCreateDTO.getPin() == null || autoInvestCreateDTO.getPin().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("PIN cannot be null or empty");
        }

        var identifier = JwtUtil.extractUsername(authService.getToken());
        var user = userRepository.findByEmail(identifier);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        var pin = pinRepository.findByIdentifier(identifier);
        if (pin == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pin not found");
        }
        if (!pin.getPin().equals(autoInvestCreateDTO.getPin())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid PIN");
        }

        // Enable auto-invest
        var autoInvest = new AutoInvest(user.getEmail());
        autoInvestRepository.save(autoInvest);

        return ResponseEntity.ok("Automatic investment enabled successfully.");
    }
}
