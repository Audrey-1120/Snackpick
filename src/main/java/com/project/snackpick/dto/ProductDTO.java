package com.project.snackpick.dto;

import com.project.snackpick.entity.ProductEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private int productId;
    private long reviewCount;
    private String productName, subCategory, topCategory;
    private double totalRatingTaste, totalRatingPrice, rating, averageRatingTaste, averageRatingPrice;

    public ProductDTO (ProductEntity product) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.subCategory = product.getSubCategory().getCategoryName();
        this.topCategory = product.getTopCategory().getCategoryName();
        this.totalRatingTaste = product.getTotalRatingTaste();
        this.totalRatingPrice = product.getTotalRatingPrice();
        this.averageRatingTaste = Math.round((product.getTotalRatingTaste() /
                (double) product.getReviewCount()) * 2) / 2.0;
        this.averageRatingPrice = Math.round((product.getTotalRatingPrice() /
                (double) product.getReviewCount()) * 2) / 2.0;
        this.reviewCount = product.getReviewCount();
    }
}
