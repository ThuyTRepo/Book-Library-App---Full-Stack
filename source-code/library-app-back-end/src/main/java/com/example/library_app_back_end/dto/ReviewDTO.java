package com.example.library_app_back_end.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ReviewDTO {
    private Long id;

    private String userEmail;

    private Date date;

    private double rating;

    private Long bookId;

    private String reviewDescription;

}
