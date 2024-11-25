package com.sparta.concurrencycontrolproject.domain.coupon.dto.response;

import com.sparta.concurrencycontrolproject.domain.coupon.entity.Coupon;
import lombok.Getter;

@Getter
public class CouponResponseDto {

    private final Long Id;
    private final String couponName;
    private final int count;
    private final int discount;

    public CouponResponseDto(Coupon coupon) {
        this.Id = coupon.getId();
        this.couponName = coupon.getCouponName();
        this.count = coupon.getCount();
        this.discount = coupon.getDiscount();
    }

    public CouponResponseDto(Long id, String couponName, int count, int discount) {
        this.Id = id;
        this.couponName = couponName;
        this.count = count;
        this.discount = discount;
    }
}
