package com.sparta.concurrencycontrolproject.domain.member.controller;

import com.sparta.concurrencycontrolproject.domain.member.dto.request.MemberChangePasswordRequest;
import com.sparta.concurrencycontrolproject.domain.member.dto.request.MemberDeleteRequest;
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
    public ResponseEntity<MemberResponse> getMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberService.getMember(memberId));
    }

    @PutMapping("/members")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody MemberChangePasswordRequest memberChangePasswordRequest) {
        memberService.changePassword(userDetails.getMember().getId(), memberChangePasswordRequest);
        return ResponseEntity.ok("비밀번호 변경이 완료되었습니다.");
    }

    @DeleteMapping("/members/{memberId}")
    public ResponseEntity<String> deleteMember(@RequestBody MemberDeleteRequest memberDeleteRequest, @PathVariable Long memberId, @AuthenticationPrincipal UserDetailsImpl authMember) {
        memberService.deleteMember(memberDeleteRequest, memberId, authMember);
        return ResponseEntity.ok("탈퇴 성공");
    }
}