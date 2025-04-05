package com.project.snackpick;

import com.project.snackpick.dto.ProductDTO;
import com.project.snackpick.service.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
public class ProductTest {

    private final ProductService productService;

    @Autowired
    public ProductTest(ProductService productService) {
        this.productService = productService;
    }

    @BeforeEach
    public void before() {
        System.out.println("Test Before");
    }

    @AfterEach
    public void after() {
        System.out.println("Test After");
    }

    @Test
    @DisplayName("제품ID로 제품 상세 데이터 조회")
    public void getProductDetail() throws Exception {
        // Given
        int productId = 1;

        // When
        ProductDTO product = productService.getProductDetail(productId);
        System.out.println("================================");
        System.out.println(product);
        System.out.println("================================");

        // Then
        assertNotNull(product);
    }

    @Test
    @DisplayName("제품 이름으로 제품 리스트 검색")
    public void searchProductList() throws Exception {

        // Given
        String productName = "새우";

        // When
        List<ProductDTO> productList = productService.searchProduct(productName);
        System.out.println("================================");
        System.out.println(productList);
        System.out.println("================================");

        // Then
        assertNotNull(productList);

    }

}
