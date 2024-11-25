package com.sparta.concurrencycontrolproject.domain.coupon.dto.request;

import lombok.Getter;

@Getter
public class CouponRequestDto {

    private String couponName;
    private int count;
    private int discount;

}
