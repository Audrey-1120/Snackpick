package com.project.snackpick.entity;

import com.project.snackpick.dto.CommentDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "COMMENT_T")
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int commentId;

    @CreationTimestamp
    @Column(name = "create_dt", updatable = false)
    private LocalDateTime createDt;

    @UpdateTimestamp
    @Column(name = "update_dt")
    private LocalDateTime updateDt;

    @Column(name = "content", length = 150)
    private String content;

    @Column(name = "state", columnDefinition = "TINYINT(1)")
    private boolean state;

    @Column(name = "depth")
    private int depth;

    @Column(name = "group_id")
    private int groupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private ReviewEntity reviewEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity memberEntity;

    public static CommentEntity toCommentEntity(CommentDTO commentDTO, ReviewEntity reviewEntity, MemberEntity memberEntity) {
        return CommentEntity.builder()
                .content(commentDTO.getContent())
                .state(commentDTO.isState())
                .depth(commentDTO.getDepth())
                .groupId(commentDTO.getGroupId())
                .reviewEntity(reviewEntity)
                .memberEntity(memberEntity)
                .build();
    }

}
