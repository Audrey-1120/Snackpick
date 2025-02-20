package com.project.snackpick.service;

import com.project.snackpick.entity.ProductEntity;

import java.util.List;
import java.util.Map;

public interface ProductService {

    // 제품 검색
    List<ProductEntity> searchProduct(String searchKeyword);

    // 제품 상세 조회
    ProductEntity getProductDetail(int productId);


}
