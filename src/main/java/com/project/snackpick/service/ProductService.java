package com.project.snackpick.service;

import com.project.snackpick.dto.ProductDTO;
import com.project.snackpick.dto.UpdateRatingDTO;
import com.project.snackpick.entity.ProductEntity;

import java.util.List;

public interface ProductService {

    // 제품 검색
    List<ProductDTO> searchProduct(String searchKeyword);

    // 제품 상세 조회
    ProductDTO getProductDetail(int productId);

    // 제품 추가
    ProductEntity insertProduct(ProductDTO productDTO);

    // 제품 평점 총합, 리뷰 개수 업데이트
    void updateProductRating(UpdateRatingDTO updateRatingDTO);

}
