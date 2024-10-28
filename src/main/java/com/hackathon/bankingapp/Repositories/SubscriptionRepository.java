package com.hackathon.bankingapp.Repositories;

import com.hackathon.bankingapp.Entities.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByNextPaymentTimeBefore(LocalDateTime now);
}
