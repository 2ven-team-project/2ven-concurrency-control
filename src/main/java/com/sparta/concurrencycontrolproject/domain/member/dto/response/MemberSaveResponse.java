package com.sparta.concurrencycontrolproject.domain.member.dto.response;

import lombok.Getter;

@Getter
public class MemberSaveResponse {

    private final String bearerToken;

    public MemberSaveResponse(String bearerToken) {
        this.bearerToken = bearerToken;
    }
}