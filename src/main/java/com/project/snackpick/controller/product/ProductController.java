package com.project.snackpick.controller.product;

import com.project.snackpick.dto.ProductDTO;
import com.project.snackpick.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/product")
@Tag(name = "Product", description = "제품 API")
public class ProductController {

    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 제품 검색
    @GetMapping("/searchProduct")
    @Operation(summary = "제품 검색", description = "제품을 이름으로 검색")
    @ApiResponse(responseCode = "200", description = "성공")
    public ResponseEntity<Map<String, Object>> searchProduct(@RequestParam String searchKeyword) {
        List<ProductDTO> productList = productService.searchProduct(searchKeyword);
        return ResponseEntity.ok(Map.of("productList", productList));
    }

}
