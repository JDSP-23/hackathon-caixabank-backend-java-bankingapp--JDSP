package com.hackathon.bankingapp.Controllers;

import com.hackathon.bankingapp.Repositories.AccountRepository;
import com.hackathon.bankingapp.Repositories.UserRepository;
import com.hackathon.bankingapp.Security.AuthService;
import com.hackathon.bankingapp.Services.JwtUtil;
import com.hackathon.bankingapp.Utils.AccountMapper;
import com.hackathon.bankingapp.Utils.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final UserMapper userMapper = new UserMapper();
    private final AccountMapper accountMapper = new AccountMapper();

    private final AuthService authService;

    DashboardController(UserRepository userRepository, AccountRepository accountRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.authService = authService;
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser() {


        String username = JwtUtil.extractUsername(authService.getToken());
        var user = userRepository.findByEmail(username);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @GetMapping("/account")
    public ResponseEntity<?> getAccount() {

        String username = JwtUtil.extractUsername(authService.getToken());
        var user = userRepository.findByEmail(username);
        var account = accountRepository.findByAccountNumber(user.getAccountNumber());
        return ResponseEntity.ok(accountMapper.toAccountDTO(account));
    }
}
