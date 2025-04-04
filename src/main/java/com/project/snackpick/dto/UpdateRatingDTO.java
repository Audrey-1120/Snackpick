package com.project.snackpick.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRatingDTO {

    private double oldRatingTaste;
    private double oldRatingPrice;
    private double newRatingTaste;
    private double newRatingPrice;
    private ReviewAction action;

}
