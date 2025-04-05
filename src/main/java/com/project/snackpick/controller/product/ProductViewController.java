package com.project.snackpick.controller.product;

import com.project.snackpick.dto.ProductDTO;
import com.project.snackpick.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/product")
@Tag(name = "Product-View", description = "제품 화면")
public class ProductViewController {

    private final ProductService productService;

    public ProductViewController(ProductService productService) {
        this.productService = productService;
    }

    // 제품 검색 페이지
    @GetMapping("/product.page")
    @Operation(summary = "제품 검색 페이지 이동", description = "제품 검색 페이지로 이동")
    public String product() {
        return "product/product";
    }

    // 제품 상세 페이지
    @GetMapping("/productDetail.page")
    @Operation(summary = "제품 상세 페이지 이동", description = "제품 상세 페이지로 이동")
    public String productDetail(@RequestParam(defaultValue = "0") int productId, Model model) {

        ProductDTO product = productService.getProductDetail(productId);
        model.addAttribute("product", product);
        return "product/product-details";
    }
}
