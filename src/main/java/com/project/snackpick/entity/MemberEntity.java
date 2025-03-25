package com.project.snackpick.entity;

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

    @OneToMany(mappedBy = "memberEntity")
    @BatchSize(size = 10)
    @Builder.Default
    private List<ReviewEntity> reviewList = new ArrayList<>();

    @OneToMany(mappedBy = "memberEntity")
    @BatchSize(size = 10)
    @Builder.Default
    private List<RecommendEntity> recommendList = new ArrayList<>();

}
