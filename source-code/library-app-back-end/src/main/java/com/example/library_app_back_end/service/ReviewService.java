package com.example.library_app_back_end.service;
import com.example.library_app_back_end.dto.ReviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {
    List<ReviewDTO> getAllByBookId(Long bookId);
    Page<ReviewDTO> getAllByBookId(Long bookId, Pageable pageable);

    ReviewDTO add(ReviewDTO reviewDTO);

    Boolean userReviewListed(String userEmail, Long bookId);

}
