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

    @Column(name = "category_name", length = 30)
    private String categoryName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "top_category_id")
    @JsonIgnore
    private CategoryEntity topCategory;

    @OneToMany(mappedBy = "topCategory")
    @BatchSize(size = 20)
    @JsonManagedReference
    private List<CategoryEntity> subCategoryList = new ArrayList<>();

    @Override
    public String toString() {
        return "CategoryEntity{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", topCategoryId=" + (topCategory != null ? topCategory.getCategoryId() : "null") +
                '}';
    }
}
