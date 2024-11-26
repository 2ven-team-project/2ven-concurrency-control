package com.sparta.concurrencycontrolproject.domain.member.service;

import com.sparta.concurrencycontrolproject.domain.member.dto.request.MemberChangePasswordRequest;
import com.sparta.concurrencycontrolproject.domain.member.dto.request.MemberDeleteRequest;
import com.sparta.concurrencycontrolproject.domain.member.dto.response.MemberDeleteResponse;
import com.sparta.concurrencycontrolproject.domain.member.dto.response.MemberResponse;
import com.sparta.concurrencycontrolproject.domain.member.entity.Member;
import com.sparta.concurrencycontrolproject.domain.member.repository.MemberRepository;
import com.sparta.concurrencycontrolproject.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberResponse getMember(long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return new MemberResponse(member.getId(), member.getEmail());
    }

    @Transactional
    public void changePassword(long memberId, MemberChangePasswordRequest memberChangePasswordRequest) {
        validateNewPassword(memberChangePasswordRequest);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (passwordEncoder.matches(memberChangePasswordRequest.getNewPassword(), member.getPassword())) {
            throw new RuntimeException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
        }

        if (!passwordEncoder.matches(memberChangePasswordRequest.getOldPassword(), member.getPassword())) {
            throw new RuntimeException("잘못된 비밀번호입니다.");
        }

        member.changePassword(passwordEncoder.encode(memberChangePasswordRequest.getNewPassword()));
    }

    private static void validateNewPassword(MemberChangePasswordRequest memberChangePasswordRequest) {
        if (memberChangePasswordRequest.getNewPassword().length() < 8 ||
                !memberChangePasswordRequest.getNewPassword().matches(".*\\d.*") ||
                !memberChangePasswordRequest.getNewPassword().matches(".*[A-Z].*")) {
            throw new RuntimeException("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.");
        }
    }

    public MemberDeleteResponse deleteMember(MemberDeleteRequest memberDeleteRequest, Long memberId, UserDetailsImpl authMember) {
        Member member = memberRepository.findByEmail(authMember.getMember().getEmail()).orElseThrow(
                () -> new IllegalArgumentException("User not found"));
        if(!member.getId().equals(memberId) || !authMember.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("User not found");
        }
        if(!member.getPassword().equals(passwordEncoder.encode(memberDeleteRequest.getPassword()))) {
            throw new IllegalArgumentException("User not found");
        }
        memberRepository.save(member);

        return new MemberDeleteResponse(member.getId());
    }
}