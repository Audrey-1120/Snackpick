package com.project.snackpick.controller.product;

import com.project.snackpick.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("/product")
@Tag(name = "Product", description = "상품 API")
public class ProductController {

    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 상품 검색
    @GetMapping("/searchProduct")
    @Operation(summary = "상품 검색", description = "상품을 이름으로 검색")
    public ResponseEntity<Map<String, Object>> searchProduct(@RequestParam String searchKeyword) {
        Map<String, Object> response = productService.searchProduct(searchKeyword);
        return ResponseEntity.ok(response);
    }

}
