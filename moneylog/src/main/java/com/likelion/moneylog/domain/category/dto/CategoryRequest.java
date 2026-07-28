package com.likelion.moneylog.domain.category.dto;

import com.likelion.moneylog.domain.category.entity.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// 카테고리 등록/수정 요청 DTO
public record CategoryRequest(
        @NotBlank(message = "카테고리명은 필수입니다.")
        @Size(max = 50, message = "카테고리명은 50자 이하여야 합니다.")
        String name,

        @NotNull(message = "카테고리 타입은 필수입니다.")
        CategoryType type          // INCOME / EXPENSE
) {}
