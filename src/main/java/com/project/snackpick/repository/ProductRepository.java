package com.project.snackpick.repository;

import com.project.snackpick.entity.ProductEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {

    // 제품 검색
    // SELECT * FROM PRODUCT WHERE PRODUCT_NAME LIKE CONCAT('%', #{KEYWORD}, '%');
    //    @EntityGraph(attributePaths = {"reviewList", "topCategory", "subCategory"}) // 자동으로 필요한 연관 엔티티를 가져온다.
    //    @Query("SELECT p FROM ProductEntity p WHERE p.productName LIKE %:searchKeyword%")


    @Query("SELECT p, COUNT(r.reviewId) FROM ProductEntity p " +
           "LEFT JOIN p.reviewList r " + // LEFT JOIN은 p와 p.reviewList를 조인한 결과를 기준으로 카운트한다.
            "LEFT JOIN FETCH p.topCategory " +
            "LEFT JOIN FETCH p.subCategory " +
            "WHERE p.productName LIKE %:searchKeyword% " +
            "GROUP BY p")
    List<Object[]> SearchProductByProductName(@Param("searchKeyword") String searchKeyword);

    // 제품 상세 조회
    @EntityGraph(attributePaths = {"reviewList", "topCategory", "subCategory"})
    @Query("SELECT p FROM ProductEntity p WHERE p.productId = :productId")
    ProductEntity findProductByProductId(@Param("productId") int productId);

}
