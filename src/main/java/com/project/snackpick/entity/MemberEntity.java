package com.project.snackpick.entity;

import com.project.snackpick.dto.MemberDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "MEMBER_T")
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int memberId;

    @Column(name = "id", unique = true, nullable = false, length = 25)
    private String id;

    @Column(name = "password")
    private String password;

    @Column(name = "name", length = 25)
    private String name;

    @Column(name = "nickname", length = 25)
    private String nickname;

    @Column(name = "profile_image", length = 200)
    private String profileImage;

    @Column(name = "role", length = 20)
    private String role;

    @Column(name = "state", columnDefinition = "TINYINT(1)")
    private boolean state;

    @OneToMany(mappedBy = "memberEntity")
    @BatchSize(size = 10)
    @Builder.Default
    private List<ReviewEntity> reviewList = new ArrayList<>();

    @OneToMany(mappedBy = "memberEntity")
    @BatchSize(size = 10)
    @Builder.Default
    private List<CommentEntity> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "memberEntity")
    @BatchSize(size = 10)
    @Builder.Default
    private List<RecommendEntity> recommendList = new ArrayList<>();

    public static MemberEntity toMemberEntity(MemberDTO memberDTO) {
        return MemberEntity.builder()
                .id(memberDTO.getId())
                .password(memberDTO.getPassword())
                .name(memberDTO.getName())
                .nickname(memberDTO.getNickname())
                .profileImage(memberDTO.getProfileImage())
                .role(memberDTO.getRole())
                .build();
    }

}
