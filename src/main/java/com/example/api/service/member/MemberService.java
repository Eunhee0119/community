package com.example.api.service.member;

import com.example.api.service.member.request.MemberCreateServiceRequest;
import com.example.api.service.member.response.MemberResponse;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberResponse createMember(MemberCreateServiceRequest memberCreateRequest) {
        Member member = memberCreateRequest.toEntity();
        Member createdMember = memberRepository.save(member);

        return MemberResponse.of(createdMember);
    }
}
