package com.project.snackpick.repository;

import com.project.snackpick.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Integer> {

    // 리뷰 리스트 1차 조회 - 페이징
    @Query(value = "SELECT r.reviewId FROM ReviewEntity r WHERE r.productEntity.productId = :productId")
    Page<ReviewEntity> findByReviewListByProductId(@Param("productId") int productId, Pageable pageable);

    // 리뷰 리스트 2차 조회 - 리뷰 이미지, 작성자 정보
    @Query("SELECT r FROM ReviewEntity r " +
            "LEFT JOIN FETCH r.memberEntity m " +
            "LEFT JOIN FETCH r.reviewImageEntityList ri " +
            "WHERE r.reviewId IN :reviewIdList " +
            "AND (ri IS NULL OR ri.isRepresent = true)")
    List<ReviewEntity> findReviewListWithImage(@Param("reviewIdList") List<ReviewEntity> reviewIdList);

}
