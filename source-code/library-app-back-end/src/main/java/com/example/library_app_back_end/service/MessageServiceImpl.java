package com.example.library_app_back_end.service;

import com.example.library_app_back_end.constant.ErrorConstant;
import com.example.library_app_back_end.dao.MessageRepository;
import com.example.library_app_back_end.dto.MessageDTO;
import com.example.library_app_back_end.entity.Message;
import com.example.library_app_back_end.error.ValidationException;
import com.example.library_app_back_end.mapper.MessageMapper;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Log4j2
@Service
@Transactional
public class MessageServiceImpl implements MessageService{
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    public MessageServiceImpl(MessageRepository messageRepository, MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
    }


    @Override
    public MessageDTO createMessage(String userEmail, MessageDTO messageRequest) {
        if (Objects.isNull(messageRequest.getTitle())){
            throw new ValidationException(ErrorConstant.BLANK_FIELD, String.format(ErrorConstant.BLANK_FIELD_LABEL, "Title"));
        }
        if (Objects.isNull(messageRequest.getQuestion())){
            throw new ValidationException(ErrorConstant.BLANK_FIELD, String.format(ErrorConstant.BLANK_FIELD_LABEL, "Question"));
        }
        Message message = Message.builder()
                .userEmail(userEmail)
                .title(messageRequest.getTitle())
                .question(messageRequest.getQuestion())
                .build();

        Message result = messageRepository.save(message);
        return messageMapper.toDto(result);
    }

    @Override
    public Page<MessageDTO> getMessagesByUserEmail(String userEmail, Pageable pageable) {
        log.debug("Request to get a page of histories by User email: %",userEmail);
        Page<Message> messages = messageRepository.findAllByUserEmail(userEmail,pageable);
        Page<MessageDTO> result = messages.map(messageMapper::toDto);
        return result;
    }

    @Override
    public Page<MessageDTO> getOpenMessages(Pageable pageable) {
        log.debug("Admin - request to get open messages");
        Page<Message> openMessages = messageRepository.findAllByIsClosed(false, pageable);
        Page<MessageDTO> result = openMessages.map(messageMapper::toDto);
        return result;
    }

    @Override
    public MessageDTO answerMessage(MessageDTO messageRequest, String adminEmail) throws ValidationException {
        log.debug("Admin : % - answer message", adminEmail);
        Message message = messageRepository.findById(messageRequest.getId()).orElseThrow(
                () -> new ValidationException(ErrorConstant.NOT_FOUND_OBJECT, String.format(ErrorConstant.NOT_FOUND_OBJECT_LABEL, "Message")));
        if (Objects.isNull(messageRequest.getResponse())){
            throw new ValidationException(ErrorConstant.BLANK_FIELD, String.format(ErrorConstant.BLANK_FIELD_LABEL, "response"));
        }
        if (message.isClosed()){
            throw new ValidationException(ErrorConstant.ANSWERED_MESSAGE,ErrorConstant.ANSWERED_MESSAGE_LABEL);
        }
        message.setResponse(messageRequest.getResponse());
        message.setClosed(true);
        message.setAdminEmail(adminEmail);
        Message result = messageRepository.save(message);

        return messageMapper.toDto(result);
    }

}
