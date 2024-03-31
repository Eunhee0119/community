package com.example.community.member.controller.request;

import com.example.community.member.domain.RoleType;
import com.example.community.member.service.request.MemberCreateServiceRequest;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberCreateRequest {

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @NotBlank(message = "패스워드는 필수 입력 값입니다.")
    private String password;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @Size(min = 11, max = 11)
    private String phone;

    @Positive(message = "나이는 필수 입력 값입니다.")

    private int age;

    private String city;
    private String street;
    private String zipcode;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Builder
    private MemberCreateRequest(String email, String password, String name, String phone, int age, String city, String street, String zipcode, RoleType roleType) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.age = age;
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
        this.roleType = roleType;
    }

    public MemberCreateServiceRequest toServiceRequest() {
        return MemberCreateServiceRequest.builder()
                .email(email)
                .password(password)
                .name(name)
                .phone(phone)
                .age(age)
                .city(city)
                .street(street)
                .zipcode(zipcode)
                .roleType(roleType).build();
    }
}
