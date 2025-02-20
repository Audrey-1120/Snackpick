package com.project.snackpick.dto;

import com.project.snackpick.entity.ProductEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDTO {

    private int productId;
    private String productName, productCat1Name, productCat2Name;
    private double ratingTasteAverage, ratingPriceAverage;

    public ProductEntity toProductEntity(ProductDTO productDTO) {
        return ProductEntity.builder()
                .productName(productDTO.getProductName())
                .productCat1Name(productDTO.getProductCat1Name())
                .productCat2Name(productDTO.getProductCat2Name())
                .ratingTasteAverage(productDTO.getRatingTasteAverage())
                .ratingPriceAverage(productDTO.getRatingPriceAverage())
                .build();
    }


}
