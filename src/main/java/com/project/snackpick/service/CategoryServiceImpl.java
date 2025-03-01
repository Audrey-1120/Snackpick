package com.project.snackpick.service;

import com.project.snackpick.dto.CategoryDTO;
import com.project.snackpick.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryDTO> getAllCategoryList() {
        List<CategoryDTO> categoryList = categoryRepository.findAllCategoryList().stream()
                .map(CategoryDTO::new)
                .collect(Collectors.toList());

        return categoryList;
    }
}
