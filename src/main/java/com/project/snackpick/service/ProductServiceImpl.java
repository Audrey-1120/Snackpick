package com.project.snackpick.service;

import com.project.snackpick.entity.ProductEntity;
import com.project.snackpick.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Map<String, Object> searchProduct(String searchKeyword) {
        // 받은 검색어
        List<ProductEntity> productList = productRepository.findByProductNameLike("%" + searchKeyword + "%");
        return Map.of("productList", productList);
    }

}
