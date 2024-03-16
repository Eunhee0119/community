package com.example.community.category.respository;

import com.example.community.category.domain.Category;

import java.util.List;

public interface CategoryRepositoryCustom {

    List<Category> findAllOrderByParentIdAsc();

}
