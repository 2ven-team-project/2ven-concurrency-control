package com.sparta.concurrencycontrolproject.domain.concert.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ConcertRequest {
    private String concertName;
    private int price;
    private String description;
    private String image;
    private LocalDateTime date;
    private LocalDateTime startAt;
    private int seating; // 좌석 수
}
