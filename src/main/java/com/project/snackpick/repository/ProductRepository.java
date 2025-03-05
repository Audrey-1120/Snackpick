package com.project.snackpick.repository;

import com.project.snackpick.entity.ProductEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {

    // 제품 검색
    @Query("SELECT p, p.reviewCount FROM ProductEntity p " +
            "LEFT JOIN FETCH p.topCategory " +
            "LEFT JOIN FETCH p.subCategory " +
            "WHERE p.productName LIKE %:searchKeyword% " +
            "GROUP BY p")
    List<ProductEntity> SearchProductByProductName(@Param("searchKeyword") String searchKeyword);

    // 제품 상세 조회
    @EntityGraph(attributePaths = {"reviewList", "topCategory", "subCategory"})
    @Query("SELECT p FROM ProductEntity p WHERE p.productId = :productId")
    Optional<ProductEntity> findProductByProductId(@Param("productId") int productId);

}
