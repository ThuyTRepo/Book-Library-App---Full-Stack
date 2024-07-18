package com.example.library_app_back_end.controller;

import com.example.library_app_back_end.constant.ErrorConstant;
import com.example.library_app_back_end.constant.ValidateConstant;
import com.example.library_app_back_end.dto.HistoryDTO;
import com.example.library_app_back_end.response.ApiResponse;
import com.example.library_app_back_end.response.DataPagingResponse;
import com.example.library_app_back_end.service.HistoryService;
import com.example.library_app_back_end.utils.ExtractJWT;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Log4j2
@RestController
@CrossOrigin(origins = "https://localhost:3000",allowCredentials = "true")
@RequestMapping("/api/histories")
public class HistoryController {
    private static final String[] listSort = {"returnedDate"};
    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @RequestMapping(value = "/secure", method = RequestMethod.GET)
    public ApiResponse<?> getAllDevices(
            @RequestHeader(value = "Authorization") String token,
            @RequestParam(value = "page", required = false, defaultValue = ValidateConstant.PAGE) int page,
            @RequestParam(value = "pageSize", required = false, defaultValue = ValidateConstant.PER_PAGE) int pageSize
    ) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        if (Objects.isNull(userEmail)){
            return new ApiResponse<>(400, null, ErrorConstant.ERROR_TOKEN
                    , ErrorConstant.MISSING_USER_EMAIL);
        }
        DataPagingResponse<HistoryDTO> dataPagingResponse;
        log.debug("REST request to get a page of history");
        try {
            Page<HistoryDTO> data = historyService.getHistoriesByUserEmail(userEmail, PageRequest.of(page,
                    pageSize, Sort.by(Sort.Direction.DESC, listSort)));
            dataPagingResponse = new DataPagingResponse<>(data.getContent(),
                    data.getTotalElements(), data.getNumber(), data.getSize());
        } catch (Exception e) {
            return new ApiResponse<>(400, null, ValidateConstant.ERROR, ValidateConstant.ERROR_LABEL);
        }
        return new ApiResponse<>(200, dataPagingResponse, null, null);
    }
}

