package com.sparta.concurrencycontrolproject.domain.auth.controller;

import com.sparta.concurrencycontrolproject.domain.auth.dto.SignupRequest;
import com.sparta.concurrencycontrolproject.domain.auth.dto.SignupResponse;
import com.sparta.concurrencycontrolproject.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/signup")
    public SignupResponse signup(@Valid @RequestBody SignupRequest signupRequest) {
        return authService.signup(signupRequest);
    }

}
