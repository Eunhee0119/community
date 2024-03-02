package com.example.api.service.member.request;

import com.example.domain.common.Address;
import com.example.domain.member.Member;
import com.example.domain.member.RoleType;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberCreateServiceRequest {

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
    public MemberCreateServiceRequest(String email, String password, String name, String phone, int age, String city, String street, String zipcode, RoleType roleType) {
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

    public Member toEntity() {
        Address address = Address.builder()
                                    .city(city)
                                    .street(street)
                                    .zipcode(zipcode)
                                    .build();
        return Member.builder()
                .email(email)
                .password(password)
                .name(name)
                .phone(phone)
                .age(age)
                .address(address)
                .roleType(roleType)
                .build();
    }
}
