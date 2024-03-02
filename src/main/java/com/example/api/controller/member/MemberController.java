package com.example.api.controller.member;

import com.example.api.ApiResponse;
import com.example.api.controller.member.request.MemberCreateRequest;
import com.example.api.service.member.MemberService;
import com.example.api.service.member.request.MemberCreateServiceRequest;
import com.example.api.service.member.response.MemberResponse;
import jakarta.validation.Valid;
import lombok.Getter;
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
