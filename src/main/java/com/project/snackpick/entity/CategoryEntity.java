package com.project.snackpick.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @ManyToOne
    @JoinColumn(name = "top_category_id")
    private CategoryEntity topCategory;

    // 중분류 리스트 (대분류가 여러 개의 중분류를 가질 수 있다.)
    @OneToMany(mappedBy = "topCategory")
    private List<CategoryEntity> subCategories = new ArrayList<>();

}
