package com.example.library_app_back_end.service;

import com.example.library_app_back_end.dao.HistoryRepository;
import com.example.library_app_back_end.dto.BookDTO;
import com.example.library_app_back_end.dto.HistoryDTO;
import com.example.library_app_back_end.entity.History;
import com.example.library_app_back_end.mapper.BookMapper;
import com.example.library_app_back_end.mapper.HistoryMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
@Transactional
public class HistoryServiceImpl implements HistoryService{
    private final HistoryRepository historyRepository;
    private final BookMapper bookMapper;
    private final HistoryMapper historyMapper;
    public HistoryServiceImpl(HistoryRepository historyRepository, BookMapper bookMapper, HistoryMapper historyMapper) {
        this.historyRepository = historyRepository;
        this.bookMapper = bookMapper;
        this.historyMapper = historyMapper;
    }

    @Override
    public Page<HistoryDTO> getHistoriesByUserEmail(String userEmail, Pageable pageable) {
        log.debug("Request to get a page of histories by User email: %",userEmail);
        Page<History> histories = historyRepository.findAllByUserEmail(userEmail, pageable);
        List<History> historyList = histories.getContent();
        Map<Long, BookDTO> bookMap = new HashMap<>();
        for (History h : historyList){
            BookDTO bookDTO = bookMapper.toDto(h.getBook());
            bookMap.putIfAbsent(bookDTO.getId(),bookDTO);
        }
        Page<HistoryDTO> historyDTOPage = histories.map(historyMapper::toDto);
        int i = 0;
        for (History h : historyList){
            historyDTOPage.getContent().get(i).setBookDTO(bookMap.get(h.getBook().getId()));
            i++;
        }
        return historyDTOPage;
    }
}
