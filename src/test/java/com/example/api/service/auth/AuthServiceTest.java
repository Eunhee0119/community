package com.example.api.service.auth;

import com.example.api.controller.auth.dto.TokenDto;
import com.example.api.controller.auth.request.TokenRequest;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.fixture.member.MemberFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static com.example.fixture.member.MemberConstant.TEST_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class AuthServiceTest {

    @Autowired
    AuthService authService;

    @Autowired
    MemberRepository memberRepository;

    Member savedMember;

    @BeforeEach
    void beforeAll() {
        savedMember = memberRepository.save(MemberFixture.createEncodedPasswordMember());
    }

    @DisplayName("토큰을 발급한다.")
    @Test()
    void getToken(){
        //given
        TokenRequest request = TokenRequest.builder()
                                            .email(savedMember.getEmail())
                                            .password(TEST_PASSWORD)
                                            .build();

        //when
        TokenDto token = authService.getToken(request);

        //then
        assertThat(token.getAccessToken()).isNotEmpty();
        assertThat(token.getReflashToken()).isNotEmpty();
    }

    @DisplayName("유효하지 않은 이메일로 토큰을 발급 시 에러가 발생한다.")
    @Test()
    void getTokenWithInvalidEmail(){
        //given
        String invalidEmail = "invalid@test.com";
        TokenRequest request = TokenRequest.builder()
                .email(invalidEmail)
                .password(TEST_PASSWORD)
                .build();

        //when //then
        assertThatThrownBy(()->authService.getToken(request))
                .hasMessage("자격 증명에 실패하였습니다.");
    }

    @DisplayName("유효하지 않은 패스워드로 토큰을 발급 시 에러가 발생한다.")
    @Test()
    void getTokenWithInvalidPassword(){
        //given
        String invalidPassword = "invalidPassword";
        TokenRequest request = TokenRequest.builder()
                .email(savedMember.getEmail())
                .password(invalidPassword)
                .build();

        //when //then
        assertThatThrownBy(()->authService.getToken(request))
                .hasMessage("자격 증명에 실패하였습니다.");
    }
}