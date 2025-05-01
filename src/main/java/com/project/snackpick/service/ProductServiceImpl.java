package com.project.snackpick.service;

import com.project.snackpick.dto.ProductDTO;
import com.project.snackpick.dto.UpdateRatingDTO;
import com.project.snackpick.entity.CategoryEntity;
import com.project.snackpick.entity.ProductEntity;
import com.project.snackpick.exception.CustomException;
import com.project.snackpick.exception.ErrorCode;
import com.project.snackpick.repository.CategoryRepository;
import com.project.snackpick.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // 제품 검색
    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> searchProduct(String searchKeyword) {

        List<ProductEntity> productEntityList = productRepository.SearchProductByProductName(searchKeyword);

        if(productEntityList.isEmpty()) {
            return Collections.emptyList();
        }

        List<ProductDTO> productList = productEntityList.stream()
                .map(ProductDTO::new)
                .toList();

        return productList;
    }

    // 제품 상세 조회
    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductDetail(int productId) {

        ProductDTO product = new ProductDTO(productRepository.findProductByProductId(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY,
                        ErrorCode.NOT_FOUND_ENTITY.formatMessage("제품"))));

        return product;
    }

    // 제품 추가
    @Override
    @Transactional
    public ProductEntity insertProduct(ProductDTO productDTO) {

        CategoryEntity topCategory = categoryRepository.findById(Integer.parseInt(productDTO.getTopCategory()))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY,
                        ErrorCode.NOT_FOUND_ENTITY.formatMessage("대분류 카테고리")));

        CategoryEntity subCategory = categoryRepository.findById(Integer.parseInt(productDTO.getSubCategory()))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY,
                        ErrorCode.NOT_FOUND_ENTITY.formatMessage("중분류 카테고리")));

        ProductEntity productEntity = ProductEntity.builder()
                .productName(productDTO.getProductName())
                .topCategory(topCategory)
                .subCategory(subCategory)
                .build();

        return productRepository.save(productEntity);
    }

    // 제품 평점 총합 및 리뷰 개수 업데이트
    @Override
    @Transactional
    public void updateProductRating(UpdateRatingDTO rating, ProductEntity product) {

        double totalTaste = product.getTotalRatingTaste();
        double totalPrice = product.getTotalRatingPrice();
        double oldTaste = rating.getOldRatingTaste();
        double oldPrice = rating.getOldRatingPrice();
        double newTaste = rating.getNewRatingTaste();
        double newPrice = rating.getNewRatingPrice();

        long reviewCount = product.getReviewCount();

        switch (rating.getAction()) {
            case INSERT:
                product.setTotalRatingTaste(totalTaste + newTaste);
                product.setTotalRatingPrice(totalPrice + newPrice);
                product.setReviewCount(reviewCount + 1);
                break;

            case UPDATE:
                product.setTotalRatingTaste(totalTaste - oldTaste + newTaste);
                product.setTotalRatingPrice(totalPrice - oldPrice + newPrice);
                break;

            case DELETE:
                product.setTotalRatingTaste(totalTaste - oldTaste);
                product.setTotalRatingPrice(totalPrice - oldPrice);
                product.setReviewCount(reviewCount - 1);
                break;

            default:
                throw new CustomException(ErrorCode.SERVER_ERROR,
                        ErrorCode.SERVER_ERROR.formatMessage("제품 정보 업데이트"));
        }
    }
}