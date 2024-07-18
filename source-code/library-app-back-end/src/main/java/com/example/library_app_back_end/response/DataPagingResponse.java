package com.example.library_app_back_end.response;

import lombok.Data;

import java.util.List;

@Data
public class DataPagingResponse<T> {
    private List<T> items;

    private long total;

    private int page;

    private int pageSize;

    public DataPagingResponse(List<T> items, long total, int page, int pageSize) {
        super();
        this.items = items;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
    }

    public DataPagingResponse() {}
}
