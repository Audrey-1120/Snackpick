package com.project.snackpick.dto;

import com.project.snackpick.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {

    private String id, password, name, nickname, profileImage, role;
    private int memberId;
    private long reviewCount, commentCount;

    public MemberDTO(MemberEntity memberEntity) {
        this.memberId = memberEntity.getMemberId();
        this.id = memberEntity.getId();
        this.nickname = memberEntity.getNickname();
        this.profileImage = memberEntity.getProfileImage();
        this.role = memberEntity.getRole();
    }

    public MemberDTO(MemberEntity memberEntity, long reviewCount, long commentCount) {
        this.memberId = memberEntity.getMemberId();
        this.id = memberEntity.getId();
        this.nickname = memberEntity.getNickname();
        this.profileImage = memberEntity.getProfileImage();
        this.role = memberEntity.getRole();
        this.reviewCount = reviewCount;
        this.commentCount = commentCount;
    }
}
