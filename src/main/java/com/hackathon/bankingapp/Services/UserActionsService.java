package com.hackathon.bankingapp.Services;

import com.hackathon.bankingapp.Entities.*;
import com.hackathon.bankingapp.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class UserActionsService {
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AutoInvestRepository autoInvestRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Scheduled(fixedRate = 1000) // Poll every second
    public void processSubscriptions() {
        LocalDateTime now = LocalDateTime.now();
        List<Subscription> subscriptions = subscriptionRepository.findByNextPaymentTimeBefore(now);

        for (Subscription subscription : subscriptions) {
            Account account = accountRepository.findByAccountNumber(subscription.getAccountNumber());

            if (account != null && account.getBalance() >= subscription.getAmount()) {
                // Deduct subscription amount from account
                account.setBalance(account.getBalance() - subscription.getAmount());

                // Log the transaction
                addTransaction(account.getAccountNumber(), "N/A", subscription.getAmount(), "SUBSCRIPTION");

                // Update the next payment time for the subscription
                subscription.setNextPaymentTime(now.plusSeconds(subscription.getIntervalSeconds()));
                accountRepository.save(account);
                subscriptionRepository.save(subscription);
            } else {
                // If balance is insufficient, remove the subscription
                subscriptionRepository.delete(subscription);
            }
        }
    }

    @Scheduled(fixedRate = 30000) // Check market every 30 seconds
    public void processAutoInvest() {
        List<AutoInvest> autoInvests = autoInvestRepository.findAll();

        for (AutoInvest autoInvest : autoInvests) {
            User user = userRepository.findByEmail(autoInvest.getIdentifier());
            Account account = accountRepository.findByAccountNumber(user.getAccountNumber());

            if (account != null) {
                Map<String, Double> marketPrices = getDataFromApi();

                for (Asset asset : assetRepository.findAllByIdentifier(account.getIdentifier())) {
                    Double currentPrice = marketPrices.get(asset.getAssetSymbol());
                    if (currentPrice != null) {
                        double initialPrice = asset.getCost();
                        if (currentPrice >= initialPrice * 1.2) {
                            sellAsset(account, asset, currentPrice);
                        } else if (currentPrice <= initialPrice * 0.8) {
                            buyAsset(account, asset, currentPrice);
                        }
                    }
                }
            }
        }
    }

    private void buyAsset(Account account, Asset asset, double currentPrice) {
        double buyAmount = 0.2; // Define the amount to buy
        double cost = buyAmount * currentPrice;

        if (account.getBalance() >= cost) {
            asset.setAmount(asset.getAmount() + buyAmount);
            account.setBalance(account.getBalance() - cost);
            accountRepository.save(account);
            assetRepository.save(asset);
            addTransaction(account.getAccountNumber(), "N/A", cost, "ASSET_PURCHASE");
        }
    }

    private void sellAsset(Account account, Asset asset, double currentPrice) {
        double sellAmount = 0.1; // Define the amount to sell
        if (asset.getAmount() >= sellAmount) {
            double revenue = sellAmount * currentPrice;
            asset.setAmount(asset.getAmount() - sellAmount);
            account.setBalance(account.getBalance() + revenue);
            accountRepository.save(account);
            assetRepository.save(asset);
            addTransaction(account.getAccountNumber(), "N/A", revenue, "ASSET_SELL");
        }
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
}
