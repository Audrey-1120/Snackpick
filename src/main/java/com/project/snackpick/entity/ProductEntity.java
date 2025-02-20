package com.project.snackpick.entity;

import com.project.snackpick.dto.ProductDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "PRODUCT_T")
public class ProductEntity { // 제품

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId;

    // 제품 이름
    @Column(name = "product_name", unique = true, nullable = false, length = 25)
    private String productName;

    // 대분류
    @Column(name = "product_cat1_nm", length = 50)
    private String productCat1Name;

    // 중분류
    @Column(name = "product_cat2_nm", length = 50)
    private String productCat2Name;

    // 맛 평균 별점
    @Column(name = "rating_taste_average")
    private double ratingTasteAverage;

    // 가격 평균 별점
    @Column(name = "rating_price_average")
    private double ratingPriceAverage;

    // 리뷰와 1:N 관계
    @OneToMany(mappedBy = "productEntity")
    private List<ReviewEntity> reviewList = new ArrayList<>();

    public ProductDTO toProductDTO() {
        return ProductDTO.builder()
                .productName(productName)
                .productCat1Nm(productCat1Name)
                .productCat2Nm(productCat2Name)
                .ratingTasteAverage(ratingTasteAverage)
                .ratingPriceAverage(ratingPriceAverage)
                .build();
    }
}
