package com.project.snackpick.service;

import com.project.snackpick.dto.ProductDTO;
import com.project.snackpick.entity.ProductEntity;
import com.project.snackpick.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 제품 검색
    @Override
    public List<ProductDTO> searchProduct(String searchKeyword) {
        List<Object[]> productList = productRepository.SearchProductByProductName(searchKeyword);

        if(productList.isEmpty()) {
            return new ArrayList<>();
        }

        return productList.stream()
                .map(product -> new ProductDTO((ProductEntity) product[0], (long) product[1]))
                .collect(Collectors.toList());
    }

    // 제품 상세 조회
    @Override
    public ProductDTO getProductDetail(int productId) {
        ProductDTO product = new ProductDTO(productRepository.findProductByProductId(productId), 0);
        return product;
    }
}
