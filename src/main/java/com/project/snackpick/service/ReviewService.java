package com.project.snackpick.service;

import com.project.snackpick.dto.CustomUserDetails;
import com.project.snackpick.dto.PageDTO;
import com.project.snackpick.dto.ReviewDTO;
import com.project.snackpick.dto.ReviewRequestDTO;
import com.project.snackpick.entity.ReviewEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ReviewService {

    // 리뷰 작성
    Map<String, Object> insertReview(ReviewRequestDTO reviewRequestDTO, MultipartFile[] reviewImageList, CustomUserDetails user);

    // 리뷰 이미지 저장
    void insertReviewImage(MultipartFile[] reviewImageList, ReviewEntity reviewEntity, int representIndex);

    // 리뷰 이미지 삭제
    void deleteReviewImage(ReviewEntity reviewEntity);

    // 리뷰 이미지 대표 여부 설정
    void updateRepresentImage(int reviewImageId, ReviewEntity reviewEntity);

    // 리뷰 리스트 조회
    PageDTO<ReviewDTO> getReviewList(Pageable pageable, int productNo);

    // 리뷰 상세 조회
    ReviewDTO getReviewDetail(int reviewId);

    // 리뷰 삭제
    Map<String, Object> deleteReview(int reviewId, CustomUserDetails user);

    // 리뷰 수정
    Map<String, Object> updateReview(ReviewRequestDTO reviewRequestDTO, MultipartFile[] reviewImageList, CustomUserDetails user);

}
