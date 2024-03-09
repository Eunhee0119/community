package com.example.domain.member;

import com.example.domain.common.Address;
import com.example.domain.common.BaseEntity;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

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

    @Builder
    public Member(String email, String password, String name, String phone, int age, Address address, RoleType roleType) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.age = age;
        this.address = address;
        this.roleType = roleType != null ? roleType : RoleType.MEMBER;
    }

}
