package com.example.library_app_back_end.dao;

import com.example.library_app_back_end.entity.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<History,Long> {

    Page<History> findAllByUserEmail(@Param("userEmail") String userEmail, Pageable pageable);
}
