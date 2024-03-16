package com.example.community.member.domain;

import com.example.community.common.Address;
import com.example.community.common.BaseEntity;
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
    private Member(String email, String password, String name, String phone, int age, Address address, RoleType roleType) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.age = age;
        this.address = address;
        this.roleType = roleType != null ? roleType : RoleType.MEMBER;
    }

}
