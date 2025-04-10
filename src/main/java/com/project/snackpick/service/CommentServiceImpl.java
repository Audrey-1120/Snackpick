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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;

    public CommentServiceImpl(CommentRepository commentRepository, ReviewRepository reviewRepository, MemberRepository memberRepository) {
        this.commentRepository = commentRepository;
        this.reviewRepository = reviewRepository;
        this.memberRepository = memberRepository;
    }

    // 댓글 목록 조회
    @Override
    @Transactional(readOnly = true)
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
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY,
                        ErrorCode.NOT_FOUND_ENTITY.formatMessage("리뷰")));

        MemberEntity memberEntity = memberRepository.findById(user.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY,
                        ErrorCode.NOT_FOUND_ENTITY.formatMessage("회원")));

        CommentEntity comment = CommentEntity.toCommentEntity(commentDTO, reviewEntity, memberEntity);

        commentRepository.save(comment);

        if(commentDTO.getDepth() == 0) {
            comment.setGroupId(comment.getCommentId());
        }

        return Map.of("success", true
                    , "comment", new CommentDTO(comment)
                    , "message", "댓글이 작성되었습니다.");
    }

    // 댓글 수정
    @Override
    @Transactional
    public Map<String, Object> updateComment(CommentDTO commentDTO, CustomUserDetails user) {

        CommentEntity comment = commentRepository.findById(commentDTO.getCommentId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY,
                        ErrorCode.NOT_FOUND_ENTITY.formatMessage("댓글")));

        if(!hasPermission(comment, user)) {
            throw new CustomException(ErrorCode.NOT_PERMISSION,
                    ErrorCode.NOT_PERMISSION.formatMessage("댓글 수정"));
        }

        comment.setContent(commentDTO.getContent());
        comment.setUpdateDt(LocalDateTime.now());

        return Map.of("success", true
                    , "comment", new CommentDTO(comment)
                    , "message", "댓글이 수정되었습니다.");
    }

    // 권한 확인
    @Override
    public boolean hasPermission(CommentEntity comment, CustomUserDetails user) {

        int writerId = comment.getMemberEntity().getMemberId();
        int loginId = user.getMemberId();

        return writerId == loginId;
    }
}
