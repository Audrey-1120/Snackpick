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

    public MemberEntity toMemberEntity() {
        return MemberEntity.builder()
                .id(id)
                .password(password)
                .name(name)
                .nickname(nickname)
                .profileImage(profileImage)
                .role(role)
                .build();
    }

    public MemberDTO(MemberEntity memberEntity) {
        this.memberId = memberEntity.getMemberId();
        this.id = memberEntity.getId();
        this.nickname = memberEntity.getNickname();
        this.profileImage = memberEntity.getProfileImage();
        this.role = memberEntity.getRole();
    }

}
