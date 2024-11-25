package com.sparta.concurrencycontrolproject.domain.member.service;

import com.sparta.concurrencycontrolproject.domain.member.dto.request.MemberRoleChangeRequest;
import com.sparta.concurrencycontrolproject.domain.member.entity.Member;
import com.sparta.concurrencycontrolproject.domain.member.entity.MemberRole;
import com.sparta.concurrencycontrolproject.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberAdminService {

    private final MemberRepository memberRepository;

    @Transactional
    public void changeMemberRole(long memberId, MemberRoleChangeRequest memberRoleChangeRequest) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("User not found"));
        member.updateRole(MemberRole.of(memberRoleChangeRequest.getRole()));
    }
}