package com.sparta.concurrencycontrolproject.domain.ticket.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.concurrencycontrolproject.domain.ticket.dto.response.SeatResponse;
import com.sparta.concurrencycontrolproject.domain.ticket.service.TicketService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/concerts")
public class TicketController { //이 컨트롤러에서 예매를 진행함 (Ticket 을 만든다 = 예매한다)
	private final TicketService ticketService;

	//예약 가능한 좌석 리스트
	@GetMapping("/{concertId}/seats")
	public ResponseEntity<List<SeatResponse>> getAvailableSeats(@PathVariable Long concertId) {
		List<SeatResponse> seats = ticketService.getAvailableSeats(concertId);
		return ResponseEntity.ok(seats);
	}
}
