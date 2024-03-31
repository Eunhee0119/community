package com.example.community.category.respository;


import com.example.community.category.domain.Category;
import com.example.util.fixture.category.CategoryFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.util.fixture.category.CategoryFixture.createCategory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@SpringBootTest
@Transactional
class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;


    @DisplayName("카테고리 리스트를 조회한다.")
    @Test()
    void findAllOrderByParentIdAsc() {
        //given
        Category category1 = CategoryFixture.createCategory("카테고리1");
        Category category2 = createCategory("카테고리2",category1);
        Category category3 = createCategory("카테고리3",category1);
        Category category4 = CategoryFixture.createCategory("카테고리4");
        Category category5 = createCategory("카테고리5",category3);
        Category category6 = createCategory("카테고리6",category4);
        categoryRepository.saveAll(List.of(category1, category2, category3, category4, category5, category6));

        //when
        List<Category> categories = categoryRepository.findAllOrderByParentIdAsc();

        //then
        assertThat(categories).extracting("id", "name", "parent")
                .containsExactly(tuple(category1.getId(), category1.getName(), category1.getParent()),
                        tuple(category4.getId(), category4.getName(), category4.getParent()),
                        tuple(category2.getId(), category2.getName(), category2.getParent()),
                        tuple(category3.getId(), category3.getName(), category3.getParent()),
                        tuple(category5.getId(), category5.getName(), category5.getParent()),
                        tuple(category6.getId(), category6.getName(), category6.getParent())
                );
    }



}