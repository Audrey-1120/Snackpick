package com.project.snackpick.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequestDTO {

    private boolean addProduct, deleteAllImageList;
    private int representIndex, representImageId;
    private ReviewDTO reviewDTO;
    private ProductDTO productDTO;

}
