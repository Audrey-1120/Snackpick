package com.project.snackpick.dto;

import com.project.snackpick.entity.CommentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {

    private int commentId, depth, groupId, reviewId;
    private LocalDateTime createDt, updateDt;
    private String content;
    private boolean state;
    private MemberDTO member;

    public CommentDTO(CommentEntity commentEntity) {
        this.commentId = commentEntity.getCommentId();
        this.content = commentEntity.getContent();
        this.depth = commentEntity.getDepth();
        this.groupId = commentEntity.getGroupId();
        this.createDt = commentEntity.getCreateDt();
        this.updateDt = commentEntity.getUpdateDt();
        this.state = commentEntity.isState();
        this.member = new MemberDTO(commentEntity.getMemberEntity());
    }

}
