package com.project.snackpick.service;

import com.project.snackpick.dto.CommentDTO;

import java.util.List;

public interface CommentService {

    // 댓글 목록 조회
    List<CommentDTO> getCommentList(int reviewId);
}
