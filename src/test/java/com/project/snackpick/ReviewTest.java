package com.project.snackpick;

import com.project.snackpick.dto.*;
import com.project.snackpick.entity.MemberEntity;
import com.project.snackpick.service.ReviewService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@DisplayName("리뷰 테스트")
public class ReviewTest {

    private final ReviewService reviewService;

    @Autowired
    public ReviewTest(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @BeforeEach
    public void before() {
        System.out.println("Test Before");
    }

    @AfterEach
    public void after() {
        System.out.println("Test After");
    }

    @Test
    @DisplayName("기존 제품 리뷰 작성")
    public void insertExistProductReview() throws Exception {

        // ReviewDTO 객체 생성
        ReviewDTO reviewDTO = ReviewDTO.builder()
                .productId(1)
                .state(false)
                .ratingTaste(2.5)
                .ratingPrice(3.5)
                .content("리뷰 테스트 내용1")
                .location("세븐일레븐 종로재동점")
                .build();

        // ReviewRequestDTO 객체 생성
        ReviewRequestDTO reviewRequestDTO = ReviewRequestDTO.builder()
                .addProduct(false)
                .representIndex(1)
                .reviewDTO(reviewDTO)
                .build();

        // MemberEntity 객체 생성
        MemberEntity memberEntity = MemberEntity.builder()
                .memberId(1)
                .id("mintii92")
                .build();

        // MockMultipartFile 여러개 생성 (필드명은 images로 통일한다.)
        MockMultipartFile file1 = new MockMultipartFile(
                "reviewImage", // 필드명
                "test1.jpg", // 파일명
                "image/jpeg", // MIME 타입
                "image data".getBytes(StandardCharsets.UTF_8) // 가짜 데이터
        );

        MockMultipartFile file2 = new MockMultipartFile(
                "reviewImage", // 필드명
                "test2.jpg", // 파일명
                "image/jpeg", // MIME 타입
                "image data".getBytes(StandardCharsets.UTF_8) // 가짜 데이터
        );

        // MultipartFile 배열 초기화
        MultipartFile[] files = {file1, file2};

        // CustomDetails 객체 생성
        CustomUserDetails user = new CustomUserDetails(memberEntity);

        // 서비스 호출
        Map<String, Object> map = reviewService.insertReview(reviewRequestDTO, files, user);

        System.out.println("완료 메시지: " + String.valueOf(map.get("message")));
        System.out.println("리다이렉트 경로" + String.valueOf(map.get("redirectUrl")));

        // success값이 true인가?
        assertTrue((Boolean)map.get("success"));
    }

    @Test
    @DisplayName("새 제품 리뷰 작성")
    public void insertNewProductReview() throws Exception {

        // ReviewDTO 객체 생성
        ReviewDTO reviewDTO = ReviewDTO.builder()
                .state(false)
                .ratingTaste(2.5)
                .ratingPrice(3.5)
                .content("리뷰 테스트 내용1")
                .location("세븐일레븐 종로재동점")
                .build();

        ProductDTO productDTO = ProductDTO.builder()
                .productName("과자1")
                .topCategory("3")
                .subCategory("14")
                .build();

        // ReviewRequestDTO 객체 생성
        ReviewRequestDTO reviewRequestDTO = ReviewRequestDTO.builder()
                .addProduct(true)
                .representIndex(1)
                .reviewDTO(reviewDTO)
                .productDTO(productDTO)
                .build();

        // MemberEntity 객체 생성
        MemberEntity memberEntity = MemberEntity.builder()
                .memberId(1)
                .id("mintii92")
                .name("김민티")
                .nickname("민티927")
                .build();

        // MockMultipartFile 여러개 생성 (필드명은 images로 통일한다.)
        MockMultipartFile file1 = new MockMultipartFile(
                "reviewImage", // 필드명
                "test1.jpg", // 파일명
                "image/jpeg", // MIME 타입
                "image data".getBytes(StandardCharsets.UTF_8) // 가짜 데이터
        );

        MockMultipartFile file2 = new MockMultipartFile(
                "reviewImage", // 필드명
                "test2.jpg", // 파일명
                "image/jpeg", // MIME 타입
                "image data".getBytes(StandardCharsets.UTF_8) // 가짜 데이터
        );

        // MultipartFile 배열 초기화
        MultipartFile[] files = {file1, file2};

        // CustomDetails 객체 생성
        CustomUserDetails user = new CustomUserDetails(memberEntity);

        // 서비스 호출
        Map<String, Object> map = reviewService.insertReview(reviewRequestDTO, files, user);

        System.out.println("완료 메시지: " + String.valueOf(map.get("message")));
        System.out.println("리다이렉트 경로" + String.valueOf(map.get("redirectUrl")));

        // success값이 true인가?
        assertTrue((Boolean)map.get("success"));

    }

    @Test
    @DisplayName("리뷰 목록 조회")
    public void getReviewList() throws Exception {

        int page = 1;
        int size = 4;
        int productId = 1;

        Pageable pageable = PageRequest.of(page, size, Sort.by("createDt").descending());

        PageDTO<ReviewDTO> reviewDTO = reviewService.getReviewList(pageable, productId);

        assertNotNull(reviewDTO);

        for(ReviewDTO review : reviewDTO.getContents()) {
            System.out.println("리뷰 ID: " + review.getReviewId());
            System.out.println("리뷰 내용: " + review.getContent());
        }
    }

    @Test
    @DisplayName("리뷰 상세 조회")
    public void getReviewDetail() throws Exception {

        ReviewDTO reviewDTO = reviewService.getReviewDetail(1);
        System.out.println("리뷰 ID: " + reviewDTO.getReviewId());
        System.out.println("리뷰 내용: " + reviewDTO.getContent());

        assertNotNull(reviewDTO);

    }

    @Test
    @DisplayName("리뷰 수정")
    public void updateReview() throws Exception {

        // ReviewDTO 객체 생성
        ReviewDTO reviewDTO = ReviewDTO.builder()
                .reviewId(1)
                .productId(1)
                .state(false)
                .ratingTaste(2.5)
                .ratingPrice(3.5)
                .content("리뷰 테스트 내용 수정")
                .location("세븐일레븐 종로재동점")
                .build();

        // ReviewRequestDTO 객체 생성
        ReviewRequestDTO reviewRequestDTO = ReviewRequestDTO.builder()
                .representIndex(1)
                .reviewDTO(reviewDTO)
                .build();

        // MemberEntity 객체 생성
        MemberEntity memberEntity = MemberEntity.builder()
                .memberId(1)
                .id("mintii92")
                .name("김민티")
                .nickname("민티927")
                .build();

        // MockMultipartFile 여러개 생성 (필드명은 images로 통일한다.)
        MockMultipartFile file1 = new MockMultipartFile(
                "reviewImage", // 필드명
                "test1.jpg", // 파일명
                "image/jpeg", // MIME 타입
                "image data".getBytes(StandardCharsets.UTF_8) // 가짜 데이터
        );

        MockMultipartFile file2 = new MockMultipartFile(
                "reviewImage", // 필드명
                "test2.jpg", // 파일명
                "image/jpeg", // MIME 타입
                "image data".getBytes(StandardCharsets.UTF_8) // 가짜 데이터
        );

        // MultipartFile 배열 초기화
        MultipartFile[] files = {file1, file2};

        // CustomDetails 객체 생성
        CustomUserDetails user = new CustomUserDetails(memberEntity);

        // 서비스 호출
        Map<String, Object> map = reviewService.updateReview(reviewRequestDTO, files, user);

        System.out.println("완료 메시지: " + String.valueOf(map.get("message")));
        System.out.println("리다이렉트 경로" + String.valueOf(map.get("redirectUrl")));

        // success값이 true인가?
        assertTrue((Boolean)map.get("success"));

    }

    @Test
    @DisplayName("리뷰 삭제")
    public void deleteReview() throws Exception {

        // MemberEntity 객체 생성
        MemberEntity memberEntity = MemberEntity.builder()
                .memberId(1)
                .id("mintii92")
                .name("김민티")
                .nickname("민티927")
                .build();

        CustomUserDetails user = new CustomUserDetails(memberEntity);

        Map<String, Object> map = reviewService.deleteReview(1, user);

        System.out.println("완료 메시지: " + String.valueOf(map.get("message")));
        System.out.println("리다이렉트 경로" + String.valueOf(map.get("redirectUrl")));

        // success값이 true인가?
        assertTrue((Boolean)map.get("success"));

    }
}
