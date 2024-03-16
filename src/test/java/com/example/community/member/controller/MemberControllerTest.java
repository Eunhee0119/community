package com.example.community.member.controller;

import com.example.community.member.controller.request.MemberCreateRequest;
import com.example.community.member.service.MemberService;
import com.example.community.config.jwt.JwtTokenProvider;
import com.example.community.member.domain.RoleType;
import com.example.util.fixture.member.MemberConstant;
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

import static com.example.community.member.domain.RoleType.MEMBER;
import static com.example.util.fixture.member.MemberConstant.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberController.class)
@Import(TestSecurityConfig.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @DisplayName("회원 가입을 한다.")
    @Test
    void createMember() throws Exception {

        MemberCreateRequest request = createMemberRequest(MemberConstant.TEST_EMAIL, TEST_PASSWORD, TEST_NAME,
                TEST_PHONE, TEST_AGE, TEST_CITY, TEST_STREET, TEST_ZIP_CODE, MEMBER);

        mockMvc.perform(post("/api/members/new")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.name()));

    }

    @DisplayName("회원을 생성할 때 이메일 형식이 아닐 경우 에러가 발생한다.")
    @Test
    void createMemberWhenNotValidEmail() throws Exception {

        MemberCreateRequest request = createMemberRequest("testtest", TEST_PASSWORD, TEST_NAME,
                TEST_PHONE, TEST_AGE, TEST_CITY, TEST_STREET, TEST_ZIP_CODE, MEMBER);

        mockMvc.perform(post("/api/members/new")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("이메일 형식에 맞지 않습니다."));
    }


    @DisplayName("회원을 생성할 때 패스워드를 입력하지 않는 경우 에러가 발생한다.")
    @Test
    void createMemberWhenBlankPassword() throws Exception {

        MemberCreateRequest request = createMemberRequest(TEST_EMAIL, " ", TEST_NAME,
                TEST_PHONE, TEST_AGE, TEST_CITY, TEST_STREET, TEST_ZIP_CODE, MEMBER);

        mockMvc.perform(post("/api/members/new")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("패스워드는 필수 입력 값입니다."));
    }

    @DisplayName("회원을 생성할 때 이름을 입력하지 않는 경우 에러가 발생한다.")
    @Test
    void createMemberWhenBlankName() throws Exception {

        MemberCreateRequest request = createMemberRequest(TEST_EMAIL, TEST_PASSWORD, " ",
                TEST_PHONE, TEST_AGE, TEST_CITY, TEST_STREET, TEST_ZIP_CODE, MEMBER);

        mockMvc.perform(post("/api/members/new")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("이름은 필수 입력 값입니다."));
    }

    @DisplayName("회원을 생성할 때 나이를 입력하지 않거나 0이하인 경우 에러가 발생한다.")
    @Test
    void createMemberWhenNotPositiveAge() throws Exception {

        MemberCreateRequest request = createMemberRequest(TEST_EMAIL, TEST_PASSWORD, TEST_NAME,
                TEST_PHONE, 0, TEST_CITY, TEST_STREET, TEST_ZIP_CODE, MEMBER);

        mockMvc.perform(post("/api/members/new")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("나이는 필수 입력 값입니다."));
    }

    @DisplayName("회원 정보를 조회한다.")
    @Test
    void getMember() throws Exception {


        mockMvc.perform(get("/api/members/1"))
                .andDo(print())
                .andExpect(status().isOk());
//                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
//                .andExpect(jsonPath("$.status").value(HttpStatus.OK.name()));

    }

    private MemberCreateRequest createMemberRequest(String email, String password, String name, String phone,
                                                    int age, String city, String street, String zipcode, RoleType roleType) {

        return MemberCreateRequest.builder()
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