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
    private int ReviewImageId;

    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private ReviewEntity reviewEntity;

    @Column(name = "review_image_path")
    private String reviewImagePath;

    // 대표이미지 여부
    @Column(name = "is_represent")
    private boolean isRepresent;

}
