package com.project.snackpick.controller.review;

import com.project.snackpick.dto.CustomUserDetails;
import com.project.snackpick.dto.PageDTO;
import com.project.snackpick.dto.ReviewDTO;
import com.project.snackpick.dto.ReviewRequestDTO;
import com.project.snackpick.service.ReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Controller
@RequestMapping("/review")
@Tag(name = "Review", description = "리뷰 API")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // 리뷰 작성
    @PostMapping(value = "/insertReview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> insertReview(@RequestPart("reviewRequest") ReviewRequestDTO reviewRequestDTO,
                                                            @RequestPart(value = "reviewImageList", required = false) MultipartFile[] reviewImageList,
                                                            @AuthenticationPrincipal CustomUserDetails user) {

        Map<String, Object> response = reviewService.insertReview(reviewRequestDTO, reviewImageList, user);
        return ResponseEntity.ok(response);
    }

    // 리뷰 리스트 조회
    @GetMapping("/getReviewList")
    public ResponseEntity<Map<String, Object>> getReviewList(@PageableDefault(size = 6, sort = "createDt", direction = Sort.Direction.DESC) Pageable pageable,
                                                             @RequestParam int productId) {
        // @PageableDefault를 사용해서 page와 size, sort를 정할 수 있다.
        PageDTO<ReviewDTO> reviewList = reviewService.getReviewList(pageable, productId);
        return ResponseEntity.ok(Map.of("reviewList", reviewList));
    }

    // 리뷰 상세 조회
    @GetMapping("/getReviewDetail")
    public ResponseEntity<Map<String, Object>> getReviewDetail(@RequestParam int reviewId) {
        ReviewDTO review = reviewService.getReviewDetail(reviewId);
        return ResponseEntity.ok(Map.of("review", review));
    }

    // 리뷰 삭제
    @PutMapping("/deleteReview")
    public ResponseEntity<Map<String, Object>> deleteReview(@RequestBody Map<String, String> param,
                                                            @AuthenticationPrincipal CustomUserDetails user) {

        int reviewId = Integer.parseInt(param.get("reviewId"));
        Map<String, Object> response = reviewService.deleteReview(reviewId, user);
        return ResponseEntity.ok(response);
    }

}