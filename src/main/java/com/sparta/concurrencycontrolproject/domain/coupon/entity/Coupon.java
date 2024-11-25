package com.sparta.concurrencycontrolproject.domain.coupon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private int count;

    @Column
    private int discount;


    @Builder
    public Coupon(String couponName, int count, int discount) {
        this.couponName = couponName;
        this.count = count;
        this.discount = discount;
    }


}
