package com.example.api.service.auth;

import com.example.api.controller.auth.dto.TokenDto;
import com.example.api.controller.auth.request.TokenRequest;
import com.example.config.jwt.JwtTokenProvider;
import com.example.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final MemberRepository memberRepository;


    public TokenDto getToken(TokenRequest tokenRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(tokenRequest.getEmail(), tokenRequest.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        TokenDto tokenDto = jwtTokenProvider.createTokenDto(authentication);

        return tokenDto;
    }

    public TokenDto regenerateToken(String refreshToken) {
        jwtTokenProvider.validateToken(refreshToken);

        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);

        String validRefreshToken = jwtTokenProvider.getRefreshTokenByEmail(authentication.getName());

        if (!refreshToken.equals(validRefreshToken)) {
            jwtTokenProvider.deleteRefreshTokenByEmail(authentication.getName());
            throw new IllegalArgumentException("유효하지 않은 접근입니다.");
        }

        TokenDto tokenDto = jwtTokenProvider.createTokenDto(authentication);

        return tokenDto;
    }
}
