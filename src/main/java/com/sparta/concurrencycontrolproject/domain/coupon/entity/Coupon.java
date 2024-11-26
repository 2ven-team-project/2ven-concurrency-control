package com.sparta.concurrencycontrolproject.domain.coupon.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String couponName;

    @Column
    @Setter
    private int count;

    @Column
    private int discount;


    @Builder
    public Coupon(String couponName, int count, int discount) {
        this.couponName = couponName;
        this.count = count;
        this.discount = discount;
    }

    public void update(Coupon coupon) {
        this.couponName = coupon.getCouponName();
        this.count = coupon.getCount();
        this.discount = coupon.getDiscount();
    }
}
