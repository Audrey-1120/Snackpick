package com.project.snackpick.controller.review;

import com.project.snackpick.dto.*;
import com.project.snackpick.service.CommentService;
import com.project.snackpick.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/review")
@Tag(name = "Review", description = "리뷰 API")
public class ReviewController {

    private final ReviewService reviewService;
    private final CommentService commentService;

    public ReviewController(ReviewService reviewService, CommentService commentService) {
        this.reviewService = reviewService;
        this.commentService = commentService;
    }

    // 리뷰 작성
    @PostMapping(value = "/insertReview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "리뷰 작성", description = "리뷰 작성")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "404", description = "기존 제품 정보 혹은 로그인한 멤버 정보가 없음")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> insertReview(@RequestPart("reviewRequest") ReviewRequestDTO reviewRequestDTO,
                                                            @RequestPart(value = "reviewImageList", required = false) MultipartFile[] reviewImageList,
                                                            @AuthenticationPrincipal CustomUserDetails user) {

        Map<String, Object> response = reviewService.insertReview(reviewRequestDTO, reviewImageList, user);
        return ResponseEntity.ok(response);
    }

    // 리뷰 리스트 조회
    @GetMapping("/getReviewList")
    @Operation(summary = "리뷰 목록 조회", description = "리뷰 목록 조회")
    @ApiResponse(responseCode = "200", description = "성공")
    public ResponseEntity<Map<String, Object>> getReviewList(@PageableDefault(size = 4, sort = "createDt", direction = Sort.Direction.DESC) Pageable pageable,
                                                             @RequestParam int productId) {

        PageDTO<ReviewDTO> reviewList = reviewService.getReviewList(pageable, productId);
        return ResponseEntity.ok(Map.of("reviewList", reviewList));
    }

    // 리뷰 상세 조회
    @GetMapping("/getReviewDetail")
    @Operation(summary = "리뷰 상세 조회", description = "리뷰 삳세 조회")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "404", description = "리뷰 ID에 해당하는 제품 정보가 없음.")
    public ResponseEntity<Map<String, Object>> getReviewDetail(@RequestParam int reviewId) {

        ReviewDTO review = reviewService.getReviewDetail(reviewId);
        List<CommentDTO> commentList = commentService.getCommentList(reviewId);
        return ResponseEntity.ok(Map.of("review", review,
                                        "commentList", commentList));
    }

    // 리뷰 삭제
    @PutMapping("/deleteReview")
    @Operation(summary = "리뷰 삭제", description = "리뷰 삭제")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "404", description = "리뷰 ID에 해당하는 리뷰 데이터가 없음.")
    @ApiResponse(responseCode = "403", description = "리뷰를 삭제할 권한이 없음.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> deleteReview(@RequestBody Map<String, String> param,
                                                            @AuthenticationPrincipal CustomUserDetails user) {

        int reviewId = Integer.parseInt(param.get("reviewId"));
        Map<String, Object> response = reviewService.deleteReview(reviewId, user);
        return ResponseEntity.ok(response);
    }

    // 리뷰 수정
    @PutMapping("/updateReview")
    @Operation(summary = "리뷰 수정", description = "리뷰 수정")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "404", description = "리뷰 ID에 해당하는 리뷰 데이터가 없음.")
    @ApiResponse(responseCode = "403", description = "리뷰를 수정할 권한이 없음.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> updateReview(@RequestPart("reviewRequest") ReviewRequestDTO reviewRequestDTO,
                                                            @RequestPart(value = "reviewImageList", required = false) MultipartFile[] reviewImageList,
                                                            @AuthenticationPrincipal CustomUserDetails user) {

        Map<String, Object> response = reviewService.updateReview(reviewRequestDTO, reviewImageList, user);
        return ResponseEntity.ok(response);
    }
}