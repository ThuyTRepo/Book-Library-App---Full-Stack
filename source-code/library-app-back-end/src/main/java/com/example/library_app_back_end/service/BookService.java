package com.example.library_app_back_end.service;

import com.example.library_app_back_end.dto.BookDTO;
import com.example.library_app_back_end.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {
    List<BookDTO> getAll(String title, String category);
    Page<BookDTO> getAllPaging(String title, String category,Pageable pageable);
    BookDTO getBook(Long id);

    Boolean checkOutBookByUser(String userEmail, Long bookId);

    BookDTO checkoutBook(String userEmail, Long bookId);

//    count number of book checked out by user
    int currentCheckedOutCount(String userEmail);

//    get books checked out by user
    List<BookDTO> currentCheckedOutBooks(String userEmail);

    BookDTO returnBook (String userEmail, Long bookId);

    BookDTO addBook (BookDTO bookDTO);

    BookDTO increaseBookQuantity(Long bookId);

    BookDTO decreaseBookQuantity(Long bookId);

    void deleteBook(Long bookId);

}
