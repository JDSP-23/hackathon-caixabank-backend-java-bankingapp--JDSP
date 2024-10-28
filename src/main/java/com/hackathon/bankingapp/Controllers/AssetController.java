package com.hackathon.bankingapp.Controllers;

import com.hackathon.bankingapp.DTO.AssetCreateDTO;
import com.hackathon.bankingapp.DTO.AssetSellDTO;
import com.hackathon.bankingapp.Entities.Asset;
import com.hackathon.bankingapp.Entities.Account;
import com.hackathon.bankingapp.Entities.User;
import com.hackathon.bankingapp.Entities.Pin;
import com.hackathon.bankingapp.Entities.Transaction;
import com.hackathon.bankingapp.Repositories.AccountRepository;
import com.hackathon.bankingapp.Repositories.AssetRepository;
import com.hackathon.bankingapp.Repositories.OtpRepository;
import com.hackathon.bankingapp.Repositories.PinRepository;
import com.hackathon.bankingapp.Repositories.TransactionRepository;
import com.hackathon.bankingapp.Repositories.UserRepository;
import com.hackathon.bankingapp.Security.AuthService;
import com.hackathon.bankingapp.Services.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.UUID.randomUUID;

@RestController
@RequestMapping("/api/account/")
public class AssetController {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final OtpRepository otpRepository;
    private final PinRepository pinRepository;
    private final TransactionRepository transactionRepository;
    private final AssetRepository assetRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private RestTemplate restTemplate;

    private final AuthService authService;

    public AssetController(UserRepository userRepository, PasswordEncoder passwordEncoder, AccountRepository accountRepository, OtpRepository otpRepository, PinRepository pinRepository, TransactionRepository transactionRepository, AssetRepository assetRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
        this.pinRepository = pinRepository;
        this.transactionRepository = transactionRepository;
        this.assetRepository = assetRepository;
        this.authService = authService;
    }

    @PostMapping("/buy-asset")
    public ResponseEntity<?> buyAsset(@RequestBody AssetCreateDTO assetCreateDTO) {

        String identifier = JwtUtil.extractUsername(authService.getToken());
        Pin pin = pinRepository.findByIdentifier(identifier);

        if (!pin.getPin().equals(assetCreateDTO.getPin())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid PIN");
        }

        Map<String, Double> marketPrices = getDataFromApi();
        Double assetPrice = marketPrices.get(assetCreateDTO.getAssetSymbol());
        if (assetPrice == null) {
            return ResponseEntity.internalServerError().body("Internal error occurred while purchasing the asset.");
        }
        double totalAmount = assetCreateDTO.getAmount() / assetPrice;

        User user = userRepository.findByEmail(identifier);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Account account = accountRepository.findByAccountNumber(user.getAccountNumber());
        if (account == null || account.getBalance() < assetCreateDTO.getAmount()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Internal error occurred while purchasing the asset.");
        }

        account.setBalance(account.getBalance() - assetCreateDTO.getAmount());
        accountRepository.save(account);

        Asset asset = assetRepository.findByAssetSymbolAndIdentifier(assetCreateDTO.getAssetSymbol(), identifier);
        if (asset == null) {
            asset = new Asset();
            asset.setAssetSymbol(assetCreateDTO.getAssetSymbol());
            asset.setIdentifier(identifier);
        }
        asset.setAmount(asset.getAmount() + totalAmount);
        asset.setCost(assetPrice);
        assetRepository.save(asset);

        addTransaction(account.getAccountNumber(), "N/A", assetCreateDTO.getAmount(), "ASSET_PURCHASE");

        StringBuilder currentAssets = new StringBuilder();
        double totalAssets = 0.0;
        for (Asset a : assetRepository.findByIdentifier(identifier)) {
            if (a.getAmount() > 0.0) {
                totalAssets += marketPrices.get(a.getAssetSymbol()) * a.getAmount();
                BigDecimal amountBigDecimal = BigDecimal.valueOf(a.getAmount());
                BigDecimal costBigDecimal = BigDecimal.valueOf(a.getCost());

                currentAssets.append(String.format("- %s: %s units purchased at $%s\n", a.getAssetSymbol(), amountBigDecimal.toPlainString(), costBigDecimal.toPlainString()));
            }
        }

        BigDecimal quantitySold = BigDecimal.valueOf(totalAmount);
        BigDecimal quantityAmount = BigDecimal.valueOf(asset.getAmount());

        String message = String.format("Dear %s,\n\n" + "You have successfully purchased %.2f units of %s for a total amount of $%.2f.\n\n" + "Current holdings of %s: %s units\n\n" + "Summary of current assets:\n%s\n" + "Account Balance: $%.2f\n" + "Net Worth: $%.2f\n\n" + "Thank you for using our investment services.\n\n" + "Best Regards,\n" + "Investment Management Team", user.getName(), quantitySold, asset.getAssetSymbol(), assetCreateDTO.getAmount(), asset.getAssetSymbol(), quantityAmount, currentAssets.toString(), account.getBalance(), account.getBalance() + totalAssets);
        sendEmail(user.getEmail(), "Investment Purchase Confirmation", message);
        return ResponseEntity.ok("Asset purchase successful.");
    }

    @PostMapping("/sell-asset")
    public ResponseEntity<?> sellAsset(@RequestBody AssetSellDTO assetSellDTO) {

        String identifier = JwtUtil.extractUsername(authService.getToken());
        Pin pin = pinRepository.findByIdentifier(identifier);

        if (!pin.getPin().equals(assetSellDTO.getPin())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid PIN");
        }

        Map<String, Double> marketPrices = getDataFromApi();
        Double assetPrice = marketPrices.get(assetSellDTO.getAssetSymbol());

        if (assetPrice == null) {
            return ResponseEntity.internalServerError().body("Internal error occurred while selling the asset.");
        }

        User user = userRepository.findByEmail(identifier);
        Account account = accountRepository.findByAccountNumber(user.getAccountNumber());
        Asset asset = assetRepository.findByAssetSymbolAndIdentifier(assetSellDTO.getAssetSymbol(), identifier);

        if (user == null || account == null || asset == null || asset.getAmount() < assetSellDTO.getQuantity()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Internal error occurred while selling the asset.");
        }

        double totalSaleAmount = assetPrice * assetSellDTO.getQuantity();
        account.setBalance(account.getBalance() + totalSaleAmount);
        accountRepository.save(account);

        asset.setAmount(asset.getAmount() - assetSellDTO.getQuantity());
        assetRepository.save(asset);

        addTransaction(account.getAccountNumber(), "N/A", totalSaleAmount, "ASSET_SELL");

        double purchasePrice = asset.getCost() / asset.getAmount();
        double gainOrLoss = calculateGainOrLossAmount(assetPrice, purchasePrice, assetSellDTO.getQuantity());

        StringBuilder currentAssets = new StringBuilder();
        double totalAssets = 0.0;
        for (Asset a : assetRepository.findByIdentifier(identifier)) {
            if (a.getAmount() > 0.0) {
                totalAssets += marketPrices.get(a.getAssetSymbol()) * a.getAmount();
                BigDecimal amountBigDecimal = BigDecimal.valueOf(a.getAmount());
                BigDecimal costBigDecimal = BigDecimal.valueOf(a.getCost());

                currentAssets.append(String.format("- %s: %s units purchased at $%s\n", a.getAssetSymbol(), amountBigDecimal.toPlainString(), costBigDecimal.toPlainString()));
            }
        }
        BigDecimal quantitySold = BigDecimal.valueOf(assetSellDTO.getQuantity());
        BigDecimal quantityAmount = BigDecimal.valueOf(asset.getAmount());
        String message = String.format("Dear %s,\n\n" + "You have successfully sold %.2f units of %s.\n\n" + "Total Gain/Loss: $%.2f\n\n" + "Remaining holdings of %s: %s units\n\n" + "Summary of current assets:\n%s\n" + "Account Balance: $%.2f\n" + "Net Worth: $%.2f\n\n" + "Thank you for using our investment services.\n\n" + "Best Regards,\n" + "Investment Management Team", user.getName(), quantitySold, asset.getAssetSymbol(), gainOrLoss, asset.getAssetSymbol(), quantityAmount, currentAssets.toString(), account.getBalance(), account.getBalance() + totalAssets);
        sendEmail(user.getEmail(), "Investment Sale Confirmation", message);
        return ResponseEntity.ok("Asset sale successful.");
    }

    @GetMapping("/net-worth")
    public ResponseEntity<?> getNetWorth() {


        String identifier = JwtUtil.extractUsername(authService.getToken());
        User user = userRepository.findByEmail(identifier);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Account account = accountRepository.findByAccountNumber(user.getAccountNumber());
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        List<Asset> assets = assetRepository.findByIdentifier(identifier);
        double netWorth = account.getBalance();
        for (Asset asset : assets) {
            Map<String, Double> marketPrices = getDataFromApi();
            Double assetPrice = marketPrices.get(asset.getAssetSymbol());
            if (assetPrice == null) {
                return ResponseEntity.internalServerError().body("Internal error occurred while calculating net worth.");
            }
            netWorth += assetPrice * asset.getAmount();
        }

        return ResponseEntity.ok(netWorth);
    }

    @GetMapping("/assets")
    public ResponseEntity<?> getAssets() {

        String identifier = JwtUtil.extractUsername(authService.getToken());
        User user = userRepository.findByEmail(identifier);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        List<Asset> assets = assetRepository.findByIdentifier(identifier);
        Map<String, Double> assetMap = new HashMap<>();
        for (Asset asset : assets) {
            assetMap.put(asset.getAssetSymbol(), asset.getAmount());
        }

        return ResponseEntity.ok(assetMap);
    }

    @GetMapping("/assets/{symbol}")
    public ResponseEntity<?> getAsset(@PathVariable String symbol) {


        String identifier = JwtUtil.extractUsername(authService.getToken());
        User user = userRepository.findByEmail(identifier);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Asset asset = assetRepository.findByAssetSymbolAndIdentifier(symbol, identifier);
        if (asset == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Asset not found");
        }

        Map<String, Double> assetMap = new HashMap<>();
        assetMap.put(symbol, asset.getAmount());

        return ResponseEntity.ok(assetMap);
    }

    private double calculateGainOrLossAmount(double sellingPrice, double purchasePrice, double amountSold) {
        double totalSellingPrice = sellingPrice * amountSold;
        double totalPurchasePrice = purchasePrice * amountSold;
        return totalSellingPrice - totalPurchasePrice;
    }

    private Map<String, Double> getDataFromApi() {
        return restTemplate.getForObject("https://faas-lon1-917a94a7.doserverless.co/api/v1/web/fn-e0f31110-7521-4cb9-86a2-645f66eefb63/default/market-prices-simulator", Map.class);
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

    private void sendEmail(String to, String subject, String message) {
        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setTo(to);
        emailMessage.setSubject(subject);
        emailMessage.setText(message);
        mailSender.send(emailMessage);
    }
}
