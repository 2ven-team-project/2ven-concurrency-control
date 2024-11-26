package com.sparta.concurrencycontrolproject.domain.ticket.dto.request;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class TicketingRequest {
	private String seatNumber;
	private LocalDateTime date;
}
