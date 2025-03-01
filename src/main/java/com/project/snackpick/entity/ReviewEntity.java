package com.project.snackpick.entity;

import com.project.snackpick.dto.ReviewDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
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
    private LocalDateTime create_dt;

    // 수정시간
    @UpdateTimestamp
    @Column(name = "update_dt")
    private LocalDateTime update_dt;

    // 내용
    @Column(name = "content")
    private String content;

    // 맛 별점
    @Column(name = "rating_taste")
    private double ratingTaste;

    // 가격 별점
    @Column(name = "rating_price")
    private double ratingPrice;

    // 구매 위치
    @Column(name = "location")
    private String location;

    // 삭제 상태(0: 삭제 안됨, 1: 삭제됨)
    @Column(name = "state", columnDefinition = "TINYINT(1)")
    private boolean state;

    // 제품과 리뷰는 1:N 관계
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity productEntity;

    // 리뷰 사진과 1:N 관계
    // 리뷰 사진은 조회할 때 JOIN FETCH 사용하기
    @OneToMany(mappedBy = "reviewEntity")
    private List<ReviewImageEntity> reviewImageEntityList = new ArrayList<>();

    public static ReviewEntity toReviewEntity(ReviewDTO reviewDTO, MemberEntity memberEntity, ProductEntity productEntity) {
        return ReviewEntity.builder()
                .memberEntity(memberEntity)
                .content(reviewDTO.getContent())
                .ratingTaste(reviewDTO.getRatingTaste())
                .ratingPrice(reviewDTO.getRatingPrice())
                .location(reviewDTO.getLocation())
                .state(reviewDTO.isState())
                .productEntity(productEntity)
                .build();

    }
}
