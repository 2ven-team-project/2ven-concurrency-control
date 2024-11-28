package com.sparta.concurrencycontrolproject.domain.ticket.lock.util;

public class RedisKeyUtil {

    public static String generateSeatLockKey(Long concertId, String seatNumber) {
        return String.format("CONCERT-%d-SEAT-%s", concertId, seatNumber);
    }

}
