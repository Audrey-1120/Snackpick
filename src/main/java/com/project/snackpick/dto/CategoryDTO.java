package com.project.snackpick.dto;

import com.project.snackpick.entity.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
public class CategoryDTO {

    private int categoryId, topCategoryId;
    private String categoryName;
    private List<CategoryDTO> subCategoryList; // 중분류 리스트

    public CategoryDTO(CategoryEntity categoryEntity) {
        this.categoryId = categoryEntity.getCategoryId();
        this.categoryName = categoryEntity.getCategoryName();
        this.topCategoryId = (categoryEntity.getTopCategory() != null)
                                ? categoryEntity.getTopCategory().getCategoryId()
                                : 0;

        // 중분류 리스트도 DTO로 변환한다.
        this.subCategoryList = categoryEntity.getSubCategoryList().stream()
                .map(CategoryDTO::new)
                .collect(Collectors.toList());
    }

}
