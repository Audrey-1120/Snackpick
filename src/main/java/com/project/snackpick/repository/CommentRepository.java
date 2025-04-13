package com.project.snackpick.repository;

import com.project.snackpick.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {

    // 댓글 목록 조회
    @Query(value = "SELECT c FROM CommentEntity c " +
            "JOIN FETCH c.memberEntity " +
            "WHERE c.reviewEntity.reviewId = :reviewId " +
            "AND c.state = false " +
            "ORDER BY c.groupId ASC, c.depth ASC, c.createDt ASC")
    List<CommentEntity> findCommentListByReviewId(@Param("reviewId") int reviewId);

}
