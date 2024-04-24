package com.example.community.category.controller;

import com.example.community.ApiResponse;
import com.example.community.category.controller.request.CategoryCreateRequest;
import com.example.community.category.controller.request.CategoryUpdateRequest;
import com.example.community.category.service.CategoryService;
import com.example.community.category.service.response.CategoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/new")
    public ApiResponse<CategoryResponse> createCategory(@Valid @RequestBody CategoryCreateRequest createRequest) {
        CategoryResponse category = categoryService.createCategory(createRequest.toServiceRequest());
        return ApiResponse.ok(category);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{id}")
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable("id") Long categoryId
            , @Valid @RequestBody CategoryUpdateRequest updateRequest) {
        if(!updateRequest.getId().equals(categoryId)) throw new IllegalArgumentException("잘못된 요청입니다.");

        categoryService.updateCategory(updateRequest.toServiceRequest());
        return ApiResponse.noContent();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public ApiResponse<CategoryResponse> deleteCategory(@PathVariable("id") Long categoryId){
        categoryService.deleteCategory(categoryId);
        return ApiResponse.noContent();
    }

}
