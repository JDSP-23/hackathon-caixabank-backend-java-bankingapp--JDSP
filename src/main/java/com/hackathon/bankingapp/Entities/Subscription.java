package com.hackathon.bankingapp.Entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private int intervalSeconds;

    @Column(nullable = false)
    private String accountNumber;

    private LocalDateTime nextPaymentTime;

    public Subscription() {
    }

    public Subscription(double amount, int intervalSeconds, String accountNumber) {
        this.amount = amount;
        this.intervalSeconds = intervalSeconds;
        this.accountNumber = accountNumber;
        this.nextPaymentTime = LocalDateTime.now().plusSeconds(intervalSeconds);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getIntervalSeconds() {
        return intervalSeconds;
    }

    public void setIntervalSeconds(int intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public LocalDateTime getNextPaymentTime() {
        return nextPaymentTime;
    }

    public void setNextPaymentTime(LocalDateTime nextPaymentTime) {
        this.nextPaymentTime = nextPaymentTime;
    }
}
