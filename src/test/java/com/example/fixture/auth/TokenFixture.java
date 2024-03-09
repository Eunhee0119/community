package com.example.fixture.auth;

import com.example.api.controller.auth.request.TokenRequest;

import static com.example.fixture.member.MemberConstant.TEST_EMAIL;
import static com.example.fixture.member.MemberConstant.TEST_PASSWORD;

public class TokenFixture {

    public static TokenRequest createDefaultTokenRequest() {
        return createTokenRequest(TEST_EMAIL, TEST_PASSWORD);
    }

    public static TokenRequest createTokenRequest(String email, String password) {
        return TokenRequest.builder()
                .email(email)
                .password(password)
                .build();
    }

}
