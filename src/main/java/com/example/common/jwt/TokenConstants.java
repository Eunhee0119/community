package com.example.common.jwt;

import org.springframework.http.HttpHeaders;

public class TokenConstants {

    public static final String TOKEN_HEADER = HttpHeaders.AUTHORIZATION;

    public static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String TOKEN_TYPE = "JWT";

}
