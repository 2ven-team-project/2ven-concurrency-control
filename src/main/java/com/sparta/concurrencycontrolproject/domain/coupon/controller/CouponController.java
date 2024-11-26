package com.sparta.concurrencycontrolproject.domain.coupon.controller;

import com.sparta.concurrencycontrolproject.domain.coupon.dto.request.CouponRequestDto;
import com.sparta.concurrencycontrolproject.domain.coupon.dto.response.CouponResponseDto;
import com.sparta.concurrencycontrolproject.domain.coupon.service.CouponService;
import com.sparta.concurrencycontrolproject.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<CouponResponseDto> createCoupon(
        @RequestBody @Valid CouponRequestDto requestDto
        , @AuthenticationPrincipal UserDetailsImpl authMember) {

        return ResponseEntity.ok(couponService.createCoupon(requestDto, authMember));
    }

    @GetMapping
    public ResponseEntity<Page<CouponResponseDto>> getCoupon(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(couponService.getCoupon(page, size));
    }

    @PostMapping("/{couponId}")
    public ResponseEntity<CouponResponseDto> issueCoupon(@PathVariable Long couponId
        , @AuthenticationPrincipal UserDetailsImpl authMember) {
        return ResponseEntity.ok(couponService.issueCoupon(couponId, authMember));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<CouponResponseDto>> getMyCoupon(
        @AuthenticationPrincipal UserDetailsImpl authMember,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(couponService.getMyCoupon(authMember, page, size));
    }

}
