package com.sparta.concurrencycontrolproject.domain.ticket.dto.response;

import lombok.Getter;


import java.time.LocalDateTime;

@Getter
public class TicketResponse {

    private final Long id;
    private final String concertName;
    private final String seatNumber;
    private final LocalDateTime date;


    public TicketResponse(Long id, String concertName, String seatNumber, LocalDateTime date) {
        this.id = id;
        this.concertName = concertName;
        this.seatNumber = seatNumber;
        this.date = date;
    }
}
