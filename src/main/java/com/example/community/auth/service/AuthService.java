package com.example.community.auth.service;

import com.example.community.auth.controller.dto.TokenDto;
import com.example.community.auth.controller.request.TokenRequest;
import com.example.community.auth.exception.InvalidTokenException;
import com.example.community.config.jwt.JwtTokenProvider;
import com.example.community.member.repository.MemberRepository;
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


    @Transactional
    public TokenDto getToken(TokenRequest tokenRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(tokenRequest.getEmail(), tokenRequest.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        TokenDto tokenDto = jwtTokenProvider.createTokenDto(authentication);

        return tokenDto;
    }


    @Transactional
    public TokenDto regenerateToken(String refreshToken) {
        jwtTokenProvider.validateToken(refreshToken);

        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);

        String validRefreshToken = jwtTokenProvider.getRefreshTokenByEmail(authentication.getName());

        if (!refreshToken.equals(validRefreshToken)) {
            jwtTokenProvider.deleteRefreshTokenByEmail(authentication.getName());
            throw new InvalidTokenException("유효하지 않은 접근입니다.");
        }

        TokenDto tokenDto = jwtTokenProvider.createTokenDto(authentication);

        return tokenDto;
    }
}
