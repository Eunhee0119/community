package com.example.community.category.controller.request;

import com.example.community.category.service.request.CategoryCreateServiceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryCreateRequest {

    @NotBlank
    private String name;

    @Positive
    private Long parentId;

    @Builder
    private CategoryCreateRequest(String name, Long parentId) {
        this.name = name;
        this.parentId = parentId;
    }

    public CategoryCreateServiceRequest toServiceRequest() {
        return CategoryCreateServiceRequest.builder()
                .name(this.name)
                .parentId(this.parentId)
                .build();
    }
}
