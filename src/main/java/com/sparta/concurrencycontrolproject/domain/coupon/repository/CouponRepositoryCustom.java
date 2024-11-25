package com.sparta.concurrencycontrolproject.domain.coupon.repository;

import com.sparta.concurrencycontrolproject.domain.coupon.dto.response.CouponResponseDto;
import com.sparta.concurrencycontrolproject.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponRepositoryCustom {


    Page<CouponResponseDto> findByMember(Member member, Pageable pageable);
}
