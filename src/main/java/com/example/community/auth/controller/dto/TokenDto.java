package com.example.community.auth.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenDto {
    private String accessToken;
    private String refreshToken;

    @Builder
    private TokenDto(String accessToken, String reflashToken) {
        this.accessToken = accessToken;
        this.refreshToken = reflashToken;
    }
}
