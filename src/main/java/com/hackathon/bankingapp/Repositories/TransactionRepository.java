package com.hackathon.bankingapp.Repositories;

import com.hackathon.bankingapp.Entities.Transaction;
import com.hackathon.bankingapp.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllBySourceAccountNumber(String accountNumber);
}
