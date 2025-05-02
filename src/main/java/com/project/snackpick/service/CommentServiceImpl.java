package com.project.snackpick.service;

import com.project.snackpick.dto.CommentDTO;
import com.project.snackpick.dto.CustomUserDetails;
import com.project.snackpick.dto.PageDTO;
import com.project.snackpick.entity.CommentEntity;
import com.project.snackpick.entity.MemberEntity;
import com.project.snackpick.entity.ReviewEntity;
import com.project.snackpick.exception.CustomException;
import com.project.snackpick.exception.ErrorCode;
import com.project.snackpick.repository.CommentRepository;
import com.project.snackpick.repository.MemberRepository;
import com.project.snackpick.repository.ReviewRepository;
import com.project.snackpick.utils.MyPageUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.project.snackpick.dto.CommentDTO.withMember;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final MyPageUtils myPageUtils;

    public CommentServiceImpl(CommentRepository commentRepository, ReviewRepository reviewRepository, MemberRepository memberRepository, MyPageUtils myPageUtils) {
        this.commentRepository = commentRepository;
        this.reviewRepository = reviewRepository;
        this.memberRepository = memberRepository;
        this.myPageUtils = myPageUtils;
    }

    // 댓글 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentList(int reviewId) {

        List<CommentEntity> commentEntity
                = commentRepository.findCommentListByReviewId(reviewId);

        List<CommentDTO> commentList = commentEntity.stream()
                .map(CommentDTO::withMember)
                .toList();

        return commentList;
    }

    // 회원별 댓글 목록 조회
    @Override
    @Transactional(readOnly = true)
    public PageDTO<CommentDTO> getCommentListByMemberId(Pageable initPageable, CustomUserDetails user) {

        int pageNumber = (initPageable.getPageNumber() > 0) ? initPageable.getPageNumber() - 1 : 0;
        Pageable pageable = PageRequest.of(pageNumber, initPageable.getPageSize(),
                Sort.by("groupId").ascending()
                        .and(Sort.by("depth").ascending())
                        .and(Sort.by("createDt").ascending()));

        Page<CommentEntity> commentEntityPage = commentRepository.findCommentListByMemberId(user.getMemberId(), pageable);

        List<CommentDTO> commentDTOList = commentEntityPage
                .stream()
                .map(CommentDTO::withoutMember)
                .toList();

        Page<CommentDTO> commentList = new PageImpl<>(commentDTOList, pageable, commentEntityPage.getTotalElements());
        return myPageUtils.toPageDTO(commentList);
    }

    // 댓글 저장
    @Override
    @Transactional
    public Map<String, Object> insertComment(CommentDTO commentDTO, CustomUserDetails user) {

        ReviewEntity reviewEntity = reviewRepository.findReviewByReviewId(commentDTO.getReviewId())
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_PROCESSING_ERROR));

        MemberEntity memberEntity = memberRepository.findById(user.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_PROCESSING_ERROR));

        CommentEntity comment = CommentEntity.toCommentEntity(commentDTO, reviewEntity, memberEntity);

        commentRepository.save(comment);

        if(commentDTO.getDepth() == 0) {
            comment.setGroupId(comment.getCommentId());
        }

        return Map.of("success", true
                    , "comment", withMember(comment)
                    , "message", "댓글이 작성되었습니다.");
    }

    // 댓글 수정
    @Override
    @Transactional
    public Map<String, Object> updateComment(CommentDTO commentDTO, CustomUserDetails user) {

        CommentEntity comment = commentRepository.findById(commentDTO.getCommentId())
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_PROCESSING_ERROR));

        if(!hasPermission(comment, user)) {
            throw new CustomException(ErrorCode.NOT_PERMISSION,
                    ErrorCode.NOT_PERMISSION.formatMessage("댓글 수정"));
        }

        comment.setContent(commentDTO.getContent());
        comment.setUpdateDt(LocalDateTime.now());

        return Map.of("success", true
                    , "comment", withMember(comment)
                    , "message", "댓글이 수정되었습니다.");
    }

    // 댓글 삭제
    @Override
    @Transactional
    public Map<String, Object> deleteComment(CommentDTO commentDTO, CustomUserDetails user) {

        CommentEntity comment = commentRepository.findById(commentDTO.getCommentId())
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_PROCESSING_ERROR));

        if(!hasPermission(comment, user)) {
            throw new CustomException(ErrorCode.NOT_PERMISSION,
                    ErrorCode.NOT_PERMISSION.formatMessage("댓글 삭제"));
        }

        if(comment.isState()) {
            throw new CustomException(ErrorCode.ALREADY_DELETE,
                    ErrorCode.ALREADY_DELETE.formatMessage("댓글"));
        }

        comment.setState(true);

        return Map.of("success", true
                , "message", "댓글이 삭제되었습니다.");
    }

    // 댓글 일괄 삭제
    @Override
    @Transactional
    public void deleteCommentList(List<Integer> commentIdList) {

        if(commentIdList.isEmpty()) {
            return;
        }

        int commentCount = commentRepository.deleteAllCommentList(commentIdList);
        if(commentCount != commentIdList.size()) {
            throw new CustomException(ErrorCode.COMMENT_PROCESSING_ERROR);
        }
    }

    // 권한 확인
    @Override
    public boolean hasPermission(CommentEntity comment, CustomUserDetails user) {

        int writerId = comment.getMemberEntity().getMemberId();
        int loginId = user.getMemberId();

        return writerId == loginId;
    }
}
