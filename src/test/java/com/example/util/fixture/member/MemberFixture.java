package com.example.util.fixture.member;

import com.example.community.common.Address;
import com.example.community.member.domain.Member;

import static com.example.util.fixture.member.MemberConstant.*;

public class MemberFixture {


    public static Member createDefaultMember() {
        Address address = new Address(TEST_CITY, TEST_STREET, TEST_ZIP_CODE);
        return Member.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .name(TEST_NAME)
                .phone(TEST_PHONE)
                .age(TEST_AGE)
                .address(address)
                .build();
    }

    public static Member createEncodedPasswordMember() {
        Address address = new Address(TEST_CITY, TEST_STREET, TEST_ZIP_CODE);
        return Member.builder()
                .email(TEST_EMAIL)
                .password(TEST_ENC_PASSWORD)
                .name(TEST_NAME)
                .phone(TEST_PHONE)
                .age(TEST_AGE)
                .address(address)
                .build();
    }

    public static Member createCustomMember(String email, String password, String name, String phone, int age
                                                        , String city, String street, String zipCode) {
        Address address = new Address(city, street, zipCode);
        return Member.builder()
                .email(email)
                .password(password)
                .name(name)
                .phone(phone)
                .age(age)
                .address(address)
                .build();
    }
}
