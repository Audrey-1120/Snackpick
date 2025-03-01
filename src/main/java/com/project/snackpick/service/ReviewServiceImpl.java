package com.project.snackpick.service;

import com.project.snackpick.dto.CustomUserDetails;
import com.project.snackpick.dto.ProductDTO;
import com.project.snackpick.dto.ReviewDTO;
import com.project.snackpick.dto.ReviewRequestDTO;
import com.project.snackpick.entity.*;
import com.project.snackpick.repository.*;
import com.project.snackpick.utils.MyFileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Map;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final MyFileUtils myFileUtils;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ReviewImageRepository reviewImageRepository;

    public ReviewServiceImpl(MyFileUtils myFileUtils, ProductService productService, ProductRepository productRepository, ReviewRepository reviewRepository, MemberRepository memberRepository, ReviewImageRepository reviewImageRepository) {
        this.myFileUtils = myFileUtils;
        this.productService = productService;
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
        this.memberRepository = memberRepository;
        this.reviewImageRepository = reviewImageRepository;
    }

    // 리뷰 작성
    @Override
    @Transactional
    public Map<String, Object> insertReview(ReviewRequestDTO reviewRequestDTO, MultipartFile[] reviewImageList, CustomUserDetails user) {

        ProductDTO productDTO = reviewRequestDTO.getProductDTO();
        ReviewDTO reviewDTO = reviewRequestDTO.getReviewDTO();
        ProductEntity productEntity;

        // 제품 직접 추가 여부
        if(reviewRequestDTO.isAddProduct()) {
            productEntity = productService.insertProduct(productDTO); // 제품 추가
        } else {
            productEntity = productRepository.findProductByProductId(reviewDTO.getProductId()); // 기존 제품 조회
        }

        // 리뷰 저장
        MemberEntity memberEntity = memberRepository.findById(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 회원 정보를 찾을 수 없습니다."));

        reviewDTO.setMemberId(user.getMemberId());
        ReviewEntity reviewEntity = ReviewEntity.toReviewEntity(reviewDTO, memberEntity, productEntity);
        reviewRepository.save(reviewEntity);

        // 제품 평점 합, 리뷰 개수 업데이트
        productService.updateProductRating(productEntity.getProductId(), reviewEntity.getRatingTaste(), reviewEntity.getRatingPrice());

        // 첨부한 리뷰 이미지 있을 시 저장
        if(reviewImageList != null) {
            insertReviewImage(reviewImageList, reviewEntity, reviewRequestDTO.getRepresentIndex());
        }

        return Map.of("success", true
                    , "message", "리뷰가 작성되었습니다."
                    , "redirectUrl", "/product/productDetail.page?productId=" + productDTO.getProductId());
    }

    // 리뷰 이미지 저장
    @Override
    public void insertReviewImage(MultipartFile[] reviewImageList, ReviewEntity reviewEntity, int representIndex) {

        // 리뷰 이미지 업로드
        ArrayList<String> reviewImagePath = myFileUtils.uploadReviewImage(reviewImageList);

        // 이미지 경로 리스트 반복해서 돌면서 DB에 저장
        for(int i = 0; i < reviewImagePath.size(); i++) {

            // 리뷰 이미지 엔티티 생성
            ReviewImageEntity reviewImageEntity = ReviewImageEntity.builder()
                    .reviewEntity(reviewEntity)
                    .reviewImagePath(reviewImagePath.get(i))
                    .build();

            // 대표 이미지 여부 설정
            if(i == representIndex) {
                reviewImageEntity.setRepresent(true);
            } else {
                reviewImageEntity.setRepresent(false);
            }

            // 리뷰 이미지 저장
            reviewImageRepository.save(reviewImageEntity);

        }
    }
}
