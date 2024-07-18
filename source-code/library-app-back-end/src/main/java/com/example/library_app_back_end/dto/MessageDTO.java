package com.example.library_app_back_end.dto;

import lombok.Data;

@Data
public class MessageDTO {
    private long id;
    private String userEmail;
    private String title;
    private String question;
    private String adminEmail;
    private String response;
    private boolean isClosed;
}
