package com.project.snackpick.service;

import com.project.snackpick.dto.CategoryDTO;
import com.project.snackpick.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // 카테고리 목록 조회
    @Override
    public List<CategoryDTO> getAllCategoryList() {
        List<CategoryDTO> categoryList = categoryRepository.findAllCategoryList().stream()
                .map(CategoryDTO::new)
                .toList();

        return categoryList;
    }
}
