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

    private boolean addProduct;
    private boolean deleteAllImageList;
    private int representIndex;
    private int representImageId;
    private ReviewDTO reviewDTO;
    private ProductDTO productDTO;

}
