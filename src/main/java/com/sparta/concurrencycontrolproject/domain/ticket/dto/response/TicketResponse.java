package com.sparta.concurrencycontrolproject.domain.ticket.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TicketResponse {
    private String concertTitle;
    private String bookedBy;
    private String seatNumber;
    private LocalDateTime date;
    private boolean isCancelled;

    public TicketResponse(String concertTitle, String seatNumber, LocalDateTime date) {
        this.concertTitle = concertTitle;
        this.seatNumber = seatNumber;
        this.date = date;
    }

}

