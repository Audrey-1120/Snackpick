package com.project.snackpick.service;

import com.project.snackpick.dto.CustomUserDetails;
import com.project.snackpick.dto.PageDTO;
import com.project.snackpick.dto.ReviewDTO;
import com.project.snackpick.dto.ReviewRequestDTO;
import com.project.snackpick.entity.ProductEntity;
import com.project.snackpick.entity.ReviewEntity;
import com.project.snackpick.enums.UpdateAction;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ReviewService {

    /*
        리뷰 CRUD
     */

    // 리뷰 작성
    Map<String, Object> insertReview(ReviewRequestDTO reviewRequestDTO, MultipartFile[] files, CustomUserDetails user);

    // 리뷰 목록 조회
    PageDTO<ReviewDTO> getReviewList(Pageable pageable, int productId);

    // 회원별 리뷰 목록 조회
    PageDTO<ReviewDTO> getReviewListByMemberId(Pageable pageable, CustomUserDetails user);

    // 리뷰 상세 조회
    ReviewDTO getReviewDetail(int reviewId);

    // 리뷰 삭제
    Map<String, Object> deleteReview(int reviewId, CustomUserDetails user);

    // 리뷰 목록 삭제
    void deleteReviewList(List<ReviewEntity> reviewList);

    // 리뷰 수정
    Map<String, Object> updateReview(ReviewRequestDTO reviewRequestDTO, MultipartFile[] files, CustomUserDetails user);



    /*
        이미지 처리
     */

    // 리뷰 이미지 저장
    void insertReviewImage(MultipartFile[] files, ReviewEntity review, int representIndex);

    // 리뷰 이미지 삭제
    void deleteReviewImage(ReviewEntity review);

    // 리뷰 이미지 수정
    void updateReviewImage(ReviewRequestDTO reviewRequestDTO, MultipartFile[] files, ReviewEntity review);

    // 리뷰 이미지 대표 여부 설정
    void updateRepresentImage(int reviewImageId, ReviewEntity review);



    /*
        공통 로직
     */

    // 제품 평점 및 리뷰 개수 업데이트
    void updateProductStats(ProductEntity product, ReviewEntity review, ReviewDTO reviewDTO, UpdateAction action);

    // 권한 확인
    boolean hasPermission(ReviewEntity review, CustomUserDetails user);



    /*
        하위 처리
     */

    // 리뷰 데이터 저장
    ReviewEntity insertReviewFields(ReviewRequestDTO reviewRequestDTO, ProductEntity product, CustomUserDetails user);

    // 리뷰 데이터 수정
    void updateReviewFields(ReviewEntity review, ReviewDTO reviewDTO);

    // 제품 처리
    ProductEntity handleProduct(ReviewRequestDTO reviewRequestDTO);

}
