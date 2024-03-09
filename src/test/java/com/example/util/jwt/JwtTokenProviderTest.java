package com.example.util.jwt;

import com.example.config.jwt.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@Transactional
class JwtTokenProviderTest {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Value("${security.jwt.secret-key}")
    private String secretKey;


    @DisplayName("")
    @Test
    void resolveToken() {
    }

    @DisplayName("회원 정보를 담은 JWT 토큰을 생성한다.")
    @Test
    void createToken() {
        //given
        Authentication authentication = new TestingAuthenticationToken("test1@gmail.com", null, "ROLE_MEMBER");

        // when
        String token = jwtTokenProvider.createAccessToken(authentication);

        // then
        assertThat(token).isNotBlank();
    }

    @DisplayName("토큰 정보로 authentication을 조회한다.")
    @Test
    void getAuthentication() {
        //given
        String email = "test1@gmail.com";
        String role = "ROLE_USER";
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
                .setExpiration(new Date((new Date()).getTime() - 1))	// 8
                .compact();

        //when //then
        assertThatExceptionOfType(ExpiredJwtException.class)
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
        assertThatExceptionOfType(ExpiredJwtException.class)
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
}
