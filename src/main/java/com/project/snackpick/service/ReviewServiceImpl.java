package com.project.snackpick.service;

import com.project.snackpick.dto.*;
import com.project.snackpick.entity.MemberEntity;
import com.project.snackpick.entity.ProductEntity;
import com.project.snackpick.entity.ReviewEntity;
import com.project.snackpick.entity.ReviewImageEntity;
import com.project.snackpick.exception.CustomException;
import com.project.snackpick.exception.ErrorCode;
import com.project.snackpick.repository.MemberRepository;
import com.project.snackpick.repository.ProductRepository;
import com.project.snackpick.repository.ReviewImageRepository;
import com.project.snackpick.repository.ReviewRepository;
import com.project.snackpick.utils.MyFileUtils;
import com.project.snackpick.utils.MyPageUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final MyFileUtils myFileUtils;
    private final MyPageUtils myPageUtils;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ReviewImageRepository reviewImageRepository;

    public ReviewServiceImpl(MyFileUtils myFileUtils, MyPageUtils myPageUtils, ProductService productService, ProductRepository productRepository, ReviewRepository reviewRepository, MemberRepository memberRepository, ReviewImageRepository reviewImageRepository) {
        this.myFileUtils = myFileUtils;
        this.myPageUtils = myPageUtils;
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
            productEntity = productRepository.findProductByProductId(reviewDTO.getProductId())
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT)); // 기존 제품 조회
        }

        // 리뷰 저장
        MemberEntity memberEntity = memberRepository.findById(user.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

        reviewDTO.setMember(new MemberDTO(memberEntity));
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
                    , "redirectUrl", "/product/productDetail.page?productId=" + productEntity.getProductId());
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

    // 리뷰 목록 조회
    @Override
    @Transactional(readOnly = true)
    public PageDTO<ReviewDTO> getReviewList(Pageable pageable, int productId) {

        // 페이지 0 이상일 경우 1 빼줌.
        int pageNumber = (pageable.getPageNumber() > 0) ? pageable.getPageNumber() - 1: 0;
        // 같은 값이 있을 경우 reviewId 오름차순으로 정렬
        Sort sort = pageable.getSort().and(Sort.by(Sort.Order.asc("reviewId")));

        // 페이지 번호 조정해서 pageable 객체 생성
        Pageable finalPageable = PageRequest.of(pageNumber, pageable.getPageSize(), sort);

        // 리뷰 데이터 1차 조회 - 페이징
        Page<ReviewEntity> reviewEntityPage = reviewRepository.findByReviewListByProductId(productId, finalPageable);

        // 리뷰 데이터 2차 조회 - 리뷰 이미지, 작성자 프로필
        List<ReviewEntity> reviewListWithImage = reviewRepository.findReviewListWithImage(reviewEntityPage.getContent());

        // ReviewDTO로 변환
        List<ReviewDTO> reviewDTOList = reviewListWithImage.stream()
                .map(ReviewDTO::new)
                .toList();

        Page<ReviewDTO> finalPage = new PageImpl<>(reviewDTOList, finalPageable, reviewEntityPage.getTotalElements());
        return myPageUtils.toPageDTO(finalPage);

    }

    // 리뷰 상세 조회
    @Override
    @Transactional(readOnly = true)
    public ReviewDTO getReviewDetail(int reviewId) {

        ReviewEntity reviewEntity = reviewRepository.findReviewByReviewId(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

        ReviewDTO review = new ReviewDTO(reviewEntity);
        return review;
    }
}
