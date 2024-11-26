package com.sparta.concurrencycontrolproject.domain.member.dto.response;

import lombok.Getter;

@Getter
public class MemberResponse {

    private final Long id;
    private final String email;

    public MemberResponse(Long id, String email) {
        this.id = id;
        this.email = email;
    }
}