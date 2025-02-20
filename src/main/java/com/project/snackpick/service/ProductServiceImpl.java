package com.project.snackpick.service;

import com.project.snackpick.entity.ProductEntity;
import com.project.snackpick.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 제품 검색
    @Override
    public List<ProductEntity> searchProduct(String searchKeyword) {
        List<ProductEntity> productList = productRepository.findByProductNameLike("%" + searchKeyword + "%");
        return productList;
    }

    // 제품 상세 조회
    @Override
    public ProductEntity getProductDetail(int productId) {
        ProductEntity product = productRepository.findByProductId(productId);
        return product;
    }
}
