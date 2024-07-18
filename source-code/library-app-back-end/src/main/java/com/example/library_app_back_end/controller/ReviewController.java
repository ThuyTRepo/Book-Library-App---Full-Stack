package com.example.library_app_back_end.controller;

import com.example.library_app_back_end.constant.ErrorConstant;
import com.example.library_app_back_end.constant.ValidateConstant;
import com.example.library_app_back_end.dto.ReviewDTO;
import com.example.library_app_back_end.error.ValidationException;
import com.example.library_app_back_end.response.ApiResponse;
import com.example.library_app_back_end.response.DataPagingResponse;
import com.example.library_app_back_end.service.ReviewService;
import com.example.library_app_back_end.utils.ExtractJWT;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Objects;

@Log4j2
@RestController
@CrossOrigin(origins = "https://localhost:3000",allowCredentials = "true")
@RequestMapping("/api/reviews")
public class ReviewController {
    private static final String[] listSort = {"date"};
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }
    @RequestMapping(value="", method = RequestMethod.GET)
    public ApiResponse<?> getListReviewsByBookId(
            @RequestParam(value = "bookId", required = true) Long bookId,
            @RequestParam(value = "page", required = false, defaultValue = ValidateConstant.PAGE) int page,
            @RequestParam(value = "pageSize", required = false, defaultValue = ValidateConstant.PER_PAGE) int pageSize
    ) {
        log.debug("REST request to get a page of reviews by bookId %", bookId);
        DataPagingResponse<ReviewDTO> dataPagingResponse;

        try{
            if (page == -1 || pageSize == -1) {
                List<ReviewDTO> reviewListDTO = reviewService.getAllByBookId(bookId);
                dataPagingResponse = new DataPagingResponse<>(reviewListDTO, reviewListDTO.size(), -1,-1);
            }else {
                Page<ReviewDTO> data = reviewService.getAllByBookId(bookId, PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, listSort)));
                dataPagingResponse = new DataPagingResponse<>(data.getContent(), data.getTotalElements(), data.getNumber(), data.getSize());
            }
        }catch (Exception e){
            return new ApiResponse<>(400, null, ValidateConstant.ERROR, ValidateConstant.ERROR_LABEL);

        }

        return new ApiResponse<>(200, dataPagingResponse, null, null);
    }
    @RequestMapping(value = "/secure", method = RequestMethod.POST)
    public ApiResponse<?> createReview(@RequestHeader(value = "Authorization") String token,
                                       @RequestBody ReviewDTO reviewDTO){
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        if (Objects.isNull(userEmail)){
            return new ApiResponse<>(400, null, ErrorConstant.ERROR_TOKEN
                    , ErrorConstant.MISSING_USER_EMAIL);
        }
        ReviewDTO result;

        log.debug("REST request to create review by %", userEmail);

        try{
            reviewDTO.setUserEmail(userEmail);
            result=reviewService.add(reviewDTO);
            if (Objects.isNull(result)||Objects.isNull(result.getId())){
                return new ApiResponse<>(400, null, ValidateConstant.ERROR, ValidateConstant.ERROR_LABEL);
            }
        } catch (ValidationException e) {
            return new ApiResponse<>(400, null, e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            return new ApiResponse<>(400, null, ValidateConstant.ERROR, ValidateConstant.ERROR_LABEL);
        }
        return new ApiResponse<>(200, result, null, null);
    }


    @RequestMapping(value = "/secure/user/book", method = RequestMethod.GET)
    public ApiResponse<?> reviewBookByUser(@RequestHeader(value = "Authorization") String token,
                                             @RequestParam Long bookId) {
        log.debug("REST request to check if Book is reviewed by user");
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        if (Objects.isNull(userEmail)){
            return new ApiResponse<>(400, null, ErrorConstant.ERROR_TOKEN
                    , ErrorConstant.MISSING_USER_EMAIL);
        }
        Boolean isReviewed = null;
        try {
            isReviewed = reviewService.userReviewListed(userEmail, bookId);
        } catch (ValidationException e) {
            return new ApiResponse<>(400, null, e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            return new ApiResponse<>(400, null, ValidateConstant.ERROR, ValidateConstant.ERROR_LABEL);
        }
        return new ApiResponse<>(200, isReviewed, null, null);
    }
}
