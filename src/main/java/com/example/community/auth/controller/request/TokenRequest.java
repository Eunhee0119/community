package com.example.community.auth.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenRequest {

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    String email;

    @NotBlank(message = "패스워드는 필수 입력 값입니다.")
    String password;

    @Builder
    private TokenRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
