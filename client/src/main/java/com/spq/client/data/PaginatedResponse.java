package com.spq.client.data;

import java.util.List;

public record PaginatedResponse<T> (
    List<T> content,
    int page,
    int totalPages
){}

