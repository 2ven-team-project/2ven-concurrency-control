package com.sparta.concurrencycontrolproject.domain.coupon.service;

import com.sparta.concurrencycontrolproject.domain.coupon.dto.request.CouponRequestDto;
import com.sparta.concurrencycontrolproject.domain.coupon.dto.response.CouponResponseDto;
import com.sparta.concurrencycontrolproject.domain.coupon.entity.Coupon;
import com.sparta.concurrencycontrolproject.domain.coupon.entity.Issuance;
import com.sparta.concurrencycontrolproject.domain.coupon.redis.CouponRedisInitializer;
import com.sparta.concurrencycontrolproject.domain.coupon.repository.CouponRepository;
import com.sparta.concurrencycontrolproject.domain.coupon.repository.IssuanceRepository;
import com.sparta.concurrencycontrolproject.domain.member.entity.Member;
import com.sparta.concurrencycontrolproject.domain.member.entity.MemberRole;
import com.sparta.concurrencycontrolproject.domain.member.repository.MemberRepository;
import com.sparta.concurrencycontrolproject.security.UserDetailsImpl;
import com.sun.jdi.request.InvalidRequestStateException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final IssuanceRepository issuanceRepository;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private CouponRedisInitializer couponRedisInitializer;

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

        Coupon saveCoupon = couponRepository.save(coupon);

        couponRedisInitializer.initializeCouponStock(saveCoupon.getId(), requestDto.getCount());

        return new CouponResponseDto(coupon);
    }

    public Page<CouponResponseDto> getCoupon(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Coupon> coupons = couponRepository.findAll(pageable);

        return coupons.map(CouponResponseDto::new);
    }

    @Transactional
    public CouponResponseDto issueCoupon(Long couponId, UserDetailsImpl authMember) {
        Member member = memberRepository.findById(authMember.getUser().getId())
            .orElseThrow(() -> new InvalidRequestStateException("존재하는 멤버가 아닙니다"));

        String redisKey = "coupon_stock:" + couponId;

        Long stock = redisTemplate.opsForValue().decrement(redisKey);

        if (stock == null || stock < 0) {

            redisTemplate.opsForValue().increment(redisKey);
            throw new InvalidRequestStateException("쿠폰이 다 소진되었습니다.");
        }

        Coupon coupon = couponRepository.findById(couponId)
            .orElseThrow(() -> new InvalidRequestStateException("존재하는 쿠폰이 아닙니다"));

        Issuance check = issuanceRepository.findByCouponAndMember(coupon, member);

        if (check != null) {

            redisTemplate.opsForValue().increment(redisKey);
            throw new InvalidRequestStateException("발급받은 쿠폰입니다.");
        }

        Issuance issuance = Issuance.builder()
            .coupon(coupon)
            .member(member)
            .build();
        issuanceRepository.save(issuance);

        coupon.updateCount(coupon.getCount() - 1);

        return new CouponResponseDto(coupon);
    }


    public Page<CouponResponseDto> getMyCoupon(UserDetailsImpl authMember, int page, int size) {
        Member member = memberRepository.findById(authMember.getUser().getId())
            .orElseThrow(() -> new InvalidRequestStateException("존재하는 멤버가 아닙니다"));

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<CouponResponseDto> myCoupons = couponRepository.findByMember(member, pageable);

        return myCoupons;
    }
}
