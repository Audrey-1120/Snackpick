package com.project.snackpick.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "REVIEW_IMAGE_T")
public class ReviewImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reviewImageId;

    // 리뷰와 리뷰이미지는 1:N 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private ReviewEntity reviewEntity;

    // 리뷰 이미지 저장 경로
    @Column(name = "review_image_path")
    private String reviewImagePath;

    // 대표이미지 여부 0: 대표 이미지 X, 1: 대표 이미지)
    @Column(name = "is_represent", columnDefinition = "TINYINT(1)")
    private boolean isRepresent;

    public ReviewImageEntity toReviewImageEntity(ReviewEntity reviewEntity, String reviewImagePath, boolean isRepresent) {
        return ReviewImageEntity.builder()
                .reviewEntity(reviewEntity)
                .reviewImagePath(reviewImagePath)
                .isRepresent(isRepresent)
                .build();
    }

}
