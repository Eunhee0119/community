package com.example.util;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = CustomMockUserSecurityContextFactory.class)
public @interface CustomMockUser {
    String email() default "test@test.com";
}
