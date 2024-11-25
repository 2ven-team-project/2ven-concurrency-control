package com.sparta.concurrencycontrolproject.domain.coupon.service;

import com.sparta.concurrencycontrolproject.domain.coupon.dto.request.CouponRequestDto;
import com.sparta.concurrencycontrolproject.domain.coupon.entity.Coupon;
import com.sparta.concurrencycontrolproject.domain.coupon.repository.CouponRepository;
import com.sparta.concurrencycontrolproject.domain.member.entity.MemberRole;
import com.sparta.concurrencycontrolproject.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public String createCoupon(CouponRequestDto requestDto, UserDetailsImpl authMember) {

        System.out.println(authMember.getUser().getRole());

        if (authMember.getUser().getRole() != MemberRole.ADMIN) {
            throw new IllegalArgumentException("관리자가 아닙니다.");
        }
        Coupon coupon = Coupon.builder()
            .couponName(requestDto.getCouponName())
            .count(requestDto.getCount())
            .discount(requestDto.getDiscount())
            .build();
        couponRepository.save(coupon);

        return "쿠폰 등록 완료";
    }
}
