package com.sparta.concurrencycontrolproject.domain.coupon.service;

import com.sparta.concurrencycontrolproject.domain.coupon.dto.request.CouponRequestDto;
import com.sparta.concurrencycontrolproject.domain.coupon.dto.response.CouponResponseDto;
import com.sparta.concurrencycontrolproject.domain.coupon.entity.Coupon;
import com.sparta.concurrencycontrolproject.domain.coupon.repository.CouponRepository;
import com.sparta.concurrencycontrolproject.domain.member.entity.MemberRole;
import com.sparta.concurrencycontrolproject.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponResponseDto createCoupon(CouponRequestDto requestDto,
        UserDetailsImpl authMember) {

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

        return new CouponResponseDto(coupon);
    }

    public Page<CouponResponseDto> getCoupon(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Coupon> coupons = couponRepository.findAll(pageable);

        return coupons.map(coupon -> new CouponResponseDto(
            coupon.getId(),
            coupon.getCouponName(),
            coupon.getCount(),
            coupon.getDiscount()
        ));
    }
}
