package com.project.snackpick.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "PRODUCT_T")
public class ProductEntity { // 제품

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // IDENTITY 전략 - 기본키 생성을 DB에 위임한다.
    private int productId;

    // 제품 이름
    @Column(name = "product_name", unique = true, nullable = false, length = 25)
    private String productName;

    // 제품 평점 총합
    @Column(name = "total_rating_taste")
    private double totalRatingTaste;

    // 맛 평점 총합
    @Column(name = "total_rating_price")
    private double totalRatingPrice;

    // 리뷰 개수
    @Column(name = "review_count", nullable = false)
    private long reviewCount;

    // 대분류 (음료, 떡류)
    @ManyToOne
    @JoinColumn(name = "top_category_id", nullable = false)
    private CategoryEntity topCategory;

    // 중분류 (탄산음료, 과채음료)
    @ManyToOne
    @JoinColumn(name = "sub_category_id", nullable = false)
    private CategoryEntity subCategory;

    // 리뷰와 1:N 관계
    @OneToMany(mappedBy = "productEntity")
    @BatchSize(size = 10)
    private List<ReviewEntity> reviewList = new ArrayList<>();

}
