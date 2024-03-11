package com.example.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.example.common.jwt.TokenConstants.TOKEN_HEADER;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request.getHeader(TOKEN_HEADER));
        try {
            if (token != null) {
                if (jwtTokenProvider.validateToken(token)) {
                    Authentication auth = jwtTokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    Cookie[] cookies = request.getCookies();
                    String tokenByCookie = "";
                    for (Cookie cookie : cookies) {
                        tokenByCookie = cookie.getValue();
                    }
                    String refreshToken = jwtTokenProvider.resolveToken(tokenByCookie);
                    if (jwtTokenProvider.validateRefreshToken(refreshToken)) {
                        Authentication refreshAuth = jwtTokenProvider.getAuthentication(refreshToken);
                        SecurityContextHolder.getContext().setAuthentication(refreshAuth);
                        response.addHeader(TOKEN_HEADER, jwtTokenProvider.createAccessToken(refreshAuth));
                    }
                }
            }
        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
            return;
        }
        filterChain.doFilter(request, response);
    }
}
