package com.sparta.concurrencycontrolproject.domain.auth.dto;

import lombok.Getter;

@Getter
public class SigninResponse {

    private final String bearerToken;

    public SigninResponse(String bearerToken) {
        this.bearerToken = bearerToken;
    }
}