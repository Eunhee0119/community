package com.example.community.category.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoryTest {

    @DisplayName("카테고리명은 영문,한글로 시작하는 영문,한글,숫자,공백을 포함한 0~20자이어야 한다.")
    @Test()
    void validCategoryName1(){
        //given //when
        Category.validCategoryName("test 카테고리01");
        //then
        assertThat(true).isTrue();
    }

    @DisplayName("카테고리명은 숫자로 시작할 수 없다.")
    @Test()
    void validCategoryName2(){
        //given //when
        //then
        assertThatThrownBy(()->Category.validCategoryName("01카테고리")
        ,"카테고리명은 영문,한글로 시작하며 영문,숫자,한글을 포함하여 20자를 넘어갈 수 없습니다.");
    }

    @DisplayName("카테고리명은 20자를 넘을 수 없다.")
    @Test()
    void validCategoryName3(){
        //given //when
        //then
        assertThatThrownBy(()->Category.validCategoryName("카테고리명이 20자가 넘어가면 에러가 발생한다")
                ,"카테고리명은 영문,한글로 시작하며 영문,숫자,한글을 포함하여 20자를 넘어갈 수 없습니다.");
    }

}