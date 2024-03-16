package com.example.community.member.service;

import com.example.community.member.service.response.MemberResponse;
import com.example.community.member.service.request.MemberCreateServiceRequest;
import com.example.community.member.domain.Member;
import com.example.community.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberResponse createMember(MemberCreateServiceRequest memberCreateRequest) {
        Member member = memberCreateRequest.toEntity(passwordEncoder.encode(memberCreateRequest.getPassword()));
        Member createdMember = memberRepository.save(member);

        return MemberResponse.of(createdMember);
    }
}
