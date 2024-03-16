package com.example.community.member.service.request;

import com.example.community.common.Address;
import com.example.community.member.domain.Member;
import com.example.community.member.domain.RoleType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;

import java.util.regex.Pattern;

@Getter
public class MemberCreateServiceRequest {

    private static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$";


    private String email;
    private String password;

    private String name;
    private String phone;
    private int age;

    private String city;
    private String street;
    private String zipcode;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;


    @Builder
    private MemberCreateServiceRequest(String email, String password, String name, String phone, int age, String city, String street, String zipcode, RoleType roleType) {
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

    public Member toEntity(String encodedPassword) {
        validPassword(password);

        Address address = Address.builder()
                .city(city)
                .street(street)
                .zipcode(zipcode)
                .build();
        return Member.builder()
                .email(email)
                .password(encodedPassword)
                .name(name)
                .phone(phone)
                .age(age)
                .address(address)
                .roleType(roleType)
                .build();
    }


    public static boolean validPassword(String password) {
        if(!Pattern.matches(PASSWORD_PATTERN, password))
            throw new IllegalArgumentException("비밀번호는 영문, 숫자, 특수문자를 각각 한개 이상 포함한 8~16자 이내여야합니다.");
        return true;
    }

}
