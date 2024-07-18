package com.example.library_app_back_end.service;

import com.example.library_app_back_end.dto.MessageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageService {
    MessageDTO createMessage(String userEmail, MessageDTO messageRequest);
    Page<MessageDTO> getMessagesByUserEmail(String userEmail, Pageable pageable);

    Page<MessageDTO> getOpenMessages(Pageable pageable);
    MessageDTO answerMessage (MessageDTO messageRequest, String adminEmail);
}
