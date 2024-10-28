package com.hackathon.bankingapp.Repositories;

import com.hackathon.bankingapp.Entities.AutoInvest;
import com.hackathon.bankingapp.Entities.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutoInvestRepository extends JpaRepository<AutoInvest, Long> {
}
