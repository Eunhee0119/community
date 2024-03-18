package com.example.community.category.service;

import com.example.community.category.exception.NoSuchCategoryFoundException;
import com.example.community.category.domain.Category;
import com.example.community.category.respository.CategoryRepository;
import com.example.community.category.service.request.CategoryCreateServiceRequest;
import com.example.community.category.service.request.CategoryUpdateServiceRequest;
import com.example.community.category.service.response.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponse createCategory(CategoryCreateServiceRequest categoryCreateRequest) {
        Category parentCategory = null;
        if (!Objects.isNull(categoryCreateRequest.getParentId())) {
            parentCategory = categoryRepository.findById(categoryCreateRequest.getParentId()).orElseThrow(() -> new NoSuchCategoryFoundException("존재하지 않는 상위 카테고리입니다."));
        }
        Category category = categoryCreateRequest.toEntity(parentCategory);
        return CategoryResponse.of(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse updateCategory(CategoryUpdateServiceRequest categoryRequest) {
        Category category = categoryRepository.findById(categoryRequest.getId())
                .orElseThrow(() -> new NoSuchCategoryFoundException("존재하지 않는 카테고리입니다."));

        category.changeName(categoryRequest.getName());

        Category parent = null;
        if (categoryRequest.getParentId() > 0) {
            parent = categoryRepository.findById(categoryRequest.getParentId())
                    .orElseThrow(() -> new NoSuchCategoryFoundException("존재하지 않는 상위 카테고리입니다."));
        }

        if (!category.getParent().equals(parent)) {
            category.changePosition(parent);
        }

        return CategoryResponse.of(category);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoSuchCategoryFoundException("이미 존재하지 않는 카테고리입니다."));
        categoryRepository.delete(category);
    }

    public List<CategoryResponse> findAllHierarchyCategory() {
        return CategoryResponse.of(categoryRepository.findAllOrderByParentIdAsc());
    }

}
