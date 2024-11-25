package com.sparta.concurrencycontrolproject.domain.ticket.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.concurrencycontrolproject.domain.ticket.dto.request.SeatSelectionRequest;
import com.sparta.concurrencycontrolproject.domain.ticket.dto.response.SeatResponse;
import com.sparta.concurrencycontrolproject.domain.ticket.service.TicketService;
import com.sparta.concurrencycontrolproject.security.UserDetailsImpl;

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

	//좌석 선택(잠궈서 다른유저가 예약하지 못하게)
	@PostMapping("/{concertId}/seats/select")
	public ResponseEntity<String> selectSeat(@PathVariable Long concertId, @RequestBody SeatSelectionRequest request,
		@AuthenticationPrincipal UserDetailsImpl authUser) {
		ticketService.selectSeat(concertId, request.getSeatNumber(), authUser.getUser().getName());
		return ResponseEntity.ok("좌석 선택 완료!");
	}
}
