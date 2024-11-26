package com.sparta.concurrencycontrolproject.domain.ticket.dto.response;


import lombok.Getter;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;

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

