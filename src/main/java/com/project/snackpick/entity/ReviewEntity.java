package com.project.snackpick.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "REVIEW_T")
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reviewId;

    // 작성자
    @ManyToOne
    @JoinColumn(name = "member_id") // 회원 ID FK
    private MemberEntity memberEntity;

    // 작성시간
    @CreationTimestamp
    @Column(name = "create_dt")
    private Timestamp create_dt;

    // 수정시간
    @UpdateTimestamp
    @Column(name = "update_dt")
    private Timestamp update_dt;

    @Column(name = "content")
    private String content;

    @Column(name = "rating_taste")
    private float ratingTaste;

    @Column(name = "rating_price")
    private float ratingPrice;

    @Column(name = "location")
    private String location; // 구매 위치

    // 0: 기존, 1: 삭제
    @Column(name = "state")
    private int state;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity productEntity;

    // 리뷰 사진과 1:N 관계
    // 리뷰 사진은 조회할 때 JOIN FETCH 사용하기
    @OneToMany(mappedBy = "reviewEntity")
    private List<ReviewImageEntity> reviewImageEntityList = new ArrayList<>();

}
