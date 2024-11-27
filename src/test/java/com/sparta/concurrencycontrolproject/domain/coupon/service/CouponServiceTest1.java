package com.sparta.concurrencycontrolproject.domain.coupon.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.sparta.concurrencycontrolproject.domain.coupon.entity.Coupon;
import com.sparta.concurrencycontrolproject.domain.coupon.repository.CouponRepository;
import com.sparta.concurrencycontrolproject.domain.coupon.repository.IssuanceRepository;
import com.sparta.concurrencycontrolproject.domain.member.entity.Member;
import com.sparta.concurrencycontrolproject.domain.member.entity.MemberRole;
import com.sparta.concurrencycontrolproject.domain.member.repository.MemberRepository;
import com.sparta.concurrencycontrolproject.security.UserDetailsImpl;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CouponServiceTest1 {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private IssuanceRepository issuanceRepository;

    @Test
    public void testIssueCouponConcurrency() throws InterruptedException {
        // Given
        Coupon coupon = Coupon.builder()
            .couponName("Test Coupon")
            .count(100)  // 총 100개의 쿠폰
            .discount(10)
            .build();
        couponRepository.save(coupon);

        int threadCount = 150;  // 동시 150개의 요청
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // When
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    Member member = new Member(
                        "test" + index + "@example.com",
                        "Test Member " + index,
                        "password",
                        MemberRole.USER
                    );
                    memberRepository.save(member);

                    couponService.issueCoupon(coupon.getId(), new UserDetailsImpl(member));
                    System.out.println(
                        "쿠폰 발급 성공: Thread " + Thread.currentThread().getId() + " Member "
                            + member.getEmail());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    System.out.println("쿠폰 발급 실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        // Then
        Coupon updatedCoupon = couponRepository.findById(coupon.getId()).orElseThrow();
        Long issuedCoupons = issuanceRepository.countByCoupon(coupon);

        System.out.println("발급 성공 쿠폰: " + successCount.get());
        System.out.println("발급 실패 쿠폰: " + failureCount.get());
        System.out.println("남은 쿠폰: " + updatedCoupon.getCount());
        System.out.println("총 발급 쿠폰: " + issuedCoupons);

        assertThat(updatedCoupon.getCount()).isEqualTo(0);
        assertThat(issuedCoupons).isEqualTo(100);
    }
}