package com.project.snackpick.controller.comment;

import com.project.snackpick.dto.CommentDTO;
import com.project.snackpick.dto.CustomUserDetails;
import com.project.snackpick.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/comment")
@Tag(name = "Comment", description = "댓글 API")
public class CommentController {

    private CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // 댓글 작성
    @PostMapping("/insertComment")
    @Operation(summary = "댓글 작성", description = "댓글 작성")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "404", description = "기존 리뷰 정보 혹은 로그인한 멤버 정보가 없음")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류로 댓글 저장 실패")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> insertComment(@RequestBody CommentDTO commentDTO,
                                                             @AuthenticationPrincipal CustomUserDetails user) {

        Map<String, Object> response = commentService.insertComment(commentDTO, user);
        return ResponseEntity.ok(response);
    }
}
