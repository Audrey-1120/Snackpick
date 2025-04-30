package com.project.snackpick.repository;

import com.project.snackpick.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Integer> {

    // 리뷰 목록 조회 - 페이징
    @Query("SELECT r.reviewId FROM ReviewEntity r " +
            "WHERE r.productEntity.productId = :productId " +
            "AND r.state = false")
    Page<ReviewEntity> findByReviewListByProductId(@Param("productId") int productId, Pageable pageable);

    // 리뷰 목록 조회 - 리뷰 이미지, 작성자 정보
    @Query("SELECT DISTINCT r FROM ReviewEntity r " +
            "LEFT JOIN FETCH r.memberEntity m " +
            "LEFT JOIN FETCH r.reviewImageEntityList ri " +
            "WHERE r.reviewId IN :reviewIdList " +
            "AND (ri IS NULL OR ri.isRepresent = true)")
    List<ReviewEntity> findReviewListWithImage(@Param("reviewIdList") List<ReviewEntity> reviewIdList);

    // 회원별 리뷰 목록 조회 - 페이징
    @Query("SELECT r.reviewId FROM ReviewEntity r " +
            "WHERE r.memberEntity.memberId = :memberId " +
            "AND r.state = false")
    Page<ReviewEntity> findByReviewListByMemberId(@Param("memberId") int memberId, Pageable pageable);

    // 회원별 리뷰 목록 전체 조회
    @Query("SELECT r FROM ReviewEntity r " +
            "LEFT JOIN FETCH r.productEntity p " +
            "WHERE r.memberEntity.memberId = :memberId " +
            "AND r.state = false")
    List<ReviewEntity> findAllReviewListByMemberId(@Param("memberId") int memberId);

    // 리뷰 상세 조회
    @Query("SELECT DISTINCT r FROM ReviewEntity r " +
            "LEFT JOIN FETCH r.memberEntity m " +
            "LEFT JOIN FETCH r.reviewImageEntityList ri " +
            "WHERE r.reviewId = :reviewId")
    Optional<ReviewEntity> findReviewByReviewId(@Param("reviewId") int reviewId);

    // 리뷰 목록 삭제
    @Modifying
    @Query("UPDATE ReviewEntity r " +
            "SET r.state = true " +
            "WHERE r.reviewId IN :reviewIdList")
    int deleteAllReviewList(@Param("reviewIdList") List<Integer> reviewIdList);
}