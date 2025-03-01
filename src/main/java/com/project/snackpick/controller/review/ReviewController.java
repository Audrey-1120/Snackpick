package com.project.snackpick.controller.review;

import com.project.snackpick.dto.CustomUserDetails;
import com.project.snackpick.dto.ReviewRequestDTO;
import com.project.snackpick.service.ReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
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
    public ResponseEntity<Map<String, Object>> insertReview(@RequestPart("reviewRequest") ReviewRequestDTO reviewRequestDTO,
                                                            @RequestPart(value = "reviewImageList", required = false) MultipartFile[] reviewImageList,
                                                            @AuthenticationPrincipal CustomUserDetails user) {

        Map<String, Object> response = reviewService.insertReview(reviewRequestDTO, reviewImageList, user);
        return ResponseEntity.ok(response);
    }
}
