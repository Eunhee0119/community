package com.example.community.category.service.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryUpdateServiceRequest {

    private Long id;
    private String name;
    private Long parentId;

    @Builder
    private CategoryUpdateServiceRequest(Long id, String name,  Long parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
    }
}
