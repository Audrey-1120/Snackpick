package com.project.snackpick.controller.product;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/product")
public class ProductViewController {

    // 상품 검색 페이지
    @GetMapping("/product.page")
    public String product() {
        return "product/product";
    }

}
