package com.example.library_app_back_end.service;

import com.example.library_app_back_end.dto.HistoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HistoryService {
    Page<HistoryDTO> getHistoriesByUserEmail(String userEmail, Pageable pageable);

}
