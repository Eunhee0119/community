package com.example.api.service.member.request;

import com.example.domain.member.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.example.fixture.member.MemberConstant.*;
import static com.example.fixture.member.MemberConstant.TEST_ZIP_CODE;
import static com.example.fixture.member.MemberFixture.createCustomMember;
import static com.example.fixture.member.MemberFixture.createDefaultMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class MemberCreateServiceRequestTest {

    @DisplayName("비밀번호는 영문, 숫자, 특수문자를 각각 한개 이상 포함한 8~16자 이내여야합니다.")
    @Test()
    void validPassword() {
        //given//when
        boolean result = MemberCreateServiceRequest.validPassword(TEST_PASSWORD);

        //then
        assertThat(result).isTrue();
    }

    @DisplayName("비밀번호가 영문, 숫자, 특수문자를 각각 한개 이상 포함한 8~16자가 아닐 경우 에러가 발생한다.")
    @Test()
    void validPasswordWhenNotValid() {
        //given//when//then
        assertThatThrownBy(() -> MemberCreateServiceRequest.validPassword("test1234"))
                .hasMessage("비밀번호는 영문, 숫자, 특수문자를 각각 한개 이상 포함한 8~16자 이내여야합니다.");

        assertThatThrownBy(() -> MemberCreateServiceRequest.validPassword("t1@"))
                .hasMessage("비밀번호는 영문, 숫자, 특수문자를 각각 한개 이상 포함한 8~16자 이내여야합니다.");

        assertThatThrownBy(() -> MemberCreateServiceRequest.validPassword("test#@!!!!!"))
                .hasMessage("비밀번호는 영문, 숫자, 특수문자를 각각 한개 이상 포함한 8~16자 이내여야합니다.");
    }
}