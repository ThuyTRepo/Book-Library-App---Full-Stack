package com.example.library_app_back_end.dao;

import com.example.library_app_back_end.entity.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CheckoutRepository extends JpaRepository<Checkout, Long> {

    Optional<Checkout> findFirstByUserEmailAndBookId(String userEmail, Long bookId);
    List<Checkout> findAllByUserEmail(String userEmail);
}
