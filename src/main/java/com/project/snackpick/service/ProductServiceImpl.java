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
import org.springframework.transaction.annotation.Propagation;
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
    public ProductDTO getProductDetail(int productId) {

        ProductDTO product = new ProductDTO(productRepository.findProductByProductId(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT)));

        return product;
    }

    // 제품 추가
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ProductEntity insertProduct(ProductDTO productDTO) {

        CategoryEntity topCategory = categoryRepository.findById(Integer.parseInt(productDTO.getTopCategory()))
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 대분류 카테고리를 찾을 수 없습니다."));

        CategoryEntity subCategory = categoryRepository.findById(Integer.parseInt(productDTO.getSubCategory()))
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 중분류 카테고리를 찾을 수 없습니다."));

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
    public void updateProductRating(UpdateRatingDTO rating) {

        ProductEntity productEntity = productRepository.findProductByProductId(rating.getProductId())
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

        switch (rating.getAction()) {
            case INSERT:
                productEntity.setTotalRatingTaste(productEntity.getTotalRatingTaste() + rating.getNewRatingTaste());
                productEntity.setTotalRatingPrice(productEntity.getTotalRatingPrice() + rating.getNewRatingPrice());
                productEntity.setReviewCount(productEntity.getReviewCount() + 1);
                break;

            case UPDATE:
                productEntity.setTotalRatingTaste(productEntity.getTotalRatingTaste() - rating.getOldRatingTaste() + rating.getNewRatingTaste());
                productEntity.setTotalRatingPrice(productEntity.getTotalRatingPrice() - rating.getOldRatingPrice() + rating.getNewRatingPrice());
                break;

            case DELETE:
                productEntity.setTotalRatingTaste(productEntity.getTotalRatingTaste() - rating.getOldRatingTaste());
                productEntity.setTotalRatingPrice(productEntity.getTotalRatingPrice() - rating.getOldRatingPrice());
                productEntity.setReviewCount(productEntity.getReviewCount() - 1);
                break;

            default:
                throw new CustomException(ErrorCode.SERVER_ERROR,
                        ErrorCode.SERVER_ERROR.formatMessage("제품의 평점 업데이트"));
        }
    }
}
