package com.project.snackpick.service;

import com.project.snackpick.dto.CustomUserDetails;
import com.project.snackpick.dto.ReviewRequestDTO;
import com.project.snackpick.entity.ReviewEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ReviewService {

    // 리뷰 작성
    Map<String, Object> insertReview(ReviewRequestDTO reviewRequestDTO, MultipartFile[] reviewImageList, CustomUserDetails user);

    // 리뷰 이미지 저장
    void insertReviewImage(MultipartFile[] reviewImageList, ReviewEntity reviewEntity, int representIndex);

}
