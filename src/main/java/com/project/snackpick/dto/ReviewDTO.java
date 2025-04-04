package com.project.snackpick.dto;

import com.project.snackpick.entity.ReviewEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {

    private int reviewId, productId;
    private boolean state;
    private double ratingTaste, ratingPrice;
    private LocalDateTime createDt, updateDt;
    private String content, location;
    private List<ReviewImageDTO> reviewImageList;
    private MemberDTO member;

    public ReviewDTO(ReviewEntity reviewEntity) {
        this.reviewId = reviewEntity.getReviewId();
        this.createDt = reviewEntity.getCreateDt();
        this.updateDt = reviewEntity.getUpdateDt();
        this.content = reviewEntity.getContent();
        this.ratingTaste = reviewEntity.getRatingTaste();
        this.ratingPrice = reviewEntity.getRatingPrice();
        this.location = reviewEntity.getLocation();
        this.state = reviewEntity.isState();
        this.productId = reviewEntity.getProductEntity().getProductId();

        this.reviewImageList = reviewEntity.getReviewImageEntityList().stream()
                .map(ReviewImageDTO::new)
                .toList();
        this.member = new MemberDTO(reviewEntity.getMemberEntity());

    }
}
