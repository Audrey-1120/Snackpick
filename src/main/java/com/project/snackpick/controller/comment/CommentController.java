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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/comment")
@Tag(name = "Comment", description = "댓글 API")
public class CommentController {

    private final CommentService commentService;

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

    // 댓글 수정
    @PutMapping("/updateComment")
    @Operation(summary = "댓글 수정", description = "댓글 수정")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "403", description = "댓글을 수정할 권한이 없음")
    @ApiResponse(responseCode = "404", description = "수정할 댓글 정보가 없음")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> updateComment(@RequestBody CommentDTO commentDTO,
                                                             @AuthenticationPrincipal CustomUserDetails user) {
        Map<String, Object> response = commentService.updateComment(commentDTO, user);
        return ResponseEntity.ok(response);
    }

    // 댓글 삭제
    @PutMapping("/deleteComment")
    @Operation(summary = "댓글 수정", description = "댓글 수정")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "이미 삭제된 댓글임")
    @ApiResponse(responseCode = "403", description = "댓글을 삭제할 권한이 없음")
    @ApiResponse(responseCode = "404", description = "수정할 댓글 정보가 없음")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> deleteComment(@RequestBody CommentDTO commentDTO,
                                                             @AuthenticationPrincipal CustomUserDetails user) {
        Map<String, Object> response = commentService.deleteComment(commentDTO, user);
        return ResponseEntity.ok(response);
    }
}
