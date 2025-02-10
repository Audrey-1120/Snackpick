package com.project.snackpick.dto;

import com.project.snackpick.entity.MemberEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberDTO {

    private String memberId, id, password, name, nickname, profileImage, role;

    public MemberEntity toMemberEntity() {
        return MemberEntity.builder()
                .id(id)
                .password(password)
                .name(name)
                .nickname(nickname)
                .profileImage(profileImage)
                .build();
    }

}
