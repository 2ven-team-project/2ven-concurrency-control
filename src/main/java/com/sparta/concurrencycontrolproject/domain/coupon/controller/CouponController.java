package com.sparta.concurrencycontrolproject.domain.coupon.controller;

import com.sparta.concurrencycontrolproject.domain.coupon.dto.request.CouponRequestDto;
import com.sparta.concurrencycontrolproject.domain.coupon.service.CouponService;
import com.sparta.concurrencycontrolproject.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    public String createCoupon(
        @RequestBody @Valid CouponRequestDto requestDto
        , @AuthenticationPrincipal UserDetailsImpl authMember) {

        return couponService.createCoupon(requestDto, authMember);
    }

}
