package com.sparta.concurrencycontrolproject.domain.auth.service;

import com.sparta.concurrencycontrolproject.config.JwtUtil;
import com.sparta.concurrencycontrolproject.domain.auth.dto.SignupRequest;
import com.sparta.concurrencycontrolproject.domain.auth.dto.SignupResponse;
import com.sparta.concurrencycontrolproject.domain.member.entity.Member;
import com.sparta.concurrencycontrolproject.domain.member.entity.MemberRole;
import com.sparta.concurrencycontrolproject.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {

        if (memberRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        MemberRole memberRole = MemberRole.of(signupRequest.getMemberRole());

        Member newUser = new Member(
                signupRequest.getEmail(),
                signupRequest.getName(),
                encodedPassword,
                memberRole
        );
        Member savedUser = memberRepository.save(newUser);

        String bearerToken = jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), memberRole);

        return new SignupResponse(bearerToken);
    }

}