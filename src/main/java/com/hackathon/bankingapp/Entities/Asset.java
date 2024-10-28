package com.hackathon.bankingapp.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "assets")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "assetSymbol", nullable = false)
    private String assetSymbol;
    @Column(name = "amount", nullable = false)
    private double amount;
    @Column(name = "cost", nullable = false)
    private double cost;
    @Column(name = "identifier", nullable = false)
    private String identifier;


    public Asset() {
    }

    public Asset(Long id, String assetSymbol, double amount, double cost, String identifier) {
        this.id = id;
        this.assetSymbol = assetSymbol;
        this.amount = amount;
        this.cost = cost;
        this.identifier = identifier;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAssetSymbol() {
        return assetSymbol;
    }

    public void setAssetSymbol(String assetSymbol) {
        this.assetSymbol = assetSymbol;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
