package com.sparta.concurrencycontrolproject.domain.ticket.controller;

import java.time.LocalDateTime;
import java.util.List;

import com.sparta.concurrencycontrolproject.domain.ticket.dto.response.TicketResponse;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.sparta.concurrencycontrolproject.domain.ticket.dto.request.SeatSelectionRequest;
import com.sparta.concurrencycontrolproject.domain.ticket.dto.request.TicketingRequest;
import com.sparta.concurrencycontrolproject.domain.ticket.dto.response.SeatResponse;
import com.sparta.concurrencycontrolproject.domain.ticket.dto.response.TicketDetailResponse;
import com.sparta.concurrencycontrolproject.domain.ticket.dto.response.TicketResponse;
import com.sparta.concurrencycontrolproject.domain.ticket.service.TicketService;
import com.sparta.concurrencycontrolproject.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
		ticketService.selectSeat(concertId, request.getSeatNumber(), authUser.getMember().getName());
		return ResponseEntity.ok("좌석 선택 완료!");
	}

	//좌석 선택(Redis Lock 적용)
	@PostMapping("/{concertId}/seats/lock")
	public ResponseEntity<String> selectSeatWithLock(@PathVariable Long concertId,
		@RequestBody SeatSelectionRequest request, @AuthenticationPrincipal UserDetailsImpl authUser) {
		ticketService.selectSeatWithLock(concertId, request.getSeatNumber(), authUser.getMember().getId());
		return ResponseEntity.ok("좌석에 Lock 생성 완료");
	}

	//예매 완료
	@PostMapping("/{concertId}/ticketing")
	public ResponseEntity<TicketResponse> createTicket(@PathVariable Long concertId,
		@RequestBody TicketingRequest request,
		@AuthenticationPrincipal UserDetailsImpl authUser) {
		TicketResponse ticketResponse = ticketService.createTicket(concertId, request, authUser.getMember().getName());
		return ResponseEntity.ok(ticketResponse);
	}

	//예매 취소(의정님 부분을 장민우가 약간 수정함)
	@DeleteMapping("/{concertId}/tickets/{ticketId}")
	public ResponseEntity<String> deleteTicket(@AuthenticationPrincipal UserDetailsImpl authMember,
		@PathVariable Long ticketId, @PathVariable Long concertId) {
		ticketService.deleteTicket(authMember, ticketId, concertId);
		return ResponseEntity.ok("삭제 되었습니다.");
	}

	@GetMapping("/tickets")
		public ResponseEntity<Page<TicketDetailResponse>> getTickets(
			@AuthenticationPrincipal UserDetailsImpl authMember,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size
	) {
		return ResponseEntity.ok(ticketService.getAllTickets(authMember,page, size));
	}

	@GetMapping("/tickets/{ticketId}")
	public ResponseEntity<TicketDetailResponse> getTicket(
			@AuthenticationPrincipal UserDetailsImpl authMember, @PathVariable Long ticketId) {
		return ResponseEntity.ok(ticketService.getTickets(authMember, ticketId));
	}


}
