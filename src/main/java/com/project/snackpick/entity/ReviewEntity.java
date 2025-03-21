package com.project.snackpick.entity;

import com.project.snackpick.dto.ReviewDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;

    @CreationTimestamp
    @Column(name = "create_dt", updatable = false)
    private LocalDateTime createDt;

    @UpdateTimestamp
    @Column(name = "update_dt")
    private LocalDateTime updateDt;

    @Column(name = "content", length = 160)
    private String content;

    @Column(name = "rating_taste")
    private double ratingTaste;

    @Column(name = "rating_price")
    private double ratingPrice;

    @Column(name = "location", length = 50)
    private String location;

    @Column(name = "state", columnDefinition = "TINYINT(1)")
    private boolean state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity productEntity;

    @OneToMany(mappedBy = "reviewEntity")
    @BatchSize(size = 10)
    @Builder.Default
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
