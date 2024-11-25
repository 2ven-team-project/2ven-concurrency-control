package com.sparta.concurrencycontrolproject.domain.auth.dto;

import com.sparta.concurrencycontrolproject.domain.member.entity.MemberRole;
import lombok.Getter;

@Getter
public class AuthMember {

    private final Long id;
    private final String email;
    private final MemberRole userRole;

    public AuthMember(Long id, String email,MemberRole userRole) {
        this.id = id;
        this.email = email;
        this.userRole = userRole;
    }
}