package com.example.domain.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.example.fixture.member.MemberFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    @DisplayName("비밀번호는 영문, 숫자, 특수문자를 각각 한개 이상 포함한 8~16자 이내여야합니다.")
    @Test()
    void validPassword() {
        //given//when
        Member member = createDefaultMember();

        //then
        assertThat(member).extracting("email", "password")
                .containsExactly(TEST_EMAIL, TEST_PASSWORD);
    }

    @DisplayName("비밀번호가 영문, 숫자, 특수문자를 각각 한개 이상 포함한 8~16자가 아닐 경우 에러가 발생한다.")
    @Test()
    void validPasswordWhenNotValid() {
        //given//when//then
        assertThatThrownBy(() -> createCustomMember(TEST_EMAIL, "test1234", TEST_NAME, TEST_PHONE
                , TEST_AGE, TEST_CITY, TEST_STREET, TEST_ZIP_CODE))
                .hasMessage("비밀번호는 영문, 숫자, 특수문자를 각각 한개 이상 포함한 8~16자 이내여야합니다.");

        assertThatThrownBy(() -> createCustomMember(TEST_EMAIL, "t1@", TEST_NAME, TEST_PHONE
                , TEST_AGE, TEST_CITY, TEST_STREET, TEST_ZIP_CODE))
                .hasMessage("비밀번호는 영문, 숫자, 특수문자를 각각 한개 이상 포함한 8~16자 이내여야합니다.");

        assertThatThrownBy(() -> createCustomMember(TEST_EMAIL, "test#@!!!!!", TEST_NAME, TEST_PHONE
                , TEST_AGE, TEST_CITY, TEST_STREET, TEST_ZIP_CODE))
                .hasMessage("비밀번호는 영문, 숫자, 특수문자를 각각 한개 이상 포함한 8~16자 이내여야합니다.");
    }

}