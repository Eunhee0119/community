package com.example.community.category.service;

import com.example.community.category.domain.Category;
import com.example.community.category.respository.CategoryRepository;
import com.example.community.category.service.request.CategoryCreateServiceRequest;
import com.example.community.category.service.request.CategoryUpdateServiceRequest;
import com.example.community.category.service.response.CategoryResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static com.example.util.fixture.category.CategoryFixture.newCategory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

@SpringBootTest
class CategoryServiceTest {

    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryRepository categoryRepository;

    @AfterEach
    void tearDown() {
        categoryRepository.deleteAllInBatch();
    }

    @DisplayName("상위 카테고리가 있는 카테고리를 생성한다.")
    @Test()
    void createCategory() {
        //given
        CategoryResponse topCategory = categoryService.createCategory(CategoryCreateServiceRequest.builder().name("상위카테고리").build());
        CategoryCreateServiceRequest newCategoryRequest = CategoryCreateServiceRequest.builder()
                .name("새로운 카테고리")
                .parentId(topCategory.getId())
                .build();

        //when
        CategoryResponse newCategory = categoryService.createCategory(newCategoryRequest);

        //then
        assertThat(topCategory.getId()).isNotNull();
        assertThat(newCategory.getId()).isNotNull();
    }

    @DisplayName("최상위 카테고리를 생성한다.")
    @Test()
    void createCategoryWithNullParentId() {
        //given
        CategoryCreateServiceRequest newCategoryRequest = CategoryCreateServiceRequest.builder()
                .name("새로운 카테고리")
                .build();

        //when
        CategoryResponse newCategory = categoryService.createCategory(newCategoryRequest);
        Optional<Category> findCategory = categoryRepository.findById(newCategory.getId());

        //then
        assertThat(newCategory.getId()).isNotNull();
        assertThat(findCategory.isEmpty()).isFalse();

    }

    @DisplayName("존재하지 않는 상위 카테고리의 하위 카테고리를 생성할 때 에러가 발생한다.")
    @Test()
    void createCategoryWithInvalidParentId() {
        //given
        CategoryCreateServiceRequest newCategoryRequest = CategoryCreateServiceRequest.builder()
                .name("새로운 카테고리")
                .parentId(10000000L)
                .build();

        //when//then
        assertThatThrownBy(() -> categoryService.createCategory(newCategoryRequest)
                , "존재하지 않는 상위 카테고리입니다.");
    }

    @DisplayName("카테고리 정보를 변경한다.")
    @Test()
    void updateCategory() {
        //given
        Category category1 = newCategory("카테고리1");
        Category category2 = newCategory("카테고리2", category1);
        categoryRepository.saveAll(List.of(category1, category2));

        String updateName = "변경된 이름";
        CategoryUpdateServiceRequest categoryRequest = CategoryUpdateServiceRequest.builder()
                .id(category2.getId())
                .name(updateName)
                .parentId(category2.getParent().getId()).build();
        //when
        categoryService.updateCategory(categoryRequest);
        Category updateCategory = categoryRepository.findById(category2.getId()).get();

        //then
        assertThat(updateCategory).extracting("id","name","parent.id")
                .containsExactly(category2.getId(),updateName,category1.getId());
    }

    @DisplayName("존재하지 않는 카테고리의 정보를 변경하면 에러가 발생한다.")
    @Test()
    void updateCategoryWithNoExistCategory() {
        //given
        CategoryUpdateServiceRequest categoryRequest = CategoryUpdateServiceRequest.builder()
                .id(1L)
                .name("이름").build();

        //when //then
        assertThatThrownBy(()->categoryService.updateCategory(categoryRequest)
                ,"존재하지 않는 카테고리입니다.");
    }

    @DisplayName("존재하지 않는 상위 카테고리의 하위로 카테고리를 이동할 경우 에러가 발생한다.")
    @Test()
    void updateCategoryWithNoExistParentCategory() {
        //given
        Category category1 = newCategory("카테고리1");
        Category category2 = newCategory("카테고리2", category1);
        categoryRepository.saveAll(List.of(category1, category2));

        Long noExistParentId = 10000L;
        CategoryUpdateServiceRequest categoryRequest = CategoryUpdateServiceRequest.builder()
                .id(category2.getId())
                .name("변경된 이름")
                .parentId(noExistParentId).build();

        //when //then
        assertThatThrownBy(()->categoryService.updateCategory(categoryRequest)
                ,"존재하지 않는 상위 카테고리입니다.");
    }

    @DisplayName("카테고리를 삭제한다.")
    @Test()
    void deleteCategory() {
        //given
        Category category = Category.builder().name("카테고리").build();
        categoryRepository.save(category);

        //when
        categoryService.deleteCategory(category.getId());
        Optional<Category> findCategory = categoryRepository.findById(category.getId());

        //then
        assertThat(findCategory.isEmpty()).isTrue();
    }

    @DisplayName("존재하지 않는 카테고리를 삭제할 경우 에러가 발생한다.")
    @Test()
    void deleteCategoryWhenNoExistCategory() {
        //given
        //when//then
        assertThatThrownBy(() -> categoryService.deleteCategory(0L)
                , "이미 존재하지 않는 카테고리입니다.");
    }

    @DisplayName("전체 카테고리를 조회한다.")
    @Test()
    void findAllHierarchyCategory() {
        //given
        Category category1 = newCategory("카테고리1");
        Category category2 = newCategory("카테고리2", category1);
        Category category3 = newCategory("카테고리3", category1);
        Category category4 = newCategory("카테고리4");
        Category category5 = newCategory("카테고리5", category3);
        Category category6 = newCategory("카테고리6", category4);
        categoryRepository.saveAll(List.of(category1, category2, category3, category4, category5, category6));

        //when
        List<CategoryResponse> categories = categoryService.findAllHierarchyCategory();

        //then
        assertThat(categories).hasSize(2)
                .extracting("name", "depth")
                .containsExactly(tuple(category1.getName(), 0),
                        tuple(category4.getName(), 0));
        assertThat(categories.get(0).getChildren()).hasSize(2)
                .extracting("name", "depth")
                .containsExactly(tuple(category2.getName(), 1),
                        tuple(category3.getName(), 1));
        ;
    }

    @DisplayName("전체 카테고리를 조회 시 카테고리가 하나도 없을 경우 빈 리스트를 반환한다.")
    @Test()
    void findAllHierarchyCategoryWhenNotExistCategory() {
        //given
        //when
        List<CategoryResponse> categories = categoryService.findAllHierarchyCategory();

        //then
        assertThat(categories).hasSize(0);
    }
}