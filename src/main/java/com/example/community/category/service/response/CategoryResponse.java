package com.example.community.category.service.response;

import com.example.community.category.domain.Category;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
public class CategoryResponse {

    private Long id;
    private String name;

    private int depth;
    private List<CategoryResponse> children = new ArrayList<>();

    @Builder
    private CategoryResponse(Long id, String name, int depth, List<CategoryResponse> children) {
        this.id = id;
        this.name = name;
        this.depth = depth;
        this.children = children;
    }

    public static CategoryResponse of(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .depth(category.getDepth())
                .build();
    }

    public static List<CategoryResponse> of(List<Category> categories) {
        List<CategoryResponse> categoryList = new LinkedList<>();
        Map<Long, CategoryResponse> categoryMap = new HashMap<>();
        categories.stream().forEach(category -> {
            CategoryResponse categoryResponse = CategoryResponse.of(category);
            Category parent = category.getParent();
            if (Objects.isNull(parent)) categoryList.add(categoryResponse);
            else if (categoryMap.containsKey(parent.getId())) {
                CategoryResponse parentResponse = categoryMap.get(category.getParent().getId());
                parentResponse.addChildren(categoryResponse);
            }
            categoryMap.put(categoryResponse.getId(), categoryResponse);
        });

        return categoryList;
    }

    private void addChildren(CategoryResponse category) {
        if(Objects.isNull(this.children)) {
            this.children = new ArrayList<>();
        }
        this.children.add(category);
    }
}
