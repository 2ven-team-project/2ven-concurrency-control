package com.sparta.concurrencycontrolproject.domain.ticket.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.concurrencycontrolproject.domain.concert.entity.Concert;
import com.sparta.concurrencycontrolproject.domain.concert.repository.ConcertRepository;
import com.sparta.concurrencycontrolproject.domain.member.entity.Member;
import com.sparta.concurrencycontrolproject.domain.member.repository.MemberRepository;
import com.sparta.concurrencycontrolproject.domain.seat.entity.Seat;
import com.sparta.concurrencycontrolproject.domain.seat.repository.SeatRepository;
import com.sparta.concurrencycontrolproject.domain.ticket.dto.response.SeatResponse;
import com.sparta.concurrencycontrolproject.domain.ticket.entity.Ticket;
import com.sparta.concurrencycontrolproject.domain.ticket.repository.TicketRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {
	private final MemberRepository memberRepository;
	private final TicketRepository ticketRepository;
	private final ConcertRepository concertRepository;
	private final SeatRepository seatRepository;

	public List<SeatResponse> getAvailableSeats(Long concertId) {
		List<Seat> seats = seatRepository.findByConcertId(concertId);

		return seats.stream()
			.filter(seat -> !seat.isBooked())
			.map(seat -> new SeatResponse(seat.getSeatNumber(), seat.isBooked()))
			.collect(Collectors.toList());
	}


	@Transactional
	public Ticket bookSeat(Long memberId, Long concertId, String seatNumber) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 입니다."));
		Concert concert = concertRepository.findById(concertId)
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 공연 입니다."));

		//좌석 확인
		Seat seat = seatRepository.findByConcertIdAndSeatNumber(concertId, seatNumber)
			.orElseThrow(() -> new IllegalArgumentException("좌석 또는 콘서트가 유효하지 않습니다."));

		if (seat.isBooked()) {
			throw new IllegalArgumentException("좌석이 이미 예약처리 중 입니다.");
		}

		seat.book();

		Ticket ticket = new Ticket(member, concert, seatNumber);
		return ticketRepository.save(ticket);
	}


}
