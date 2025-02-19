package com.project.snackpick.entity;

import jakarta.persistence.*;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "MEMBER_T")
public class MemberEntity { // 회원

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int memberId;

    @Column(name = "id", unique = true, nullable = false, length = 25)
    private String id;

    @Column(name = "password")
    private String password;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "nickname", length = 20)
    private String nickname;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "role")
    private String role;
}
