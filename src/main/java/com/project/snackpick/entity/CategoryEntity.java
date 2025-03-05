package com.project.snackpick.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "CATEGORY_T")
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int categoryId;

    @Column(name = "category_name")
    private String categoryName;

    // 대분류 (NULL 이면 대분류, 값이 존재하는 경우 중분류)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "top_category_id")
    @JsonIgnore
    private CategoryEntity topCategory;

    // 중분류 리스트 (대분류가 여러 개의 중분류를 가질 수 있다.)
    @OneToMany(mappedBy = "topCategory")
    @BatchSize(size = 20)
    @JsonManagedReference // JSON 직렬화 허용...
    private List<CategoryEntity> subCategoryList = new ArrayList<>();

    @Override
    public String toString() {
        return "CategoryEntity{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", topCategoryId=" + (topCategory != null ? topCategory.getCategoryId() : "null") + // ID만 출력
                '}';
    }
}
