package com.project.snackpick.service;

import com.project.snackpick.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {

    // 카테고리 목록 조회
    List<CategoryDTO> getAllCategoryList();
}
