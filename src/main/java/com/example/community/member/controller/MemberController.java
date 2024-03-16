package com.example.community.member.controller;

import com.example.community.ApiResponse;
import com.example.community.member.controller.request.MemberCreateRequest;
import com.example.community.member.service.MemberService;
import com.example.community.member.service.request.MemberCreateServiceRequest;
import com.example.community.member.service.response.MemberResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/api/members/new")
    public ApiResponse<MemberResponse> createMember(@Valid @RequestBody MemberCreateRequest memberCreateRequest){
        MemberCreateServiceRequest serviceRequest = memberCreateRequest.toServiceRequest();
        return ApiResponse.ok(memberService.createMember(serviceRequest));
    }

    @GetMapping("/api/members/1")
    public ApiResponse<MemberResponse> createMember(){
        return ApiResponse.ok(null);
    }

}
