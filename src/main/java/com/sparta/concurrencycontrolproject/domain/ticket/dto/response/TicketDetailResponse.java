package com.sparta.concurrencycontrolproject.domain.ticket.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TicketDetailResponse {
    private Long ticketId;
    private String concertTitle;
    private String seatNumber;
    private LocalDateTime date;
    private boolean isCancelled;
    private String username;

    public TicketDetailResponse(Long ticketId, String concertTitle, String seatNumber, LocalDateTime date, boolean isCancelled, String username) {
        this.ticketId = ticketId;
        this.concertTitle = concertTitle;
        this.seatNumber = seatNumber;
        this.date = date;
        this.isCancelled = isCancelled;
        this.username = username;
    }

}

