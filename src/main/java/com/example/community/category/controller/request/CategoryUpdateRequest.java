package com.example.community.category.controller.request;

import com.example.community.category.service.request.CategoryUpdateServiceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryUpdateRequest {


    @NotNull
    @Positive
    private Long id;

    @NotBlank
    private String name;

    @Positive
    private Long parentId;

    @Builder
    public CategoryUpdateRequest(Long id, String name, Long parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
    }

    public CategoryUpdateServiceRequest toServiceRequest() {
        return CategoryUpdateServiceRequest.builder()
                .id(this.id)
                .name(this.name)
                .parentId(this.parentId)
                .build();
    }
}
