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

    private CommentDTO(CommentEntity commentEntity, boolean includeMember) {
        this.commentId = commentEntity.getCommentId();
        this.groupId = commentEntity.getGroupId();
        this.depth = commentEntity.getDepth();
        this.content = commentEntity.getContent();
        this.createDt = commentEntity.getCreateDt();

        if(includeMember) {
            this.member = new MemberDTO(commentEntity.getMemberEntity());
        }
    }

    public static CommentDTO withMember(CommentEntity commentEntity) {
        return new CommentDTO(commentEntity, true);
    }

    public static CommentDTO withoutMember(CommentEntity commentEntity) {
        return new CommentDTO(commentEntity, false);
    }
}
