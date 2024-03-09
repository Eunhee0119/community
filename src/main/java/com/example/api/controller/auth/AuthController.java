package com.example.api.controller.auth;

import com.example.api.ApiResponse;
import com.example.api.controller.auth.dto.TokenDto;
import com.example.api.controller.auth.request.TokenRequest;
import com.example.api.service.auth.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.example.common.jwt.TokenConstants.TOKEN_HEADER;
import static com.example.common.jwt.TokenConstants.TOKEN_PREFIX;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/api/authentication")
    public ApiResponse<TokenDto> getAuthentication(@Valid @RequestBody TokenRequest tokenRequest, HttpServletResponse response) {
        TokenDto tokenDto = authService.getToken(tokenRequest);
        Cookie cookie = new Cookie(
                "access_token",
                tokenDto.getAccessToken()
        );
        cookie.setPath("/");
        cookie.setMaxAge(Integer.MAX_VALUE);
        response.addCookie(cookie);
        response.setHeader(TOKEN_HEADER,TOKEN_PREFIX + tokenDto.getAccessToken());
        return ApiResponse.ok(tokenDto);
    }


}
