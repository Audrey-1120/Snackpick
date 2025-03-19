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
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId;

    @Column(name = "product_name", unique = true, nullable = false, length = 25)
    private String productName;

    @Column(name = "total_rating_taste")
    private double totalRatingTaste;

    @Column(name = "total_rating_price")
    private double totalRatingPrice;

    @Column(name = "review_count", nullable = false)
    private long reviewCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "top_category_id", nullable = false)
    private CategoryEntity topCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id", nullable = false)
    private CategoryEntity subCategory;

    @OneToMany(mappedBy = "productEntity")
    @BatchSize(size = 10)
    private List<ReviewEntity> reviewList = new ArrayList<>();

}
