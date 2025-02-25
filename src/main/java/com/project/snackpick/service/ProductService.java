package com.project.snackpick.service;

import com.project.snackpick.dto.ProductDTO;

import java.util.List;

public interface ProductService {

    // 제품 검색
    List<ProductDTO> searchProduct(String searchKeyword);

    // 제품 상세 조회
    ProductDTO getProductDetail(int productId);


}
