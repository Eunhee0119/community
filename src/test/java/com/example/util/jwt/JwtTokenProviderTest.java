package com.example.util.jwt;

import com.example.community.auth.exception.InvalidTokenException;
import com.example.community.config.jwt.JwtTokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class JwtTokenProviderTest {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    final static String email = "test1@gmail.com";
    final static String role = "ROLE_MEMBER";


    @DisplayName("회원 정보를 담은 access JWT 토큰을 생성한다.")
    @Test
    void createAccessToken() {
        //given
        Authentication authentication = new TestingAuthenticationToken("test1@gmail.com", null, "ROLE_MEMBER");

        // when
        String token = jwtTokenProvider.createAccessToken(authentication);

        // then
        assertThat(token).isNotBlank();
    }

    @DisplayName("회원 정보를 담은 refresh JWT 토큰을 생성한다.")
    @Test
    void createRefreshToken() {
        //given
        Authentication authentication = new TestingAuthenticationToken("test1@gmail.com", null, "ROLE_MEMBER");

        // when
        String token = jwtTokenProvider.createRefreshToken(authentication);
        String savedRefreshToken = redisTemplate.opsForValue().get(authentication.getName());

        // then
        assertThat(token).isNotBlank();
        assertThat(savedRefreshToken).isEqualTo(token);
    }

    @DisplayName("회원 정보를 담은 refresh JWT 토큰을 생성 시 이전 refresh 토큰은 삭제한다.")
    @Test
    void createRefreshTokenWhenDuplicatedToken() throws InterruptedException {
        //given
        Authentication authentication = new TestingAuthenticationToken("test1@gmail.com", null, "ROLE_MEMBER");

        // when
        String token = jwtTokenProvider.createRefreshToken(authentication);
        Thread.sleep(1000);
        String newToken = jwtTokenProvider.createRefreshToken(authentication);
        String savedRefreshToken = redisTemplate.opsForValue().get(authentication.getName());

        // then
        assertThat(token).isNotBlank();
        assertThat(savedRefreshToken).isNotEqualTo(token);
        assertThat(savedRefreshToken).isEqualTo(newToken);
    }

    @DisplayName("토큰 정보로 authentication을 조회한다.")
    @Test
    void getAuthentication() {
        //given
        Authentication authentication = new TestingAuthenticationToken(email, null, role);
        String token = jwtTokenProvider.createAccessToken(authentication);

        //when
        Authentication authFromToken = jwtTokenProvider.getAuthentication(token);
        User principal = (User) authFromToken.getPrincipal();

        //then
        assertThat(authFromToken.isAuthenticated()).isEqualTo(authentication.isAuthenticated());
        assertThat(principal.getUsername()).isEqualTo(email);
    }

    @DisplayName("유효하지 않은 토큰 정보로 authentication을 조회하면 에러가 발생한다.")
    @Test
    void getAuthenticationWithInvalidToken() {
        //when //then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> jwtTokenProvider.getAuthentication(null));
    }

    @DisplayName("만료된 토큰 정보로 authentication을 조회하면 에러가 발생한다.")
    @Test
    void getAuthenticationWithExpiredToken() {
        //given
        String expiredToken = Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey)), SignatureAlgorithm.HS512)
                .setSubject(String.valueOf(1L))
                .setExpiration(new Date((new Date()).getTime() - 1))    // 8
                .compact();

        //when //then
        assertThatExceptionOfType(InvalidTokenException.class)
                .isThrownBy(() -> jwtTokenProvider.getAuthentication(expiredToken));
    }

    @DisplayName("만료된 토큰 정보로 authentication을 조회하면 에러가 발생한다.")
    @Test
    void getAuthenticationWithWrongSecretKeyToken() {
        //given
        String expiredToken = Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey)), SignatureAlgorithm.HS512)
                .setSubject(String.valueOf(1L))
                .setExpiration(new Date((new Date()).getTime() - 1))
                .compact();

        //when //then
        assertThatExceptionOfType(InvalidTokenException.class)
                .isThrownBy(() -> jwtTokenProvider.getAuthentication(expiredToken));
    }


    @DisplayName("시크릿 키가 틀린 토큰 정보로 payload를 조회할 경우 예외를 발생시킨다.")
    @Test
    void getPayloadByWrongSecretKeyToken() {
        //given
        String wrongSceretKey = "IbFqfWcbGtNkwU8T3OGTkfplLCeUGpbSwubFe8uvhZ9efOMAnR7E6W4LOgHenKC5qJtQPzopFBGhhzRcehxs92fgx0t";
        String wrongToken = Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(wrongSceretKey)), SignatureAlgorithm.HS512)
                .setSubject(String.valueOf(1L))
                .setExpiration(new Date((new Date()).getTime() - 1))
                .compact();

        assertThatExceptionOfType(SignatureException.class)
                .isThrownBy(() -> jwtTokenProvider.getAuthentication(wrongToken));
    }

    @DisplayName("유효한 리프레시 토큰인지 체크한다.")
    @Test
    void validateRefreshToken() {
        //given
        String refreshToken = createRefreshtoken(1000000);

        //when
        boolean result = jwtTokenProvider.validateRefreshToken(refreshToken);

        assertThat(result).isTrue();
    }


    @DisplayName("만료된 리프레시 토큰이면 예외가 발생한다.")
    @Test
    void validateRefreshTokenWithTimeoutToken() throws InterruptedException {
        //given
        String refreshToken = createRefreshtoken(20000);

        //when
        Thread.sleep(20000);

        assertThatThrownBy(() -> jwtTokenProvider.validateRefreshToken(refreshToken));
    }

    @DisplayName("이메일로 리프레시 토큰을 조회한다.")
    @Test()
    void getRefreshTokenByEmail() {
        //given
        String refreshToken = createRefreshtoken(30000);

        //when
        String refreshTokenByEmail = jwtTokenProvider.getRefreshTokenByEmail(email);

        //then
        assertThat(refreshTokenByEmail).isEqualTo(refreshToken);
    }

    @DisplayName("이메일로 리프레시 토큰을 삭제한다.")
    @Test()
    void deleteRefreshTokenByEmail() {
        //given
        String refreshToken = createRefreshtoken(30000);

        //when
        jwtTokenProvider.deleteRefreshTokenByEmail(email);
        String findRefreshToken = redisTemplate.opsForValue().get(email);

        //then
        assertThat(findRefreshToken).isNull();
    }


    private String createRefreshtoken(long refreshExpireTime) {
        Authentication authentication = new TestingAuthenticationToken(email, null, role);

        String refreshToken = Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey)), SignatureAlgorithm.HS512)
                .setSubject(authentication.getName())
                .claim("AuthKey", role)
                .setExpiration(new Date((new Date()).getTime() + refreshExpireTime))    // 8
                .compact();

        redisTemplate.opsForValue().set(
                authentication.getName(),
                refreshToken,
                refreshExpireTime,
                TimeUnit.MILLISECONDS
        );
        return refreshToken;
    }
}
