package com.project.snackpick.dto;

import com.project.snackpick.entity.ProductEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDTO {

    private int productId;
    private String productName, productCat1Nm, productCat2Nm;
    private double RatingTasteAverage, RatingPriceAverage;

    public ProductEntity toProductEntity(ProductDTO productDTO) {
        return ProductEntity.builder()
                .productName(productDTO.getProductName())
                .productCat1Name(productDTO.getProductCat1Nm())
                .productCat2Name(productDTO.getProductCat2Nm())
                .ratingTasteAverage(productDTO.getRatingTasteAverage())
                .ratingPriceAverage(productDTO.getRatingPriceAverage())
                .build();
    }


}
