package com.hackathon.bankingapp.Repositories;

import com.hackathon.bankingapp.Entities.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    Otp findByOtp(String otp);

    Otp findByPasswordResetToken(String resetToken);
}
