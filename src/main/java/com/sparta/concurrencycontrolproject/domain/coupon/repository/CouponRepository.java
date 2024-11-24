package com.sparta.concurrencycontrolproject.domain.coupon.repository;

import com.sparta.concurrencycontrolproject.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    
}
