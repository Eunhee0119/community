package com.example.util.fixture.category;

import com.example.community.category.domain.Category;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

public class CategoryFixture {

    public static List<Category> createCategoryList() {
        Category category1 = newCategory("카테고리 1");
        Category category2 = newCategory("카테고리 2");
        Category category3 = newCategory("카테고리 3");
        Category category4 = newCategory("카테고리 4");
        Category category5 = newCategory("카테고리 5");
        Category category6 = newCategory("카테고리 6");
        return List.of(category1, category2, category3, category4, category5, category6);
    }

    public static Category newCategory(String name) {
        return newCategory(name, null);
    }

    public static Category newCategory(String name, Category parent) {
        return Category.builder().name(name).parent(parent).build();
    }

}
