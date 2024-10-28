package com.hackathon.bankingapp.Repositories;

import com.hackathon.bankingapp.Entities.Pin;
import com.hackathon.bankingapp.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PinRepository extends JpaRepository<Pin, Long> {
    Pin findByIdentifier(String identifier);
}
