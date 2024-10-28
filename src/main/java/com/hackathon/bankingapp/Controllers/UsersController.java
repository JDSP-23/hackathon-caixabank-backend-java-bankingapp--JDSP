package com.hackathon.bankingapp.Controllers;

import com.hackathon.bankingapp.DTO.*;
import com.hackathon.bankingapp.Entities.Account;
import com.hackathon.bankingapp.Entities.User;
import com.hackathon.bankingapp.Repositories.AccountRepository;
import com.hackathon.bankingapp.Repositories.UserRepository;
import com.hackathon.bankingapp.Security.AuthService;
import com.hackathon.bankingapp.Services.JwtUtil;
import com.hackathon.bankingapp.Utils.UserMapper;
import jakarta.validation.Valid;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static java.util.UUID.randomUUID;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final UserMapper userMapper = new UserMapper();
    private final AuthService authService;

    private static Logger logger = Logger.getLogger(UsersController.class.getName());

    UsersController(UserRepository userRepository, AccountRepository accountRepository, PasswordEncoder passwordEncoder, AuthService authService) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> create(@Valid @RequestBody UserCreateDTO userCreateDTO) {


        String message = userCreateDTO.validatePassword(userCreateDTO.getPassword());
        if (message != null) {
            return ResponseEntity.badRequest().body(message);

        }

        message = userCreateDTO.validateEmail(userCreateDTO.getEmail());
        if (message != null) {
            return ResponseEntity.badRequest().body(message);

        }

        if (userRepository.existsByEmail(userCreateDTO.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");

        }

        if (userRepository.existsByPhoneNumber(userCreateDTO.getPhoneNumber())) {
            return ResponseEntity.badRequest().body("Phone number already exists");

        }

        User user = userMapper.toEntity(userCreateDTO);
        user.setAccountNumber(randomUUID().toString());
        user.setHashedPassword(passwordEncoder.encode(userCreateDTO.getPassword()));

        userRepository.save(user);

        Account account = new Account();
        account.setBalance(0.0);
        account.setAccountNumber(user.getAccountNumber());
        account.setIdentifier(user.getEmail());

        accountRepository.save(account);

        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginCredentialsDTO userLoginCredentialsDTO) {

        var user = userRepository.findByEmail(userLoginCredentialsDTO.getIdentifier());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found for the given identifier: " + userLoginCredentialsDTO.getIdentifier());
        }

        if (!passwordEncoder.matches(userLoginCredentialsDTO.getPassword(), user.getHashedPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bad credentials");
        }

        String token = JwtUtil.generateToken(userLoginCredentialsDTO.getIdentifier());
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        JwtUtil.revokeToken(authService.getToken());

        return ResponseEntity.ok().build();
    }
}
