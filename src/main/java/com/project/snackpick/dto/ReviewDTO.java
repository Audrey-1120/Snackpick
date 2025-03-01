package com.project.snackpick.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {

    private int reviewId, memberId, productId;
    private boolean state;
    private double ratingTaste, ratingPrice;
    private LocalDateTime createDt, updateDt;
    private String content, location;

}
