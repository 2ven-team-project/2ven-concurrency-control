package com.sparta.concurrencycontrolproject.domain.coupon.service;

import com.sparta.concurrencycontrolproject.domain.coupon.dto.request.CouponRequestDto;
import com.sparta.concurrencycontrolproject.domain.coupon.dto.response.CouponResponseDto;
import com.sparta.concurrencycontrolproject.domain.coupon.entity.Coupon;
import com.sparta.concurrencycontrolproject.domain.coupon.entity.Issuance;
import com.sparta.concurrencycontrolproject.domain.coupon.repository.CouponRepository;
import com.sparta.concurrencycontrolproject.domain.coupon.repository.IssuanceRepository;
import com.sparta.concurrencycontrolproject.domain.member.entity.Member;
import com.sparta.concurrencycontrolproject.domain.member.entity.MemberRole;
import com.sparta.concurrencycontrolproject.domain.member.repository.MemberRepository;
import com.sparta.concurrencycontrolproject.security.UserDetailsImpl;
import com.sun.jdi.request.InvalidRequestStateException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final IssuanceRepository issuanceRepository;
    private final MemberRepository memberRepository;
    private final RedissonClient redissonClient;


    public CouponResponseDto createCoupon(CouponRequestDto requestDto,
        UserDetailsImpl authMember) {

        if (authMember.getUser().getRole() != MemberRole.ADMIN) {
            throw new IllegalArgumentException("관리자가 아닙니다.");
        }
        Coupon coupon = Coupon.builder()
            .couponName(requestDto.getCouponName())
            .count(requestDto.getCount())
            .discount(requestDto.getDiscount())
            .build();

        Coupon saveCoupon = couponRepository.save(coupon);

        return new CouponResponseDto(coupon);
    }

    public Page<CouponResponseDto> getCoupon(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Coupon> coupons = couponRepository.findAll(pageable);

        return coupons.map(CouponResponseDto::new);
    }

//    @Transactional
//    public CouponResponseDto issueCoupon(Long couponId, UserDetailsImpl authMember) {
//
//        Member member = memberRepository.findById(authMember.getUser().getId())
//            .orElseThrow(() -> new InvalidRequestStateException("존재하는 멤버가 아닙니다"));
//        Coupon coupon = couponRepository.findById(couponId)
//            .orElseThrow(() -> new InvalidRequestStateException("존재하는 쿠폰이 아닙니다"));
//
//        Issuance check = issuanceRepository.findByCouponAndMember(coupon, member);
//
//        if (check != null) {
//            throw new InvalidRequestStateException("발급받은 쿠폰입니다.");
//        }
//        if (coupon.getCount() <= 0) {
//            throw new InvalidRequestStateException("쿠폰이 다 소진되었습니다.");
//        }
//
//        Issuance issuance = Issuance.builder()
//            .coupon(coupon)
//            .member(member)
//            .build();
//        issuanceRepository.save(issuance);
//
//        coupon.setCount(coupon.getCount() - 1);
//        coupon.update(coupon);
//
//        return new CouponResponseDto(coupon);
//    }

    @Transactional
    public CouponResponseDto issueCoupon(Long couponId, UserDetailsImpl authMember) {
        String lockKey = "coupon:" + couponId; // Unique lock key for the coupon
        RLock lock = redissonClient.getLock(lockKey);

        try {

            if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                Member member = memberRepository.findById(authMember.getUser().getId())
                    .orElseThrow(() -> new InvalidRequestStateException("존재하는 멤버가 아닙니다"));

                Coupon coupon = couponRepository.findByIdWithLock(couponId)
                    .orElseThrow(() -> new InvalidRequestStateException("존재하는 쿠폰이 아닙니다"));

                Issuance check = issuanceRepository.findByCouponAndMember(coupon, member);
                if (check != null) {
                    throw new InvalidRequestStateException("발급받은 쿠폰입니다.");
                }

                if (coupon.getCount() <= 0) {
                    throw new InvalidRequestStateException("쿠폰이 다 소진되었습니다.");
                }

                Issuance issuance = Issuance.builder()
                    .coupon(coupon)
                    .member(member)
                    .build();
                issuanceRepository.save(issuance);

                coupon.setCount(coupon.getCount() - 1);
                couponRepository.save(coupon);

                return new CouponResponseDto(coupon);
            } else {
                throw new IllegalStateException("락을 획득하지 못했습니다. 다시 시도하세요.");
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException("락 처리 중 에러가 발생했습니다.", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public Page<CouponResponseDto> getMyCoupon(UserDetailsImpl authMember, int page, int size) {
        Member member = memberRepository.findById(authMember.getUser().getId())
            .orElseThrow(() -> new InvalidRequestStateException("존재하는 멤버가 아닙니다"));

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<CouponResponseDto> myCoupons = couponRepository.findByMember(member, pageable);

        return myCoupons;
    }

    public int getMyCouponCount() {
        return issuanceRepository.findAll().size();
    }
}
