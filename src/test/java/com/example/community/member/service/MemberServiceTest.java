package com.example.community.member.service;

import com.example.community.member.domain.RoleType;
import com.example.community.member.service.request.MemberCreateServiceRequest;
import com.example.community.member.service.response.MemberResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static com.example.community.member.domain.RoleType.MEMBER;
import static com.example.util.fixture.member.MemberConstant.*;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    PasswordEncoder passwordEncoder;


    @DisplayName("회원을 생성한다.")
    @Test()
    void createMember() {
        //given

        MemberCreateServiceRequest memberRequest = createMemberRequest(TEST_EMAIL, TEST_PASSWORD, TEST_NAME,
                TEST_PHONE, TEST_AGE, TEST_CITY, TEST_STREET, TEST_ZIP_CODE, MEMBER);

        //when
        MemberResponse createdMember = memberService.createMember(memberRequest);

        //then
        assertThat(createdMember.getId()).isNotNull();
        assertThat(createdMember)
                .extracting("email", "name", "phone", "age", "roleType")
                .containsExactlyInAnyOrder(TEST_EMAIL, TEST_NAME, TEST_PHONE, TEST_AGE, MEMBER);
        assertThat(passwordEncoder.matches(TEST_PASSWORD, createdMember.getPassword())).isTrue();
        assertThat(createdMember.getAddress())
                .extracting("city", "street", "zipcode")
                .containsExactlyInAnyOrder(TEST_CITY, TEST_STREET, TEST_ZIP_CODE);
    }

    @DisplayName("회원을 생성할 때 roleType 값을 넣지 않을 경우 MEMBER 타입으로 가입된다.")
    @Test()
    void createMemberWithoutRoleType() {
        //given

        MemberCreateServiceRequest memberRequest = createMemberRequest(TEST_EMAIL, TEST_PASSWORD, TEST_NAME, TEST_PHONE
                , TEST_AGE, TEST_CITY, TEST_STREET, TEST_ZIP_CODE, null);

        //when
        MemberResponse createdMember = memberService.createMember(memberRequest);

        //then
        assertThat(createdMember.getId()).isNotNull();
        assertThat(createdMember)
                .extracting("email", "name", "phone", "age", "roleType")
                .containsExactlyInAnyOrder(TEST_EMAIL, TEST_NAME, TEST_PHONE, TEST_AGE, MEMBER);
        assertThat(passwordEncoder.matches(TEST_PASSWORD, createdMember.getPassword())).isTrue();
        assertThat(createdMember.getAddress())
                .extracting("city", "street", "zipcode")
                .containsExactlyInAnyOrder(TEST_CITY, TEST_STREET, TEST_ZIP_CODE);
    }


    private MemberCreateServiceRequest createMemberRequest(String email, String password, String name, String phone,
                                                           int age, String city, String street, String zipcode, RoleType roleType) {
        return MemberCreateServiceRequest.builder()
                .email(email)
                .password(password)
                .name(name)
                .phone(phone)
                .age(age)
                .city(city)
                .street(street)
                .zipcode(zipcode)
                .roleType(roleType)
                .build();
    }

}