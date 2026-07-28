package com.likelion.moneylog.domain.transaction.controller;

import com.likelion.moneylog.global.response.ApiResponse;
import com.likelion.moneylog.global.response.PageMeta;
import com.likelion.moneylog.domain.transaction.dto.TransactionRequest;
import com.likelion.moneylog.domain.transaction.dto.TransactionResponse;
import com.likelion.moneylog.domain.transaction.entity.TransactionType;
import com.likelion.moneylog.domain.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponse>> create(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody TransactionRequest req) {
        TransactionResponse created = transactionService.create(userId, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("거래내역 등록에 성공했습니다.", created));
    }

    @GetMapping
    public ApiResponse<Map<String, List<TransactionResponse>>> list(
            @AuthenticationPrincipal Long userId,        // ← 현재 로그인 사용자 id
            @RequestParam(required = false) String yearMonth,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 5) Pageable pageable) {
        Page<TransactionResponse> data =
                transactionService.getMyTransactions(userId, yearMonth, type, categoryId, pageable);
        return ApiResponse.success(
                "거래내역 목록 조회에 성공했습니다.",
                Map.of("transactions", data.getContent()),
                PageMeta.from(data));
    }

    // 단건 조회: 남의 것이면 403(FORBIDDEN)
    @GetMapping("/{id}")
    public ApiResponse<TransactionResponse> get(@AuthenticationPrincipal Long userId,
                                                @PathVariable Long id) {
        TransactionResponse data = transactionService.getMyTransaction(userId, id);
        return ApiResponse.success("거래내역 조회에 성공했습니다.", data);
    }

    @PutMapping("/{id}")
    public ApiResponse<TransactionResponse> update(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id, @Valid @RequestBody TransactionRequest req) {
        return ApiResponse.success("거래내역 수정에 성공했습니다.",
                transactionService.update(userId, id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        transactionService.delete(userId, id);
        return ApiResponse.success("거래내역 삭제에 성공했습니다.", null);
    }
}