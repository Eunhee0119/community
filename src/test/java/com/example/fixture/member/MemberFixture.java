package com.example.fixture.member;

import com.example.domain.common.Address;
import com.example.domain.member.Member;

public class MemberFixture {

    public static final String TEST_EMAIL = "newMember@test.com";
    public static final String TEST_PASSWORD = "test1234%%";
    public static final String TEST_NAME = "테스트계정";
    public static final String TEST_PHONE = "01000000000";
    public static final int TEST_AGE = 20;
    public static final String TEST_CITY = "서울";
    public static final String TEST_STREET = "테스트로 12길 11";
    public static final String TEST_ZIP_CODE = "001-01";

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
