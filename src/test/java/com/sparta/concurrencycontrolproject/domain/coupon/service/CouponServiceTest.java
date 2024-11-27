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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CouponServiceTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private IssuanceRepository issuanceRepository; // 주입 추가

    @Test
    public void testIssueCouponConcurrency() throws InterruptedException {
        // Given
        Coupon coupon = Coupon.builder()
            .couponName("Test Coupon")
            .count(100)
            .discount(10)
            .build();
        couponRepository.save(coupon);

        int threadCount = 150; // 150 threads trying to issue coupons
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // When
        for (int i = 0; i < threadCount; i++) {
            final int index = i;  // Use index to create a unique email and member
            executorService.submit(() -> {
                try {
                    // Create a new member with a unique email for each thread
                    Member member = new Member(
                        "test" + index + "@example.com",  // Unique Email
                        "Test Member " + index,           // Unique Name
                        "password",                       // Password
                        MemberRole.USER                   // Role
                    );
                    memberRepository.save(member);

                    couponService.issueCoupon(coupon.getId(), new UserDetailsImpl(member));
                    System.out.println(
                        "쿠폰 발급 성공: Thread " + Thread.currentThread().getId() + " Member "
                            + member.getEmail());
                } catch (Exception e) {
                    System.out.println("쿠폰 발급 실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await(); // Wait for all threads to complete
        executorService.shutdown();

        // Then
        Coupon updatedCoupon = couponRepository.findById(coupon.getId()).orElseThrow();
        System.out.println("최종 남은 쿠폰 수: " + updatedCoupon.getCount());
        System.out.println("발급된 쿠폰 수: " + issuanceRepository.findAll().size());

        assertThat(updatedCoupon.getCount()).isEqualTo(0); // Ensure no coupons are left
        assertThat(issuanceRepository.findAll().size()).isEqualTo(
            100); // Ensure only 100 coupons were issued
    }
}
