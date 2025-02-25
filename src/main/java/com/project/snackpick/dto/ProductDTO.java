package com.project.snackpick.dto;

import com.project.snackpick.entity.ProductEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ProductDTO {

    private int productId;
    private long reviewCount;
    private String productName, subCategory, topCategory;
    private double ratingTasteAverage, ratingPriceAverage;

    public ProductDTO (ProductEntity product, long reviewCount) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.subCategory = product.getSubCategory().getCategoryName();
        this.topCategory = product.getTopCategory().getCategoryName();
        this.ratingTasteAverage = product.getRatingTasteAverage();
        this.ratingPriceAverage= product.getRatingPriceAverage();
        this.reviewCount = reviewCount;
    }

}
