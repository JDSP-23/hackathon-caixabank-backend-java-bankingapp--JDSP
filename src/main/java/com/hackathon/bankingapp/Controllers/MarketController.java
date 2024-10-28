package com.hackathon.bankingapp.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/market/")
public class MarketController {

    private final RestTemplate restTemplate;

    public MarketController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/prices")
    public ResponseEntity<?> getMarketPrices() {
        Map<String, Double> marketPrices = getDataFromApi();
        if (marketPrices.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No data found");
        }
        return ResponseEntity.ok(marketPrices);
    }

    @GetMapping("/prices/{symbol}")
    public ResponseEntity<?> getMarketPrices(@PathVariable() String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid symbol");
        }

        Map<String, Double> marketPrices;
        try {
            marketPrices = getDataFromApi();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching data from API");
        }

        if (!marketPrices.containsKey(symbol)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Symbol not found");
        }

        return ResponseEntity.ok(marketPrices.get(symbol));
    }

    private Map<String, Double> getDataFromApi() {
        return restTemplate.getForObject("https://faas-lon1-917a94a7.doserverless.co/api/v1/web/fn-e0f31110-7521-4cb9-86a2-645f66eefb63/default/market-prices-simulator", Map.class);
    }
}
