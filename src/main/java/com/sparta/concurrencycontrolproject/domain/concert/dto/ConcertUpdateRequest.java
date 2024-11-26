package com.sparta.concurrencycontrolproject.domain.concert.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ConcertUpdateRequest {
    private String concertName;
    private int price;
    private String description;
    private String image;
    private LocalDateTime date;
    private int seating;
}
