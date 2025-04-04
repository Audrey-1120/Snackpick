package com.project.snackpick.repository;

import com.project.snackpick.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {

    // 모든 카테고리 조회
    @Query("SELECT DISTINCT c FROM CategoryEntity c " +
            "LEFT JOIN FETCH c.subCategoryList " +
            "WHERE c.topCategory IS NULL")
    List<CategoryEntity> findAllCategoryList();

}
