package com.project.snackpick.repository;

import com.project.snackpick.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {

    // 단일 리뷰별 댓글 목록 조회 - 페이징
    @Query("SELECT c FROM CommentEntity c " +
            "JOIN FETCH c.memberEntity " +
            "WHERE c.reviewEntity.reviewId = :reviewId " +
            "AND c.state = false " +
            "ORDER BY c.groupId ASC, c.depth ASC, c.createDt ASC")
    List<CommentEntity> findCommentListByReviewId(@Param("reviewId") int reviewId);

    // 단일 리뷰별 댓글 목록 전체 조회
    @Query("SELECT c.commentId FROM CommentEntity c " +
            "WHERE c.reviewEntity.reviewId = :reviewId " +
            "AND c.state = false ")
    List<Integer> findAllCommentIdListByReviewId(@Param("reviewId") int reviewId);

    // 리뷰 목록으로 댓글 전체 조회
    @Query("SELECT c.commentId FROM CommentEntity c " +
            "WHERE c.reviewEntity.reviewId IN :reviewIdList " +
            "AND c.state = false")
    List<Integer> findAllCommentIdListByReviewIdList(@Param("reviewIdList") List<Integer> reviewIdList);

    // 회원별 댓글 목록 조회
    @Query("SELECT c FROM CommentEntity c " +
            "WHERE c.memberEntity.memberId = :memberId " +
            "AND c.state = false ")
    Page<CommentEntity> findCommentListByMemberId(@Param("memberId") int memberId, Pageable pageable);

    // 회원별 댓글 목록 전체 조회
    @Query("SELECT c.commentId FROM CommentEntity c " +
            "WHERE c.memberEntity.memberId = :memberId " +
            "AND c.state = false ")
    List<Integer> findAllCommentIdListByMemberId(@Param("memberId") int memberId);

    // 댓글 목록 삭제
    @Modifying
    @Query("UPDATE CommentEntity c " +
            "SET c.state = true " +
            "WHERE c.commentId IN :commentIdList")
    int deleteAllCommentList(@Param("commentIdList") List<Integer> commentIdList);
}
