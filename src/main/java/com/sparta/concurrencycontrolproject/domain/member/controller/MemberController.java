package com.sparta.concurrencycontrolproject.domain.member.controller;

import com.sparta.concurrencycontrolproject.domain.auth.dto.AuthMember;
import com.sparta.concurrencycontrolproject.domain.member.dto.request.MemberChangePasswordRequest;
import com.sparta.concurrencycontrolproject.domain.member.dto.request.MemberDeleteRequest;
import com.sparta.concurrencycontrolproject.domain.member.dto.response.MemberDeleteResponse;
import com.sparta.concurrencycontrolproject.domain.member.dto.response.MemberResponse;
import com.sparta.concurrencycontrolproject.domain.member.service.MemberService;
import com.sparta.concurrencycontrolproject.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/{memberId}")
    public ResponseEntity<MemberResponse> getMember(@PathVariable long memberId) {
        return ResponseEntity.ok(memberService.getMember(memberId));
    }

    @PutMapping("/members")
    public void changePassword(@AuthenticationPrincipal AuthMember authMember, @RequestBody MemberChangePasswordRequest memberChangePasswordRequest) {
        memberService.changePassword(authMember.getId(), memberChangePasswordRequest);
    }

    @DeleteMapping("/members/{memberId}")
    public ResponseEntity<MemberDeleteResponse> deleteMember(@RequestBody MemberDeleteRequest memberDeleteRequest, @PathVariable long memberId, @AuthenticationPrincipal UserDetailsImpl authUser) {
        return ResponseEntity.ok(memberService.deleteMember(memberDeleteRequest, memberId, authUser));
    }
}