package com.sparta.concurrencycontrolproject.domain.coupon.dto.request;

import lombok.Getter;

@Getter
public class CouponRequestDto {

    String couponName;
    int count;
    int discount;


}
