package com.sparta.concurrencycontrolproject.domain.concert.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ConcertResponse {
    private Long id;
    private String concertName;
    private int price;
    private String description;
    private String image;
    private LocalDateTime date;
    private LocalDateTime startAt;
    private int seating; // 좌석 수
}
