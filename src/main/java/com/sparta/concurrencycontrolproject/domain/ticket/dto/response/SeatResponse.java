package com.sparta.concurrencycontrolproject.domain.ticket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeatResponse {
	private String seatNumber;
	private boolean isBooked;
}
