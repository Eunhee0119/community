package com.example.domain.member;

import com.example.domain.common.Address;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;

import java.util.regex.Pattern;

@Entity
@Getter
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;

    private String name;
    private String phone;
    private int age;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;


    private static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$";

    @Builder
    public Member(String email, String password, String name, String phone, int age, Address address, RoleType roleType) {
        validPassword(password);
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.age = age;
        this.address = address;
        this.roleType = roleType != null ? roleType : RoleType.MEMBER;
    }

    private void validPassword(String password) {
        if(!Pattern.matches(PASSWORD_PATTERN, password))
            throw new IllegalArgumentException("비밀번호는 영문, 숫자, 특수문자를 각각 한개 이상 포함한 8~16자 이내여야합니다.");
    }

}
