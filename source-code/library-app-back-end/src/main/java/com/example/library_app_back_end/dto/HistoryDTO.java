package com.example.library_app_back_end.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HistoryDTO {
    private Long id;
    private String userEmail;
    private String checkoutDate;
    private String returnedDate;
    @JsonProperty("book")
    private BookDTO bookDTO;
}
