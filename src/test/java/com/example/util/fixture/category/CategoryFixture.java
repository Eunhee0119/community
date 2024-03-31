package com.example.util.fixture.category;

import com.example.community.category.domain.Category;

import java.util.List;

public class CategoryFixture {

    public static List<Category> createCategoryList() {
        Category category1 = createCategory("카테고리 1");
        Category category2 = createCategory("카테고리 2");
        Category category3 = createCategory("카테고리 3");
        Category category4 = createCategory("카테고리 4");
        Category category5 = createCategory("카테고리 5");
        Category category6 = createCategory("카테고리 6");
        return List.of(category1, category2, category3, category4, category5, category6);
    }

    public static Category createCategory(String name) {
        return createCategory(name, null);
    }

    public static Category createCategory(String name, Category parent) {
        return Category.builder().name(name).parent(parent).build();
    }

}
