package com.example.community.config.jwt;

import com.example.community.auth.controller.dto.TokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.example.community.common.jwt.TokenConstants.TOKEN_PREFIX;

@Slf4j
@Component
public class JwtTokenProvider implements InitializingBean {

    private static final String AUTHORITIES_KEY = "AuthKey";

    private final String secretKey;

    private final long accessExpireTime;

    public final long refreshExpireTime;

    private Key key;


    private final RedisTemplate<String, String> redisTemplate;


    public JwtTokenProvider(@Value("${security.jwt.secret-key}") String secretKey,
                            @Value("${security.jwt.token.expire-length}") long accessExpireTime,
                            @Value("${security.jwt.token.refresh-expire-length}") long refreshExpireTime,
                            RedisTemplate<String, String> redisTemplate) {
        this.secretKey = secretKey;
        this.accessExpireTime = accessExpireTime;
        this.refreshExpireTime = refreshExpireTime;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String createRefreshToken(Authentication authentication) {
        String refreshToken = createToken(authentication, refreshExpireTime);

        deleteRefreshTokenByEmail(authentication.getName());

        redisTemplate.opsForValue().set(
                authentication.getName(),
                refreshToken,
                refreshExpireTime,
                TimeUnit.MILLISECONDS
        );
        return refreshToken;
    }


    public String createAccessToken(Authentication authentication) {
        return createToken(authentication, accessExpireTime);
    }

    private String createToken(Authentication authentication, long expireTime) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        long now = (new Date()).getTime();
        Date validity = new Date(now + expireTime);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }


    public TokenDto createTokenDto(Authentication authentication) {
        return TokenDto.builder()
                .accessToken(createAccessToken(authentication))
                .reflashToken(createRefreshToken(authentication))
                .build();
    }

    public Authentication getAuthentication(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
            User principal = new User(claims.getSubject(), "", authorities);
            return new UsernamePasswordAuthenticationToken(principal, token, authorities);
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("유효하지 않은 토큰 정보입니다.");
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원하지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }


    public boolean validateRefreshToken(String refreshToken) {
        Authentication authentication = getAuthentication(refreshToken);

        String validRefreshToken = redisTemplate.opsForValue().get(authentication.getName());

        if (!refreshToken.equals(validRefreshToken)) {
            redisTemplate.delete(authentication.getName());
            return false;
        }

        return true;
    }


    public String getRefreshTokenByEmail(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    public void deleteRefreshTokenByEmail(String email) {
        redisTemplate.delete(email);
    }
}
