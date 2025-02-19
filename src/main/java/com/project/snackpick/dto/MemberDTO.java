package com.project.snackpick.dto;

import com.project.snackpick.entity.MemberEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
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

}
