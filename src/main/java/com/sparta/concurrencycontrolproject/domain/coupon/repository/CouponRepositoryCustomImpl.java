package com.sparta.concurrencycontrolproject.domain.coupon.repository;


import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.concurrencycontrolproject.domain.coupon.dto.response.CouponResponseDto;
import com.sparta.concurrencycontrolproject.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sparta.concurrencycontrolproject.domain.coupon.entity.QCoupon.coupon;
import static com.sparta.concurrencycontrolproject.domain.coupon.entity.QIssuance.issuance;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryCustomImpl implements CouponRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CouponResponseDto> findByMember(Member member, Pageable pageable) {

        List<CouponResponseDto> result = queryFactory
            .select(Projections.constructor(CouponResponseDto.class,
                coupon.id,
                coupon.couponName,
                coupon.count,
                coupon.discount
            ))
            .from(coupon)
            .join(issuance).on(coupon.id.eq(issuance.coupon.id))
            .where(issuance.member.id.eq(member.getId()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .select(coupon.count())
            .from(coupon)
            .join(issuance).on(coupon.id.eq(issuance.coupon.id))
            .where(issuance.member.id.eq(member.getId()))
            .fetchOne();

        return new PageImpl<>(result, pageable, total);

    }

}
