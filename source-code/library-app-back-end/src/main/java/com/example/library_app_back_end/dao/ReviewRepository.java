package com.example.library_app_back_end.dao;

import com.example.library_app_back_end.entity.Book;
import com.example.library_app_back_end.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {
    List<Review> findAllByBook(Book book);
    Page<Review> findAllByBook(Book book, Pageable pageable);
    Optional<Review> findFirstByUserEmailAndBook(String userEmail, Book book);
}
