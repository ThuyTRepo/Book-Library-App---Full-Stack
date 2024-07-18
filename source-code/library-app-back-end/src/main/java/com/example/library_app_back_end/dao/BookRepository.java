package com.example.library_app_back_end.dao;

import com.example.library_app_back_end.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT m FROM Book m WHERE lower(m.title) LIKE CONCAT('%', lower(:title), '%') AND lower(m.category) LIKE CONCAT('%', lower(:category), '%') ")
    List<Book> getBooks(@Param("title") String title, @Param("category") String category);
    @Query("SELECT m FROM Book m WHERE lower(m.title) LIKE CONCAT('%', lower(:title), '%') AND lower(m.category) LIKE CONCAT('%', lower(:category), '%') ")
    Page<Book> getBooks(@Param("title") String title, @Param("category") String category, Pageable pageable);

//    Page<Book> findByTitleContaining(@Param("title") String title, Pageable pageable);
//    Page<Book> findByCategory(@Param("category") String category, Pageable pageable);
}
