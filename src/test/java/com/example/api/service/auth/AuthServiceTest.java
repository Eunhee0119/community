package com.example.api.service.auth;

import com.example.api.controller.auth.dto.TokenDto;
import com.example.api.controller.auth.request.TokenRequest;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.fixture.member.MemberFixture;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    RedisTemplate<String, String> redisTemplate;


    @Value("${security.jwt.secret-key}")
    private String secretKey;

    Member savedMember;

    @BeforeEach
    void beforeAll() {
        savedMember = memberRepository.save(MemberFixture.createEncodedPasswordMember());
    }

    @DisplayName("토큰을 발급한다.")
    @Test()
    void getToken() {
        //given
        TokenRequest request = TokenRequest.builder()
                .email(savedMember.getEmail())
                .password(TEST_PASSWORD)
                .build();

        //when
        TokenDto token = authService.getToken(request);

        //then
        assertThat(token.getAccessToken()).isNotEmpty();
        assertThat(token.getRefreshToken()).isNotEmpty();
    }

    @DisplayName("유효하지 않은 이메일로 토큰을 발급 시 에러가 발생한다.")
    @Test()
    void getTokenWithInvalidEmail() {
        //given
        String invalidEmail = "invalid@test.com";
        TokenRequest request = TokenRequest.builder()
                .email(invalidEmail)
                .password(TEST_PASSWORD)
                .build();

        //when //then
        assertThatThrownBy(() -> authService.getToken(request))
                .hasMessage("자격 증명에 실패하였습니다.");
    }

    @DisplayName("유효하지 않은 패스워드로 토큰을 발급 시 에러가 발생한다.")
    @Test()
    void getTokenWithInvalidPassword() {
        //given
        String invalidPassword = "invalidPassword";
        TokenRequest request = TokenRequest.builder()
                .email(savedMember.getEmail())
                .password(invalidPassword)
                .build();

        //when //then
        assertThatThrownBy(() -> authService.getToken(request))
                .hasMessage("자격 증명에 실패하였습니다.");
    }

    @DisplayName("리프레시 토큰으로 토큰을 재발급한다.")
    @Test()
    void regenerateToken() {
        //given
        String refreshToken = createRefreshtoken(10000000);

        //when
        TokenDto tokenDto = authService.regenerateToken(refreshToken);

        //then
        assertThat(tokenDto.getAccessToken()).isNotEmpty();
        assertThat(tokenDto.getRefreshToken()).isNotEmpty();
        assertThat(tokenDto.getRefreshToken()).isNotEqualTo(refreshToken);
    }

    @DisplayName("만료된 리프레시 토큰으로 토큰을 재발급할 경우 에러가 발생한다.")
    @Test()
    void regenerateTokenWhitTimeoutToken() throws InterruptedException {
        //given
        String refreshToken = createRefreshtoken(20000);

        //when
        Thread.sleep(20000);

        //then
        assertThatThrownBy(() -> authService.regenerateToken(refreshToken));
    }

    @DisplayName("재발급 이전의 토큰으로 토큰을 재발급할 경우 에러가 발생한다.")
    @Test()
    void regenerateTokenWhitInvalidToken() throws InterruptedException {
        //given
        String invalidToken = createRefreshtoken(1000000);
        Thread.sleep(1000);
        createRefreshtoken(1000000);

        //when //then
        assertThatThrownBy(() -> authService.regenerateToken(invalidToken))
                .hasMessage("유효하지 않은 접근입니다.");
    }

    private String createRefreshtoken(long refreshExpireTime) {
        String email = "test1@gmail.com";
        String role = "ROLE_MEMBER";
        Authentication authentication = new TestingAuthenticationToken(email, null, role);

        String refreshToken = Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey)), SignatureAlgorithm.HS512)
                .setSubject(authentication.getName())
                .claim("AuthKey", role)
                .setExpiration(new Date((new Date()).getTime() + refreshExpireTime))    // 8
                .compact();

        redisTemplate.delete(email);

        redisTemplate.opsForValue().set(
                authentication.getName(),
                refreshToken,
                refreshExpireTime,
                TimeUnit.MILLISECONDS
        );
        return refreshToken;
    }
}