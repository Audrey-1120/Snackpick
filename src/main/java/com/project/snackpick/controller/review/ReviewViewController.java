package com.project.snackpick.controller.review;

import com.project.snackpick.dto.CategoryDTO;
import com.project.snackpick.dto.ProductDTO;
import com.project.snackpick.service.CategoryService;
import com.project.snackpick.service.ProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/review")
public class ReviewViewController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ReviewViewController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    // 리뷰 작성 페이지 이동
    @GetMapping("/reviewWrite.page")
    @PreAuthorize("isAuthenticated()")
    public String reviewWrite(@RequestParam(required = false, defaultValue = "0") int productId, Model model) {

        // productNo 존재할 경우 product 조회
        if(productId != 0) {
            ProductDTO product = productService.getProductDetail(productId);
            model.addAttribute("product", product);
        }

        List<CategoryDTO> categoryList = categoryService.getAllCategoryList();
        model.addAttribute("categoryList", categoryList);

        return "review/review-write";
    }
}
