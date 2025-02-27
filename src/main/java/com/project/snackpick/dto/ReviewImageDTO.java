package com.project.snackpick.dto;

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

}
