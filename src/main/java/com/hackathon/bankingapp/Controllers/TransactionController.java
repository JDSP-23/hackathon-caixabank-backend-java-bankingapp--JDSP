package com.hackathon.bankingapp.Controllers;

import com.hackathon.bankingapp.DTO.TransactionCreateDTO;
import com.hackathon.bankingapp.DTO.TransactionCreateTransferDTO;
import com.hackathon.bankingapp.Entities.Transaction;
import com.hackathon.bankingapp.Repositories.*;
import com.hackathon.bankingapp.Security.AuthService;
import com.hackathon.bankingapp.Services.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/account/")
public class TransactionController {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PinRepository pinRepository;
    private final TransactionRepository transactionRepository;
    private final AuthService authService;

    public TransactionController(UserRepository userRepository, AccountRepository accountRepository, PinRepository pinRepository, TransactionRepository transactionRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.pinRepository = pinRepository;
        this.transactionRepository = transactionRepository;
        this.authService = authService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody TransactionCreateDTO transactionCreateDTO) {


        String identifier = JwtUtil.extractUsername(authService.getToken());
        var pin = pinRepository.findByIdentifier(identifier);
        if (!pin.getPin().equals(transactionCreateDTO.getPin())) {
            return ResponseEntity.badRequest().body("Invalid PIN");
        }

        var user = userRepository.findByEmail(identifier);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        var account = accountRepository.findByAccountNumber(user.getAccountNumber());
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        account.setBalance(account.getBalance() + transactionCreateDTO.getAmount());
        accountRepository.save(account);

        addTransaction(account.getAccountNumber(), "N/A", transactionCreateDTO.getAmount(), "CASH_DEPOSIT");

        Map<String, String> response = new HashMap<>();
        response.put("msg", "Cash deposited successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody TransactionCreateDTO transactionCreateDTO) {


        String identifier = JwtUtil.extractUsername(authService.getToken());
        var pin = pinRepository.findByIdentifier(identifier);
        if (!pin.getPin().equals(transactionCreateDTO.getPin())) {
            return ResponseEntity.badRequest().body("Invalid PIN");
        }

        var user = userRepository.findByEmail(identifier);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        var account = accountRepository.findByAccountNumber(user.getAccountNumber());
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        if (account.getBalance() < transactionCreateDTO.getAmount()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient balance");
        }

        account.setBalance(account.getBalance() - transactionCreateDTO.getAmount());
        accountRepository.save(account);

        addTransaction(account.getAccountNumber(), "N/A", transactionCreateDTO.getAmount(), "CASH_WITHDRAWAL");

        Map<String, String> response = new HashMap<>();
        response.put("msg", "Cash withdrawn successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/fund-transfer")
    public ResponseEntity<?> fundTransfer(@RequestBody TransactionCreateTransferDTO transactionCreateTransferDTO) {

        String identifier = JwtUtil.extractUsername(authService.getToken());
        var pin = pinRepository.findByIdentifier(identifier);
        if (!pin.getPin().equals(transactionCreateTransferDTO.getPin())) {
            return ResponseEntity.badRequest().body("Invalid PIN");
        }

        var user = userRepository.findByEmail(identifier);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        var account = accountRepository.findByAccountNumber(user.getAccountNumber());
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        var targetAccount = accountRepository.findByAccountNumber(transactionCreateTransferDTO.getTargetAccountNumber());
        if (targetAccount == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Target account not found");
        }

        if (account.getBalance() < transactionCreateTransferDTO.getAmount()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient balance");
        }

        account.setBalance(account.getBalance() - transactionCreateTransferDTO.getAmount());
        targetAccount.setBalance(targetAccount.getBalance() + transactionCreateTransferDTO.getAmount());
        accountRepository.save(account);
        accountRepository.save(targetAccount);

        addTransaction(account.getAccountNumber(), targetAccount.getAccountNumber(), transactionCreateTransferDTO.getAmount(), "CASH_TRANSFER");

        Map<String, String> response = new HashMap<>();
        response.put("msg", "Fund transferred successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions() {


        String identifier = JwtUtil.extractUsername(authService.getToken());
        var user = userRepository.findByEmail(identifier);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        var account = accountRepository.findByAccountNumber(user.getAccountNumber());
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        List<Transaction> transactions = transactionRepository.findAllBySourceAccountNumber(account.getAccountNumber());
        return ResponseEntity.ok(transactions);
    }

    private void addTransaction(String account, String targetAccount, double amount, String type) {
        var transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType(type);
        transaction.setTransactionDate(System.currentTimeMillis());
        transaction.setSourceAccountNumber(account);
        transaction.setTargetAccountNumber(targetAccount);
        transactionRepository.save(transaction);
    }
}
