package com.example.library_app_back_end.service;

import com.example.library_app_back_end.constant.ErrorConstant;
import com.example.library_app_back_end.dao.BookRepository;
import com.example.library_app_back_end.dao.ReviewRepository;
import com.example.library_app_back_end.dto.ReviewDTO;
import com.example.library_app_back_end.entity.Book;
import com.example.library_app_back_end.entity.Review;
import com.example.library_app_back_end.error.ValidationException;
import com.example.library_app_back_end.mapper.ReviewMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Log4j2
@Service
@Transactional
public class ReviewServiceImpl implements ReviewService{

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final ReviewMapper reviewMapper;

    public ReviewServiceImpl(ReviewRepository reviewRepository, BookRepository bookRepository, ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.reviewMapper = reviewMapper;
    }

    @Override
    public List<ReviewDTO> getAllByBookId(Long bookId) {
        log.debug("Request to get reviews by bookId %", bookId);
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isEmpty()){
            throw new ValidationException(ErrorConstant.NOT_FOUND_OBJECT, String.format(ErrorConstant.NOT_FOUND_OBJECT_LABEL,"Book"));
        }
        Book book = bookOpt.get();
        List<Review> reviewList = reviewRepository.findAllByBook(book);
        List<ReviewDTO> reviewDTOList = reviewMapper.toDto(reviewList);
        reviewDTOList.forEach(reviewDTO ->{
            reviewDTO.setBookId(bookId);
        });
        return reviewDTOList;
    }

    @Override
    public Page<ReviewDTO> getAllByBookId(Long bookId, Pageable pageable) {
        log.debug("Request to get reviews by bookId %", bookId);
        Book book = bookRepository.findById(bookId).orElseThrow(()
                -> new ValidationException(ErrorConstant.NOT_FOUND_OBJECT, String.format(ErrorConstant.NOT_FOUND_OBJECT_LABEL,"Book")));
        Page<Review> reviews = reviewRepository.findAllByBook(book, pageable);
        Page<ReviewDTO> result = reviews.map(reviewMapper::toDto);
        result.forEach(reviewDTO -> {
            reviewDTO.setBookId(bookId);
        });
        return result;
    }

    @Override
    public ReviewDTO add(ReviewDTO reviewDTO) {
        Book book = bookRepository.findById(reviewDTO.getBookId()).orElseThrow(()
                -> new ValidationException(ErrorConstant.NOT_FOUND_OBJECT, String.format(ErrorConstant.NOT_FOUND_OBJECT_LABEL,"Book")));
        Optional<Review> validateReview = reviewRepository.findFirstByUserEmailAndBook(reviewDTO.getUserEmail(), book);
        if (validateReview.isPresent()){
            throw new ValidationException(ErrorConstant.EXIST_OBJECT, String.format(ErrorConstant.EXISTED_OBJECT_LABEL, "Review"));
        }
        if (Objects.isNull(reviewDTO.getRating())){
            throw new ValidationException(ErrorConstant.BLANK_FIELD, String.format(ErrorConstant.BLANK_FIELD_LABEL, "Rating"));
        }
        if (Objects.isNull(reviewDTO.getBookId())){
            throw new ValidationException(ErrorConstant.BLANK_FIELD, String.format(ErrorConstant.BLANK_FIELD_LABEL, "Book Id"));
        }

        if (reviewDTO.getReviewDescription()!= null && reviewDTO.getReviewDescription().length() > 255) {
            throw new ValidationException(ErrorConstant.OVER_LENGTH_FIELD
                    , String.format(ErrorConstant.OVER_LENGTH_FIELD_LABEL, "Review description", 255));
        }

        reviewDTO.setDate(Date.valueOf(LocalDate.now()));
        Review review = reviewMapper.toEntity(reviewDTO);
        review.setBook(book);
        Review result = reviewRepository.save(review);
        ReviewDTO resultDto = reviewMapper.toDto(result);
        resultDto.setBookId(result.getBook().getId());
        return resultDto;
    }

    @Override
    public Boolean userReviewListed(String userEmail, Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(()
                -> new ValidationException(ErrorConstant.NOT_FOUND_OBJECT, String.format(ErrorConstant.NOT_FOUND_OBJECT_LABEL,"Book")));
        Optional<Review> validateReview = reviewRepository.findFirstByUserEmailAndBook(userEmail, book);
        if (validateReview.isPresent()){
            return true;
        }
        return false;
    }
}
