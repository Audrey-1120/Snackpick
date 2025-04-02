package com.project.snackpick.service;

import com.project.snackpick.dto.CommentDTO;
import com.project.snackpick.dto.CustomUserDetails;
import com.project.snackpick.entity.CommentEntity;
import com.project.snackpick.entity.MemberEntity;
import com.project.snackpick.entity.ReviewEntity;
import com.project.snackpick.exception.CustomException;
import com.project.snackpick.exception.ErrorCode;
import com.project.snackpick.repository.CommentRepository;
import com.project.snackpick.repository.MemberRepository;
import com.project.snackpick.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {

    private CommentRepository commentRepository;
    private ReviewRepository reviewRepository;
    private MemberRepository memberRepository;

    public CommentServiceImpl(CommentRepository commentRepository, ReviewRepository reviewRepository, MemberRepository memberRepository) {
        this.commentRepository = commentRepository;
        this.reviewRepository = reviewRepository;
        this.memberRepository = memberRepository;
    }

    // 댓글 목록 조회
    @Override
    public List<CommentDTO> getCommentList(int reviewId) {

        List<CommentEntity> commentEntity
                = commentRepository.findCommentListByReviewId(reviewId);

        List<CommentDTO> commentList = commentEntity.stream()
                .map(CommentDTO::new)
                .toList();

        return commentList;
    }

    // 댓글 저장
    @Override
    @Transactional
    public Map<String, Object> insertComment(CommentDTO commentDTO, CustomUserDetails user) {

        ReviewEntity reviewEntity = reviewRepository.findReviewByReviewId(commentDTO.getReviewId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

        MemberEntity memberEntity = memberRepository.findById(user.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

        CommentEntity commentEntity = CommentEntity.toCommentEntity(commentDTO, reviewEntity, memberEntity);

        commentRepository.save(commentEntity);

        if(commentDTO.getDepth() == 0) {
            commentEntity.setGroupId(commentEntity.getCommentId());
        }

        return Map.of("success", true
                    , "comment", new CommentDTO(commentEntity)
                    , "message", "댓글이 작성되었습니다.");
    }
}
