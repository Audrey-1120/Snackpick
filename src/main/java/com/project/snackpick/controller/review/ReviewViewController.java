package com.project.snackpick.controller.review;

import com.project.snackpick.dto.CategoryDTO;
import com.project.snackpick.dto.ProductDTO;
import com.project.snackpick.dto.ReviewDTO;
import com.project.snackpick.service.CategoryService;
import com.project.snackpick.service.ProductService;
import com.project.snackpick.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/review")
@Tag(name = "Review-View", description = "리뷰 화면")
public class ReviewViewController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ReviewService reviewService;

    public ReviewViewController(ProductService productService, CategoryService categoryService, ReviewService reviewService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.reviewService = reviewService;
    }

    // 리뷰 작성 페이지 이동
    @GetMapping("/reviewWrite.page")
    @Operation(summary = "리뷰 작성 페이지 이동", description = "리뷰 작성 페이지로 이동")
    @PreAuthorize("isAuthenticated()")
    public String reviewWrite(@RequestParam(required = false, defaultValue = "0") int productId, Model model) {

        if(productId != 0) {
            ProductDTO product = productService.getProductDetail(productId);
            model.addAttribute("product", product);
        }

        List<CategoryDTO> categoryList = categoryService.getAllCategoryList();
        model.addAttribute("categoryList", categoryList);
        return "review/review-write";
    }

    // 리뷰 수정 페이지로 이동
    @GetMapping("/reviewUpdate.page")
    @Operation(summary = "리뷰 수정 페이지 이동", description = "리뷰 수정 페이지로 이동")
    @PreAuthorize("isAuthenticated()")
    public String reviewUpdate(@RequestParam int reviewId,
                               @RequestParam int productId,
                               Model model) {

        ProductDTO product = productService.getProductDetail(productId);
        ReviewDTO review = reviewService.getReviewDetail(reviewId);

        model.addAttribute("review", review);
        model.addAttribute("product", product);
        return "review/review-update";

    }
}