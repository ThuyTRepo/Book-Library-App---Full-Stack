package com.example.library_app_back_end.dao;

import com.example.library_app_back_end.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message,Long> {
    Page<Message> findAllByUserEmail(String userEmail, Pageable pageable);

//    For admin to find all un-answered messages
    Page<Message> findAllByIsClosed(boolean isClosed, Pageable pageable);
}
