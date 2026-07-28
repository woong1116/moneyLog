package com.likelion.moneylog.global.response;

import org.springframework.data.domain.Page;

// meta.pagination 형식: { "pagination": { page, size, totalItems, totalPages, hasNext, hasPrev } }
public record PageMeta(Pagination pagination) {

    public record Pagination(
            int page,
            int size,
            long totalItems,
            int totalPages,
            boolean hasNext,
            boolean hasPrev
    ) {}

    // Page<T> → PageMeta 변환 정적 팩토리
    public static PageMeta from(Page<?> page) {
        return new PageMeta(new Pagination(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious()
        ));
    }
}