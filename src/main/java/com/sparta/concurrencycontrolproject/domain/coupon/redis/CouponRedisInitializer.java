package com.sparta.concurrencycontrolproject.domain.coupon.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CouponRedisInitializer {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void initializeCouponStock(Long couponId, int stock) {
        String key = "coupon_stock:" + couponId;
        redisTemplate.opsForValue().set(key, stock);
    }
}
