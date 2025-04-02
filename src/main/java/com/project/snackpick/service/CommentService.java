package com.project.snackpick.service;

import com.project.snackpick.dto.CommentDTO;
import com.project.snackpick.dto.CustomUserDetails;

import java.util.List;
import java.util.Map;

public interface CommentService {

    // 댓글 목록 조회
    List<CommentDTO> getCommentList(int reviewId);

    // 댓글 작성
    Map<String, Object> insertComment(CommentDTO commentDTO, CustomUserDetails user);
}
