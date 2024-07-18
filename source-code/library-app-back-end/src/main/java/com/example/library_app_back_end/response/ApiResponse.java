package com.example.library_app_back_end.response;

import lombok.Data;

@Data
public class ApiResponse<T>  {
    private int status;

    private T data;

    private String error_code;

    private String error_message;

    public ApiResponse(int status, T data, String error_code, String error_message) {
        super();
        this.status = status;
        this.data = data;
        this.error_code = error_code;
        this.error_message = error_message;
    }

    public ApiResponse(int status, String error_code, String error_message) {
        super();
        this.status = status;
        this.error_code = error_code;
        this.error_message = error_message;
    }
}
