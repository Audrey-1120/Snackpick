package com.project.snackpick;

import com.project.snackpick.dto.CategoryDTO;
import com.project.snackpick.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
public class CategoryTest {

    private final CategoryService categoryService;

    @Autowired
    public CategoryTest(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Test
    @DisplayName("카테고리 전체 조회")
    public void getAllCategoryList() throws Exception {

        List<CategoryDTO> categoryList = categoryService.getAllCategoryList();
        System.out.println("================================");

        for(CategoryDTO category : categoryList) {
            System.out.println(category.getCategoryId());
            System.out.println(category.getCategoryName());
        }
        System.out.println("================================");

        assertNotNull(categoryList);

    }
}
