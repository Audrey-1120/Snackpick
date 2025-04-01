package com.project.snackpick.service;

import com.project.snackpick.dto.CommentDTO;
import com.project.snackpick.entity.CommentEntity;
import com.project.snackpick.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
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

}
