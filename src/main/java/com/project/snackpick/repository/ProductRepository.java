package com.project.snackpick.repository;

import com.project.snackpick.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {

    // 제품 검색
    List<ProductEntity> findByProductNameLike(String searchKeyword);

    // 제품 상세 조회
    ProductEntity findByProductId(int productId);
}
