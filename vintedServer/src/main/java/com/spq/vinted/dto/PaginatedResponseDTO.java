package com.spq.vinted.dto;

import java.util.List;

public class PaginatedResponseDTO<T> {
    private List<T> content;
    private int page;
    private int totalPages;

    public PaginatedResponseDTO(List<T> content, int page, int totalPages) {
        this.content = content;
        this.page = page;
        this.totalPages = totalPages;
    }
    public List<T> getContent() {
        return content;
    }
    public void setContent(List<T> content) {
        this.content = content;
    }
    public int getPage() {
        return page;
    }
    public void setPage(int page) {
        this.page = page;
    }
    
    public int getTotalPages() {
        return totalPages;
    }
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
