package com.example.community.auth.controller;

import com.example.community.ApiResponse;
import com.example.community.auth.controller.dto.TokenDto;
import com.example.community.auth.controller.request.TokenRequest;
import com.example.community.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.community.common.jwt.TokenConstants.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/authentication")
    public ApiResponse<TokenDto> getAuthentication(@Valid @RequestBody TokenRequest tokenRequest, HttpServletResponse res) {
        TokenDto tokenDto = authService.getToken(tokenRequest);

        // refresh token 보안을 위해 쿠키에 담고 http only 설정
        Cookie cookie = setRefreshTokenCookie(tokenDto.getRefreshToken());

        res.addCookie(cookie);
        res.setHeader(TOKEN_HEADER, TOKEN_PREFIX + tokenDto.getAccessToken());
        return ApiResponse.ok(null);
    }


    @PostMapping("/regeneratetoken")
    public ApiResponse regenerateToken(HttpServletRequest req, HttpServletResponse res) throws BadRequestException {
        Cookie[] cookies = req.getCookies();
        String refreshToken = "";
        for (Cookie cookie : cookies) {
            refreshToken = cookie.getValue();
        }

        TokenDto tokenDto = authService.regenerateToken(refreshToken);
        Cookie cookie = setRefreshTokenCookie(tokenDto.getRefreshToken());

        res.addCookie(cookie);
        res.setHeader(TOKEN_HEADER, TOKEN_PREFIX + tokenDto.getAccessToken());
        return ApiResponse.ok(null);
    }


    private Cookie setRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie(
                REFRESH_TOKEN_COOKIE,
                refreshToken
        );
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(Integer.MAX_VALUE);
        return cookie;
    }
}
