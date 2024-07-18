package com.example.library_app_back_end.controller;

import com.example.library_app_back_end.constant.ErrorConstant;
import com.example.library_app_back_end.constant.ValidateConstant;
import com.example.library_app_back_end.dto.BookDTO;
import com.example.library_app_back_end.error.ValidationException;
import com.example.library_app_back_end.response.ApiResponse;
import com.example.library_app_back_end.response.DataPagingResponse;
import com.example.library_app_back_end.service.BookService;
import com.example.library_app_back_end.utils.ExtractJWT;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Log4j2
@RestController
@CrossOrigin(origins = "https://localhost:3000",allowCredentials = "true")
@RequestMapping("/api/books")
public class BookController {

    private static final String[] listSort = {"title"};
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @RequestMapping(value="", method = RequestMethod.GET)
    public ApiResponse<?> getListBook(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "page", required = false, defaultValue = ValidateConstant.PAGE) int page,
            @RequestParam(value = "pageSize", required = false, defaultValue = ValidateConstant.PER_PAGE) int pageSize
    ) {
        log.debug("REST request to get a page of book");
        DataPagingResponse<BookDTO> dataPagingResponse;
        String titleOpt = Optional.ofNullable(title).orElse("");
        if (titleOpt.length() > ValidateConstant.FIELD_LENGTH_LIMIT_NORMAL) {
            return new ApiResponse<>(400, null, ErrorConstant.OVER_LENGTH_FIELD
                    , String.format(ErrorConstant.OVER_LENGTH_FIELD_LABEL, "title", ValidateConstant.FIELD_LENGTH_LIMIT_NORMAL));
        }
        String categoryOpt = Optional.ofNullable(category).orElse("");
        if (categoryOpt.length() > ValidateConstant.FIELD_LENGTH_LIMIT_NORMAL) {
            return new ApiResponse<>(400, null, ErrorConstant.OVER_LENGTH_FIELD
                    , String.format(ErrorConstant.OVER_LENGTH_FIELD_LABEL, "category", ValidateConstant.FIELD_LENGTH_LIMIT_NORMAL));
        }
        try{
            if (page == -1 || pageSize == -1) {
                List<BookDTO> bookDTOList = bookService.getAll(titleOpt, categoryOpt);
                dataPagingResponse = new DataPagingResponse<>(bookDTOList, bookDTOList.size(), -1,-1);
            }else {
                Page<BookDTO> data = bookService.getAllPaging(titleOpt,categoryOpt, PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, listSort)));
                dataPagingResponse = new DataPagingResponse<>(data.getContent(), data.getTotalElements(), data.getNumber(), data.getSize());
            }
        }catch (Exception e){
            return new ApiResponse<>(400, null, ValidateConstant.ERROR, ValidateConstant.ERROR_LABEL);
        }
        return new ApiResponse<>(200, dataPagingResponse, null, null);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ApiResponse<?> getBook(@PathVariable Long id) {
        log.debug("REST request to get Book detail: {}", id);
        BookDTO dto = null;
        try {
            dto = bookService.getBook(id);
        } catch (ValidationException e) {
            return new ApiResponse<>(400, null, e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            return new ApiResponse<>(400, null, ValidateConstant.ERROR, ValidateConstant.ERROR_LABEL);
        }
        return new ApiResponse<>(200, dto, null, null);
    }
    @RequestMapping(value = "/secure/ischeckedout/byuser", method = RequestMethod.GET)
    public ApiResponse<?> checkoutBookByUser(@RequestHeader(value = "Authorization") String token,
                                             @RequestParam Long bookId) {
        log.debug("REST request to check if Book is checked out by user");
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        if (Objects.isNull(userEmail)) {
            return new ApiResponse<>(401, null, ErrorConstant.ERROR_TOKEN
                    , ErrorConstant.MISSING_USER_EMAIL);
        }
        Boolean isCheckedOut = null;
        try {
            isCheckedOut = bookService.checkOutBookByUser(userEmail, bookId);
        } catch (ValidationException e) {
            return new ApiResponse<>(400, null, e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            return new ApiResponse<>(400, null, ValidateConstant.ERROR, ValidateConstant.ERROR_LABEL);
        }
        return new ApiResponse<>(200, isCheckedOut, null, null);
    }
    @RequestMapping(value = "/secure/checkout", method = RequestMethod.PATCH)
    public ApiResponse<?> checkoutBook(@RequestHeader(value = "Authorization") String token,
                                       @RequestParam Long bookId) {
        log.debug("REST request to get checkout Book : {}", bookId);
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        if (Objects.isNull(userEmail)) {
            return new ApiResponse<>(401, null, ErrorConstant.ERROR_TOKEN
                    , ErrorConstant.MISSING_USER_EMAIL);
        }
        BookDTO dto = null;
        try {
            dto = bookService.checkoutBook(userEmail, bookId);
        } catch (ValidationException e) {
            return new ApiResponse<>(400, null, e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            return new ApiResponse<>(400, null, ValidateConstant.ERROR, ValidateConstant.ERROR_LABEL);
        }
        return new ApiResponse<>(200, dto, null, null);
    }

    @RequestMapping(value = "/secure/currentloans/count", method = RequestMethod.GET)
    public ApiResponse<?> currentLoansCount(@RequestHeader(value = "Authorization") String token) {
        log.debug("REST request to get number of books checked out by user");
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        if (Objects.isNull(userEmail)) {
            return new ApiResponse<>(401, null, ErrorConstant.ERROR_TOKEN
                    , ErrorConstant.MISSING_USER_EMAIL);
        }
        int count;
        try {
            count = bookService.currentCheckedOutCount(userEmail);
        } catch (ValidationException e) {
            return new ApiResponse<>(400, null, e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            return new ApiResponse<>(400, null, ValidateConstant.ERROR, ValidateConstant.ERROR_LABEL);
        }
        return new ApiResponse<>(200, count, null, null);
    }
    @RequestMapping(value = "/secure/currentloans", method = RequestMethod.GET)
    public ApiResponse<?> getCurrentLoans(@RequestHeader(value = "Authorization") String token) {
        log.debug("REST request to get books checked out by user");
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        if (Objects.isNull(userEmail)) {
            return new ApiResponse<>(401, null, ErrorConstant.ERROR_TOKEN
                    , ErrorConstant.MISSING_USER_EMAIL);
        }
        List<BookDTO> bookDTOList;
        try {
            bookDTOList = bookService.currentCheckedOutBooks(userEmail);
        } catch (ValidationException e) {
            return new ApiResponse<>(400, null, e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            return new ApiResponse<>(400, null, ValidateConstant.ERROR, ValidateConstant.ERROR_LABEL);
        }
        return new ApiResponse<>(200, bookDTOList, null, null);
    }

    @RequestMapping(value = "/secure/return", method = RequestMethod.PATCH)
    public ApiResponse<?> returnBook(@RequestHeader(value = "Authorization") String token,
                                       @RequestParam Long bookId) {
        log.debug("REST request to get return Book : {}", bookId);
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        if (Objects.isNull(userEmail)) {
            return new ApiResponse<>(401, null, ErrorConstant.ERROR_TOKEN
                    , ErrorConstant.MISSING_USER_EMAIL);
        }
        BookDTO dto = null;
        try {
            dto = bookService.returnBook(userEmail, bookId);
        } catch (ValidationException e) {
            return new ApiResponse<>(400, null, e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            return new ApiResponse<>(400, null, ValidateConstant.ERROR, ValidateConstant.ERROR_LABEL);
        }
        return new ApiResponse<>(200, dto, null, null);
    }
    @RequestMapping(value = "/secure/admin", method = RequestMethod.POST)
    public ApiResponse<?> createBook(@RequestHeader(value = "Authorization") String token,
                                        @RequestBody BookDTO bookRequest) {
        String adminEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        String checkRole = ExtractJWT.payloadJWTExtraction(token, "\"userType\"");
        if (Objects.isNull(adminEmail) || !checkRole.equals("admin")) {
            return new ApiResponse<>(401, null, ErrorConstant.ERROR_TOKEN
                    , ErrorConstant.MISSING_ADMIN_EMAIL);
        }
        log.debug("REST request to create book by user email: %", adminEmail);
        BookDTO result;
        try {
            result = bookService.addBook(bookRequest);
            if (Objects.isNull(result) || Objects.isNull(result.getId())) {
                return new ApiResponse<>(400, null, ValidateConstant.ERROR, ValidateConstant.ERROR_LABEL);
            }
        } catch (ValidationException e) {
            return new ApiResponse<>(400, null, e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            return new ApiResponse<>(400, null, ValidateConstant.ERROR, ValidateConstant.ERROR_LABEL);
        }

        return new ApiResponse<>(200, result, null, null);
    }
    @RequestMapping(value = "/secure/admin/increase/quantity", method = RequestMethod.PATCH)
    public ApiResponse<?> increaseBookQuantity(@RequestHeader(value = "Authorization") String token,
                                     @RequestParam Long bookId) {
        log.debug("REST request to increase quantity of book : {}", bookId);
        String adminEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        String checkRole = ExtractJWT.payloadJWTExtraction(token, "\"userType\"");
        if (Objects.isNull(adminEmail) || !checkRole.equals("admin")) {
            return new ApiResponse<>(401, null, ErrorConstant.ERROR_TOKEN
                    , ErrorConstant.MISSING_ADMIN_EMAIL);
        }
        BookDTO dto = null;
        try {
            dto = bookService.increaseBookQuantity(bookId);
        } catch (ValidationException e) {
            return new ApiResponse<>(400, null, e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            return new ApiResponse<>(400, null, ValidateConstant.ERROR, ValidateConstant.ERROR_LABEL);
        }
        return new ApiResponse<>(200, dto, null, null);
    }
    @RequestMapping(value = "/secure/admin/decrease/quantity", method = RequestMethod.PATCH)
    public ApiResponse<?> decreaseBookQuantity(@RequestHeader(value = "Authorization") String token,
                                               @RequestParam Long bookId) {
        log.debug("REST request to decrease quantity of book : {}", bookId);
        String adminEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        String checkRole = ExtractJWT.payloadJWTExtraction(token, "\"userType\"");
        if (Objects.isNull(adminEmail) || !checkRole.equals("admin")) {
            return new ApiResponse<>(401, null, ErrorConstant.ERROR_TOKEN
                    , ErrorConstant.MISSING_ADMIN_EMAIL);
        }
        BookDTO dto = null;
        try {
            dto = bookService.decreaseBookQuantity(bookId);
        } catch (ValidationException e) {
            return new ApiResponse<>(400, null, e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            return new ApiResponse<>(400, null, ValidateConstant.ERROR, ValidateConstant.ERROR_LABEL);
        }
        return new ApiResponse<>(200, dto, null, null);
    }

    @RequestMapping(value = "/secure/admin", method = RequestMethod.DELETE)
    public ApiResponse<?> deleteBook(@RequestHeader(value = "Authorization") String token,
                                               @RequestParam Long bookId) {
        log.debug("REST request to delete quantity of book : {}", bookId);
        String adminEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        String checkRole = ExtractJWT.payloadJWTExtraction(token, "\"userType\"");
        if (Objects.isNull(adminEmail) || !checkRole.equals("admin")) {
            return new ApiResponse<>(401, null, ErrorConstant.ERROR_TOKEN
                    , ErrorConstant.MISSING_ADMIN_EMAIL);
        }
        try {
             bookService.deleteBook(bookId);
        } catch (ValidationException e) {
            return new ApiResponse<>(400, null, e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            return new ApiResponse<>(400, null, ValidateConstant.ERROR, ValidateConstant.ERROR_LABEL);
        }
        return new ApiResponse<>(200, null, null, null);
    }
}
