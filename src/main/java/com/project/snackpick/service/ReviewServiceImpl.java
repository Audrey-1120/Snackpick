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

        ProductEntity product = handleProduct(reviewRequestDTO);
        ReviewEntity review = insertReviewFields(reviewRequestDTO, product, user);

        updateProductStats(product, review, new ReviewDTO(), ReviewAction.INSERT);

        try {
            insertReviewImage(files, review, reviewRequestDTO.getRepresentIndex());
        } catch(IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAIL,
                    ErrorCode.FILE_UPLOAD_FAIL.formatMessage("프로필"));
        }

        return Map.of("success", true
                , "message", "리뷰가 작성되었습니다."
                , "redirectUrl", "/product/productDetail.page?productId=" + product.getProductId());
    }

    // 리뷰 목록 조회
    @Override
    @Transactional(readOnly = true)
    public PageDTO<ReviewDTO> getReviewList(Pageable initPageable, int productId) {

        int pageNumber = (initPageable.getPageNumber() > 0) ? initPageable.getPageNumber() - 1: 0;
        Sort sort = initPageable.getSort().and(Sort.by(Sort.Order.asc("reviewId")));

        Pageable pageable = PageRequest.of(pageNumber, initPageable.getPageSize(), sort);

        Page<ReviewEntity> reviewIdList = reviewRepository.findByReviewListByProductId(productId, pageable);

        List<ReviewDTO> reviewDTOList = reviewRepository.findReviewListWithImage(reviewIdList.getContent())
                .stream()
                .map(ReviewDTO::new)
                .toList();

        Page<ReviewDTO> reviewList = new PageImpl<>(reviewDTOList, pageable, reviewIdList.getTotalElements());
        return myPageUtils.toPageDTO(reviewList);
    }

    // 회원별 리뷰 목록 조회
    @Override
    @Transactional(readOnly = true)
    public PageDTO<ReviewDTO> getReviewListByMemberId(Pageable initPageable, CustomUserDetails user) {

        int pageNumber = (initPageable.getPageNumber() > 0) ? initPageable.getPageNumber() - 1 : 0;

        Pageable pageable = PageRequest.of(pageNumber, initPageable.getPageSize(), initPageable.getSort());
        Page<ReviewEntity> reviewIdList = reviewRepository.findByReviewListByMemberId(user.getMemberId(), pageable);

        List<ReviewDTO> reviewDTOList = reviewRepository.findReviewListWithImage(reviewIdList.getContent())
                .stream()
                .map(ReviewDTO::new)
                .toList();

        Page<ReviewDTO> reviewList = new PageImpl<>(reviewDTOList, pageable, reviewIdList.getTotalElements());
        return myPageUtils.toPageDTO(reviewList);
    }

    // 리뷰 상세 조회
    @Override
    @Transactional(readOnly = true)
    public ReviewDTO getReviewDetail(int reviewId) {

        ReviewEntity review = reviewRepository.findReviewByReviewId(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY,
                        ErrorCode.NOT_FOUND_ENTITY.formatMessage("리뷰")));

        return new ReviewDTO(review);
    }

    // 리뷰 단일 삭제
    @Override
    @Transactional
    public Map<String, Object> deleteReview(int reviewId, CustomUserDetails user) {

        ReviewEntity review = reviewRepository.findReviewByReviewId(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY,
                        ErrorCode.NOT_FOUND_ENTITY.formatMessage("리뷰")));

        if(!hasPermission(review, user)) {
            throw new CustomException(ErrorCode.NOT_PERMISSION,
                    ErrorCode.NOT_PERMISSION.formatMessage("리뷰 삭제"));
        }

        if(review.isState()) {
            throw new CustomException(ErrorCode.ALREADY_DELETE,
                    ErrorCode.ALREADY_DELETE.formatMessage("리뷰"));
        }

        review.setState(true);

        ProductEntity product = productRepository.findProductByProductId(review.getProductEntity().getProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY,
                        ErrorCode.NOT_FOUND_ENTITY.formatMessage("제품")));

        updateProductStats(product, review, new ReviewDTO(), ReviewAction.DELETE);

        return Map.of("success", true
                , "message", "리뷰가 삭제되었습니다.");
    }

    // 리뷰 목록 삭제
    @Override
    @Transactional
    public void deleteReviewList(List<ReviewEntity> reviewList) {

        List<Integer> reviewIdList = reviewList.stream()
                .map(ReviewEntity::getReviewId)
                .toList();

        int reviewCount = reviewRepository.deleteAllReviewList(reviewIdList);
        if(reviewCount != reviewIdList.size()) {
            throw new CustomException(ErrorCode.SERVER_ERROR,
                    ErrorCode.SERVER_ERROR.formatMessage("회원 탈퇴"));
        }
    }

    // 리뷰 수정
    @Override
    @Transactional
    public Map<String, Object> updateReview(ReviewRequestDTO reviewRequestDTO, MultipartFile[] files, CustomUserDetails user) {

        ReviewDTO reviewDTO = reviewRequestDTO.getReviewDTO();
        ReviewEntity review = reviewRepository.findReviewByReviewId(reviewDTO.getReviewId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY,
                        ErrorCode.NOT_FOUND_ENTITY.formatMessage("리뷰")));

        ProductEntity product = productRepository.findProductByProductId(reviewDTO.getProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY,
                        ErrorCode.NOT_FOUND_ENTITY.formatMessage("제품")));

        if(!hasPermission(review, user)) {
            throw new CustomException(ErrorCode.NOT_PERMISSION,
                    ErrorCode.NOT_PERMISSION.formatMessage("리뷰 수정"));
        }

        updateProductStats(product, review, reviewDTO, ReviewAction.UPDATE);
        updateReviewFields(review, reviewDTO);

        try {
            updateReviewImage(reviewRequestDTO, files, review);
        } catch(IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAIL,
                    ErrorCode.FILE_UPLOAD_FAIL.formatMessage("리뷰"));
        }

        return Map.of("success", true
                , "message", "리뷰가 수정되었습니다."
                , "redirectUrl", "/product/productDetail.page?productId=" + product.getProductId());
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

    // 리뷰 이미지 대표 여부 설정
    @Override
    public void updateRepresentImage(int reviewImageId, ReviewEntity reviewEntity) {

        List<ReviewImageEntity> reviewImageList = reviewEntity.getReviewImageEntityList();

        for(ReviewImageEntity reviewImage : reviewImageList) {
            reviewImage.setRepresent(reviewImage.getReviewImageId() == reviewImageId);
        }
    }

    // 제품 평점 및 리뷰 개수 업데이트
    @Override
    public void updateProductStats(ProductEntity product, ReviewEntity review, ReviewDTO reviewDTO, ReviewAction action) {

        UpdateRatingDTO updateRating;

        switch (action) {
            case INSERT:
                updateRating = UpdateRatingDTO.builder()
                        .newRatingTaste(review.getRatingTaste())
                        .newRatingPrice(review.getRatingPrice())
                        .action(action)
                        .build();
                productService.updateProductRating(updateRating, product);
                break;

            case UPDATE:
                updateRating = UpdateRatingDTO.builder()
                        .oldRatingTaste(review.getRatingTaste())
                        .oldRatingPrice(review.getRatingPrice())
                        .newRatingTaste(reviewDTO.getRatingTaste())
                        .newRatingPrice(reviewDTO.getRatingPrice())
                        .action(action)
                        .build();
                productService.updateProductRating(updateRating, product);
                break;

            case DELETE:
                updateRating = UpdateRatingDTO.builder()
                        .oldRatingTaste(review.getRatingTaste())
                        .oldRatingPrice(review.getRatingPrice())
                        .action(action)
                        .build();
                productService.updateProductRating(updateRating, product);
                break;
        }
    }

    // 권한 확인
    @Override
    public boolean hasPermission(ReviewEntity review, CustomUserDetails user) {

        int writerId = review.getMemberEntity().getMemberId();
        int loginId = user.getMemberId();

        return writerId == loginId;
    }

    // 리뷰 데이터 저장
    @Override
    public ReviewEntity insertReviewFields(ReviewRequestDTO reviewRequestDTO, ProductEntity product, CustomUserDetails user) {

        MemberEntity member = memberRepository.findById(user.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY,
                        ErrorCode.NOT_FOUND_ENTITY.formatMessage("회원")));

        ReviewEntity review = ReviewEntity.toReviewEntity(reviewRequestDTO.getReviewDTO(), member, product);
        return reviewRepository.save(review);
    }

    // 리뷰 데이터 수정
    @Override
    public void updateReviewFields(ReviewEntity review, ReviewDTO reviewDTO) {

        review.setRatingTaste(reviewDTO.getRatingTaste());
        review.setRatingPrice(reviewDTO.getRatingPrice());
        review.setContent(reviewDTO.getContent());
        review.setLocation(reviewDTO.getLocation());
        review.setUpdateDt(LocalDateTime.now());
    }

    // 제품 처리
    @Override
    public ProductEntity handleProduct(ReviewRequestDTO reviewRequestDTO) {

        if(reviewRequestDTO.isAddProduct()) {
            return productService.insertProduct(reviewRequestDTO.getProductDTO());
        } else {
            int productId = reviewRequestDTO.getReviewDTO().getProductId();

            return productRepository.findProductByProductId(productId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY,
                            ErrorCode.NOT_FOUND_ENTITY.formatMessage("제품")));
        }
    }

}