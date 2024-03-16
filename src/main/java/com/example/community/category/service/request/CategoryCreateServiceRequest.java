package com.example.community.category.service.request;

import com.example.community.category.domain.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CategoryCreateServiceRequest {

    private String name;
    private Long parentId;

    @Builder
    private CategoryCreateServiceRequest(String name, Long parentId) {
        this.name = name;
        this.parentId = parentId;
    }

    public Category toEntity(Category parent) {
        return Category.builder()
                .name(name)
                .parent(parent)
                .build();
    }
}
