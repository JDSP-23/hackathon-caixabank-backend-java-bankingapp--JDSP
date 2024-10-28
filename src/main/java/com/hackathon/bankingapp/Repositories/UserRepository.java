package com.hackathon.bankingapp.Repositories;

import com.hackathon.bankingapp.Entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(@NotBlank() @Email() String email);

    boolean existsByPhoneNumber(@NotBlank() String phoneNumber);

    User findByEmail(String email);
}