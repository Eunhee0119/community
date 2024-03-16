package com.example.community.member.service.response;

import com.example.community.common.Address;
import com.example.community.member.domain.Member;
import com.example.community.member.domain.RoleType;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberResponse {

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
    private MemberResponse(Long id, String email, String password, String name, String phone, int age, Address address, RoleType roleType) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.age = age;
        this.address = address;
        this.roleType = roleType;
    }

    public static MemberResponse of(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .password(member.getPassword())
                .name(member.getName())
                .phone(member.getPhone())
                .age(member.getAge())
                .address(member.getAddress())
                .roleType(member.getRoleType())
                .build();
    }
}
