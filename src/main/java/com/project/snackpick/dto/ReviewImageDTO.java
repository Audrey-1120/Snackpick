package com.project.snackpick.dto;

import com.project.snackpick.entity.ReviewImageEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewImageDTO {

    private int reviewId, reviewImageId, representIndex;
    private String reviewImagePath;
    private boolean isRepresent;

    public ReviewImageDTO(ReviewImageEntity reviewImageEntity) {
        this.reviewId = reviewImageEntity.getReviewEntity().getReviewId();
        this.reviewImageId = reviewImageEntity.getReviewImageId();
        this.reviewImagePath = reviewImageEntity.getReviewImagePath();
        this.isRepresent = reviewImageEntity.isRepresent();
    }
}
