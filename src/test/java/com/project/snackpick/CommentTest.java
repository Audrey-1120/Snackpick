package com.project.snackpick;

import com.project.snackpick.dto.CommentDTO;
import com.project.snackpick.dto.CustomUserDetails;
import com.project.snackpick.entity.MemberEntity;
import com.project.snackpick.service.CommentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@DisplayName("댓글 테스트")
public class CommentTest {

    private final CommentService commentService;

    @Autowired
    public CommentTest(CommentService commentService) {
        this.commentService = commentService;
    }

    @BeforeEach
    public void before() {
        System.out.println("Test Before");
    }

    @AfterEach
    public void after() {
        System.out.println("Test After");
    }

    @Test
    @DisplayName("댓글 작성")
    public void insertComment() {

        MemberEntity member = MemberEntity.builder()
                .memberId(1)
                .id("mintii92")
                .build();

        CustomUserDetails user = new CustomUserDetails(member);

        CommentDTO commentDTO = CommentDTO.builder()
                .reviewId(8)
                .content("테스트")
                .depth(1)
                .groupId(14)
                .state(false)
                .build();

        Map<String, Object> map = commentService.insertComment(commentDTO, user);

        System.out.println("완료 메시지: " + String.valueOf(map.get("message")));
        assertTrue((Boolean)map.get("success"));
    }

    @Test
    @DisplayName("댓글 수정")
    public void updateComment() {

        MemberEntity member = MemberEntity.builder()
                .memberId(1)
                .id("mintii92")
                .build();

        CustomUserDetails user = new CustomUserDetails(member);

        CommentDTO commentDTO = CommentDTO.builder()
                .commentId(3)
                .content("테스트")
                .build();

        Map<String, Object> map = commentService.updateComment(commentDTO, user);

        System.out.println("완료 메시지: " + String.valueOf(map.get("message")));
        assertTrue((Boolean)map.get("success"));
    }

    @Test
    @DisplayName("댓글 삭제")
    public void deleteComment() {

        MemberEntity member = MemberEntity.builder()
                .memberId(1)
                .id("mintii92")
                .build();

        CustomUserDetails user = new CustomUserDetails(member);

        CommentDTO commentDTO = CommentDTO.builder()
                .commentId(3)
                .build();

        Map<String, Object> map = commentService.deleteComment(commentDTO, user);

        System.out.println("완료 메시지: " + String.valueOf(map.get("message")));
        assertTrue((Boolean)map.get("success"));

    }

    @Test
    @DisplayName("댓글 목록 조회")
    public void getCommentList() {

        int reviewId = 15;

        List<CommentDTO> commentList = commentService.getCommentList(reviewId);
        assertNotNull(commentList);

        for(CommentDTO comment : commentList) {
            System.out.println("댓글 내용: " + comment.getContent());
        }
    }
}