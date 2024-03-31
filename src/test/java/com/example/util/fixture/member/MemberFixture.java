package com.example.util.fixture.member;

import com.example.community.common.Address;
import com.example.community.member.domain.Member;

import static com.example.util.fixture.member.MemberConstant.*;

public class MemberFixture {


    public static Member createDefaultMember() {
        return createCustomMember(TEST_EMAIL,TEST_PASSWORD,TEST_NAME,TEST_PHONE,TEST_AGE,TEST_CITY,TEST_STREET,TEST_ZIP_CODE);
    }

    public static Member createDefaultMember(String email, String password) {
        return createCustomMember(email,password,TEST_NAME,TEST_PHONE,TEST_AGE,TEST_CITY,TEST_STREET,TEST_ZIP_CODE);
    }

    public static Member createEncodedPasswordMember() {
        return createCustomMember(TEST_EMAIL,TEST_ENC_PASSWORD,TEST_NAME,TEST_PHONE,TEST_AGE,TEST_CITY,TEST_STREET,TEST_ZIP_CODE);
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
