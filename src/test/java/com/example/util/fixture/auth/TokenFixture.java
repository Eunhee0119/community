package com.example.util.fixture.auth;

import com.example.community.auth.controller.request.TokenRequest;
import com.example.util.fixture.member.MemberConstant;

public class TokenFixture {

    public static TokenRequest createDefaultTokenRequest() {
        return createTokenRequest(MemberConstant.TEST_EMAIL, MemberConstant.TEST_PASSWORD);
    }

    public static TokenRequest createTokenRequest(String email, String password) {
        return TokenRequest.builder()
                .email(email)
                .password(password)
                .build();
    }

}
