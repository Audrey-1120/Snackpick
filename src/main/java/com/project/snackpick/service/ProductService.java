package com.project.snackpick.service;

import java.util.Map;

public interface ProductService {

    // 상품 검색 api
    Map<String, Object> searchProduct(String searchKeyword);

}
