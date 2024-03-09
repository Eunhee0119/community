package com.example.api.service.member;

import com.example.api.service.member.request.MemberCreateServiceRequest;
import com.example.api.service.member.response.MemberResponse;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;


    private final PasswordEncoder bCryptPasswordEncoder;

    public MemberResponse createMember(MemberCreateServiceRequest memberCreateRequest) {
        Member member = memberCreateRequest.toEntity(bCryptPasswordEncoder.encode(memberCreateRequest.getPassword()));
        Member createdMember = memberRepository.save(member);

        return MemberResponse.of(createdMember);
    }
}
