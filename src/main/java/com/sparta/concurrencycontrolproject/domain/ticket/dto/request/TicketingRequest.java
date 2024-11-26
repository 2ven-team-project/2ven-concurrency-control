package com.sparta.concurrencycontrolproject.domain.ticket.dto.request;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TicketingRequest {
	private String seatNumber;
	private LocalDateTime date;
}
