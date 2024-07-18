package com.example.library_app_back_end.controller;

import com.example.library_app_back_end.constant.ErrorConstant;
import com.example.library_app_back_end.constant.ValidateConstant;
import com.example.library_app_back_end.dto.MessageDTO;
import com.example.library_app_back_end.error.ValidationException;
import com.example.library_app_back_end.response.ApiResponse;
import com.example.library_app_back_end.response.DataPagingResponse;
import com.example.library_app_back_end.service.MessageService;
import com.example.library_app_back_end.utils.ExtractJWT;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

@Log4j2
@RestController
@CrossOrigin(origins = "https://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/messages")
public class MessageController {
    private static final String[] listSort = {"createdOn"};
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @RequestMapping(value = "/secure", method = RequestMethod.POST)
    public ApiResponse<?> createMessage(@RequestHeader(value = "Authorization") String token,
                                        @RequestBody MessageDTO messageRequest) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        if (Objects.isNull(userEmail)) {
            return new ApiResponse<>(400, null, ErrorConstant.ERROR_TOKEN
                    , ErrorConstant.MISSING_USER_EMAIL);
        }
        log.debug("REST request to create message by user email: %", userEmail);
        MessageDTO result;
        try {
            result = messageService.createMessage(userEmail, messageRequest);
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

    @RequestMapping(value = "/secure", method = RequestMethod.GET)
    public ApiResponse<?> getMessages(
            @RequestHeader(value = "Authorization") String token,
            @RequestParam(value = "page", required = false, defaultValue = ValidateConstant.PAGE) int page,
            @RequestParam(value = "pageSize", required = false, defaultValue = ValidateConstant.PER_PAGE) int pageSize
    ) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        if (Objects.isNull(userEmail)) {
            return new ApiResponse<>(400, null, ErrorConstant.ERROR_TOKEN
                    , ErrorConstant.MISSING_USER_EMAIL);
        }
        log.debug("REST request to get messages by user email: %", userEmail);
        DataPagingResponse<MessageDTO> dataPagingResponse;
        try {
            Page<MessageDTO> data = messageService.getMessagesByUserEmail(userEmail, PageRequest.of(page,
                    pageSize, Sort.by(Sort.Direction.DESC, listSort)));
            dataPagingResponse = new DataPagingResponse<>(data.getContent(),
                    data.getTotalElements(), data.getNumber(), data.getSize());
        } catch (Exception e) {
            return new ApiResponse<>(400, null, ValidateConstant.ERROR, ValidateConstant.ERROR_LABEL);
        }
        return new ApiResponse<>(200, dataPagingResponse, null, null);
    }

    @RequestMapping(value = "/secure/admin/open/messages", method = RequestMethod.GET)
    public ApiResponse<?> getOpenMessages(
            @RequestHeader(value = "Authorization") String token,
            @RequestParam(value = "page", required = false, defaultValue = ValidateConstant.PAGE) int page,
            @RequestParam(value = "pageSize", required = false, defaultValue = ValidateConstant.PER_PAGE) int pageSize
    ) {
        String adminEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        String checkRole = ExtractJWT.payloadJWTExtraction(token, "\"userType\"");
        if (Objects.isNull(adminEmail) || !checkRole.equals("admin")) {
            return new ApiResponse<>(401, null, ErrorConstant.ERROR_TOKEN
                    , ErrorConstant.MISSING_ADMIN_EMAIL);
        }
        log.debug("Admin- REST request to get open messages");
        DataPagingResponse<MessageDTO> dataPagingResponse;
        try {
            Page<MessageDTO> data = messageService.getOpenMessages(PageRequest.of(page,
                    pageSize, Sort.by(Sort.Direction.DESC, listSort)));
            dataPagingResponse = new DataPagingResponse<>(data.getContent(),
                    data.getTotalElements(), data.getNumber(), data.getSize());
        } catch (Exception e) {
            return new ApiResponse<>(400, null, ValidateConstant.ERROR, ValidateConstant.ERROR_LABEL);
        }
        return new ApiResponse<>(200, dataPagingResponse, null, null);
    }


    @RequestMapping(value = "/secure/admin/message", method = RequestMethod.PATCH)
    public ApiResponse<?> answerMessage(@RequestHeader(value = "Authorization") String token,
                                        @RequestBody MessageDTO messageRequest) {
        String adminEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        String checkRole = ExtractJWT.payloadJWTExtraction(token, "\"userType\"");
        if (Objects.isNull(adminEmail) || !checkRole.equals("admin")) {
            return new ApiResponse<>(400, null, ErrorConstant.ERROR_TOKEN
                    , ErrorConstant.MISSING_ADMIN_EMAIL);
        }
        log.debug("REST request to answer message by admin email: %", adminEmail);
        MessageDTO result;
        try {
            result = messageService.answerMessage(messageRequest, adminEmail);
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
}



