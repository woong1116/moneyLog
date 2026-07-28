package com.likelion.moneylog.domain.transaction.entity;

import com.likelion.moneylog.domain.category.entity.Category;
import com.likelion.moneylog.domain.category.entity.CategoryType;
import com.likelion.moneylog.domain.user.entity.User;
import com.likelion.moneylog.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "transactions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)   // 무분별한 기본 생성 방지
public class Transaction extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 소유자 (본인 데이터 필터의 기준)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 카테고리
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)      // ORDINAL 금지! 문자열로 저장
    @Column(nullable = false, length = 10)
    private TransactionType type;        // INCOME / EXPENSE

    @Column(nullable = false)
    private Long amount;              // 금액(원). 항상 양수

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private LocalDate transactionDate;  // 거래 발생일

    @Builder
    public Transaction(User user, Category category, TransactionType type,
                       Long amount, String description, LocalDate transactionDate) {
        this.user = user;
        this.category = category;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.transactionDate = transactionDate;
    }

    // 수정은 Setter가 아닌 의도가 드러나는 메서드로
    public void update(TransactionType type, Long amount, Category category,
                       String description, LocalDate transactionDate) {
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.transactionDate = transactionDate;
    }
}