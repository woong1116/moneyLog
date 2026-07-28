package com.likelion.moneylog.domain.category.dto;

import com.likelion.moneylog.domain.category.entity.Category;
import com.likelion.moneylog.domain.category.entity.CategoryType;

// 카테고리 응답 DTO
public record CategoryResponse(
        Long id,
        String name,
        CategoryType type
) {
    // 엔티티 → 응답 DTO 변환 정적 팩토리
    public static CategoryResponse from(Category c) {
        return new CategoryResponse(c.getId(), c.getName(), c.getType());
    }
}
