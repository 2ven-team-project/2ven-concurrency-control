package com.sparta.concurrencycontrolproject.domain.coupon.repository;

import com.sparta.concurrencycontrolproject.domain.coupon.entity.Coupon;
import com.sparta.concurrencycontrolproject.domain.coupon.entity.Issuance;
import com.sparta.concurrencycontrolproject.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssuanceRepository extends JpaRepository<Issuance, Long> {

    Issuance findByCouponAndMember(Coupon coupon, Member member);

    Long countByCoupon(Coupon coupon);
}
