package com.project.snackpick.controller.product;

import com.project.snackpick.dto.ProductDTO;
import com.project.snackpick.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/product")
public class ProductViewController {

    private final ProductService productService;

    public ProductViewController(ProductService productService) {
        this.productService = productService;
    }

    // 상품 검색 페이지
    @GetMapping("/product.page")
    public String product() {
        return "product/product";
    }

    // 상품 상세 페이지
    @GetMapping("/productDetail.page")
    public String productDetail(@RequestParam(defaultValue = "0") int productId, Model model) {
        ProductDTO product = productService.getProductDetail(productId);
        model.addAttribute("product", product);
        return "product/product-details";
    }
}
