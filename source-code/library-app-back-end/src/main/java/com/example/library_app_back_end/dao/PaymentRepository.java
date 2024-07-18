package com.example.library_app_back_end.dao;

import com.example.library_app_back_end.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    Optional<Payment> findFirstByUserEmail(String userEmail);
}
