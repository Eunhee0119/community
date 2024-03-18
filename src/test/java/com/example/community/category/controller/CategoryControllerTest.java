package com.example.community.category.controller;

import com.example.community.category.controller.request.CategoryCreateRequest;
import com.example.community.category.controller.request.CategoryUpdateRequest;
import com.example.community.category.service.CategoryService;
import com.example.community.category.service.response.CategoryResponse;
import com.example.community.config.jwt.JwtTokenProvider;
import com.example.util.jwt.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoryController.class)
@Import(TestSecurityConfig.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;


    @DisplayName("새로운 카테고리를 생성한다.")
    @Test()
    void newCategory() throws Exception {
        //given
        String categoryName = "categoryName";
        CategoryCreateRequest categoryCreateRequest = CategoryCreateRequest.builder().name(categoryName).build();

        CategoryResponse categoryResponse = CategoryResponse.builder().id(1L).depth(0).children(null).build();
        given(categoryService.createCategory(any())).willReturn(categoryResponse);

        //when //then
        mockMvc.perform(post("/api/categories/new")
                .content(objectMapper.writeValueAsString(categoryCreateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.name()))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @DisplayName("카테고리 생성 시 카테고리명이 20자가 넘어가면 에러가 발생한다.")
    @Test()
    void newCategoryWhenNameStringLimitOver() throws Exception {
        //given
        String categoryName = "  ";
        CategoryCreateRequest categoryCreateRequest = CategoryCreateRequest.builder()
                                                                            .name(categoryName)
                                                                            .build();

        //when //then
        mockMvc.perform(post("/api/categories/new")
                        .content(objectMapper.writeValueAsString(categoryCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("카테고리 생성 시 상위 카테고리 아이디가 자연수가 아닐 경우 에러가 발생한다.")
    @Test()
    void newCategoryWhenNotPositiveParentId() throws Exception {
        //given
        CategoryCreateRequest categoryCreateRequest = CategoryCreateRequest.builder()
                .name("categoryName")
                .parentId(-1L)
                .build();

        //when //then
        mockMvc.perform(post("/api/categories/new")
                        .content(objectMapper.writeValueAsString(categoryCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("카테고리 정보를 변경한다.")
    @Test()
    void updateCategory() throws Exception {
        //given
        Long id = 2L;
        CategoryUpdateRequest updateRequest = CategoryUpdateRequest.builder()
                                                                            .id(id)
                                                                            .name("카테고리명변경")
                                                                            .parentId(1L)
                                                                            .build();

        CategoryResponse updateResponse = CategoryResponse.builder().id(id).depth(2).children(null).build();
        given(categoryService.updateCategory(any())).willReturn(updateResponse);


        //when//then
        mockMvc.perform(patch("/api/categories/"+id)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.code").value(HttpStatus.NO_CONTENT.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.name()))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("카테고리 수정 시 상위 카테고리 아이디가 자연수가 아닐 경우 에러가 발생한다.")
    @Test()
    void updateCategoryWhenNullCategoryId() throws Exception {
        //given
        Long id = 2L;
        CategoryUpdateRequest updateRequest = CategoryUpdateRequest.builder()
                .id(id)
                .name("카테고리명변경")
                .parentId(-1L)
                .build();

        //when //then
        mockMvc.perform(patch("/api/categories/"+id)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("카테고리를 삭제한다.")
    @Test()
    void deleteCategory() throws Exception {
        //given
        Long id = 2L;

        //when//then
        mockMvc.perform(delete("/api/categories/"+id))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.code").value(HttpStatus.NO_CONTENT.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.name()))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
