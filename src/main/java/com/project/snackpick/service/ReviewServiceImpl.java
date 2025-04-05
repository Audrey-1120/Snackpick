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

import java.io.IOException;
import java.time.LocalDateTime;
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
    public Map<String, Object> insertReview(ReviewRequestDTO reviewRequestDTO, MultipartFile[] files, CustomUserDetails user) {

        ProductDTO productDTO = reviewRequestDTO.getProductDTO();
        ReviewDTO reviewDTO = reviewRequestDTO.getReviewDTO();
        ProductEntity productEntity;

        if(reviewRequestDTO.isAddProduct()) {
            productEntity = productService.insertProduct(productDTO);
        } else {
            productEntity = productRepository.findProductByProductId(reviewDTO.getProductId())
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));
        }

        MemberEntity member = memberRepository.findById(user.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

        reviewDTO.setMember(new MemberDTO(member));
        ReviewEntity review = ReviewEntity.toReviewEntity(reviewDTO, member, productEntity);

        reviewRepository.save(review);

        UpdateRatingDTO updateRatingDTO = new UpdateRatingDTO(
                productEntity.getProductId(),
                0,
                0,
                review.getRatingTaste(),
                review.getRatingPrice(),
                ReviewAction.INSERT
        );

        productService.updateProductRating(updateRatingDTO);

        try {
            insertReviewImage(files, review, reviewRequestDTO.getRepresentIndex());
        } catch(IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAIL,
                    ErrorCode.FILE_UPLOAD_FAIL.formatMessage("프로필"));
        }

        return Map.of("success", true
                    , "message", "리뷰가 작성되었습니다."
                    , "redirectUrl", "/product/productDetail.page?productId=" + productEntity.getProductId());
    }

    // 리뷰 이미지 저장
    @Override
    public void insertReviewImage(MultipartFile[] files, ReviewEntity review, int representIndex) throws IOException {

        if(files == null || files.length == 0 || files[0].isEmpty()) {
            return;
        }

        List<String> imageUrlList = myFileUtils.getImageUrlList(files, "review");

        for(int i = 0; i < imageUrlList.size(); i++) {

            ReviewImageEntity reviewImage = ReviewImageEntity.builder()
                    .reviewEntity(review)
                    .reviewImagePath(imageUrlList.get(i))
                    .build();

            reviewImage.setRepresent(i == representIndex);
            reviewImageRepository.save(reviewImage);
        }

        myFileUtils.uploadImage(files, imageUrlList, "review");
    }

    // 리뷰 목록 조회
    @Override
    @Transactional(readOnly = true)
    public PageDTO<ReviewDTO> getReviewList(Pageable pageable, int productId) {

        int pageNumber = (pageable.getPageNumber() > 0) ? pageable.getPageNumber() - 1: 0;
        Sort sort = pageable.getSort().and(Sort.by(Sort.Order.asc("reviewId")));
        Pageable finalPageable = PageRequest.of(pageNumber, pageable.getPageSize(), sort);

        Page<ReviewEntity> reviewEntityPage = reviewRepository.findByReviewListByProductId(productId, finalPageable);
        List<ReviewEntity> reviewListWithImage = reviewRepository.findReviewListWithImage(reviewEntityPage.getContent());

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

    // 리뷰 삭제
    @Override
    @Transactional
    public Map<String, Object> deleteReview(int reviewId, CustomUserDetails user) {

        ReviewEntity review = reviewRepository.findReviewByReviewId(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

        if(review.getMemberEntity().getMemberId() != user.getMemberId()) {
            throw new CustomException(ErrorCode.NOT_PERMISSION,
                    ErrorCode.NOT_PERMISSION.formatMessage("리뷰 삭제"));
        }

        if(review.isState()) {
            throw new CustomException(ErrorCode.ALREADY_DELETE_REVIEW);
        }

        review.setState(true);

        UpdateRatingDTO updateRatingDTO = new UpdateRatingDTO(
                review.getProductEntity().getProductId(),
                review.getRatingTaste(),
                review.getRatingPrice(),
                0,
                0,
                ReviewAction.DELETE
        );

        productService.updateProductRating(updateRatingDTO);

        return Map.of("success", true
                , "message", "리뷰가 삭제되었습니다.");
    }

    // 리뷰 수정
    @Override
    @Transactional
    public Map<String, Object> updateReview(ReviewRequestDTO reviewRequestDTO, MultipartFile[] files, CustomUserDetails user) {

        ReviewDTO reviewDTO = reviewRequestDTO.getReviewDTO();

        ReviewEntity review = reviewRepository.findReviewByReviewId(reviewDTO.getReviewId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

        if(review.getMemberEntity().getMemberId() != user.getMemberId()) {
            throw new CustomException(ErrorCode.NOT_PERMISSION,
                    ErrorCode.NOT_PERMISSION.formatMessage("리뷰 수정"));
        }

        double oldRatingTaste = review.getRatingTaste();
        double oldRatingPrice = review.getRatingPrice();

        ProductEntity productEntity = productRepository.findProductByProductId(reviewDTO.getProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

        review.setRatingTaste(reviewDTO.getRatingTaste());
        review.setRatingPrice(reviewDTO.getRatingPrice());
        review.setContent(reviewDTO.getContent());
        review.setLocation(reviewDTO.getLocation());
        review.setUpdateDt(LocalDateTime.now());

        UpdateRatingDTO updateRatingDTO = new UpdateRatingDTO(
                productEntity.getProductId(),
                oldRatingTaste,
                oldRatingPrice,
                reviewDTO.getRatingTaste(),
                reviewDTO.getRatingPrice(),
                ReviewAction.UPDATE
        );

        productService.updateProductRating(updateRatingDTO);

        try {
            updateReviewImage(reviewRequestDTO, files, review);
        } catch(IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAIL,
                    ErrorCode.FILE_UPLOAD_FAIL.formatMessage("리뷰"));
        }

        return Map.of("success", true
                , "message", "리뷰가 수정되었습니다."
                , "redirectUrl", "/product/productDetail.page?productId=" + productEntity.getProductId());
    }

    // 리뷰 이미지 수정
    @Override
    public void updateReviewImage(ReviewRequestDTO reviewRequestDTO, MultipartFile[] files, ReviewEntity review) throws IOException {

        if (reviewRequestDTO.isDeleteAllImageList()) {
            deleteReviewImage(review);
        } else if (files != null) {
            deleteReviewImage(review);
            insertReviewImage(files, review, reviewRequestDTO.getRepresentIndex());
        } else if (reviewRequestDTO.getRepresentImageId() != 0) {
            updateRepresentImage(reviewRequestDTO.getRepresentImageId(), review);
        }
    }

    // 리뷰 이미지 삭제
    @Override
    public void deleteReviewImage(ReviewEntity review) {

        List<ReviewImageEntity> reviewImageList = review.getReviewImageEntityList();

        if(reviewImageList == null || reviewImageList.isEmpty()) {
            return;
        }

        List<String> imageURlList = reviewImageList.stream()
                .map(ReviewImageEntity::getReviewImagePath)
                .toList();

        reviewImageRepository.deleteAll(review.getReviewImageEntityList());
        myFileUtils.deleteImage(imageURlList);
    }

    // 리뷰 이미지 대표 여부 설정
    @Override
    public void updateRepresentImage(int reviewImageId, ReviewEntity reviewEntity) {

        List<ReviewImageEntity> reviewImageList = reviewEntity.getReviewImageEntityList();

        for(ReviewImageEntity reviewImage : reviewImageList) {
            reviewImage.setRepresent(reviewImage.getReviewImageId() == reviewImageId);
        }
    }
}
