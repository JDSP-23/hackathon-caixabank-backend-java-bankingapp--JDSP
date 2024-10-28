package com.hackathon.bankingapp.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "auto_invest")
public class AutoInvest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String identifier;

    public AutoInvest() {
    }

    public AutoInvest(String identifier) {
        this.identifier = identifier;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
