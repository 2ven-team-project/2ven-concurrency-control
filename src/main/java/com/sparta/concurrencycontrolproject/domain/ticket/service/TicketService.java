package com.sparta.concurrencycontrolproject.domain.ticket.service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.sparta.concurrencycontrolproject.domain.ticket.dto.request.TicketingRequest;
import com.sparta.concurrencycontrolproject.domain.ticket.dto.response.TicketResponse;
import com.sparta.concurrencycontrolproject.domain.ticket.lock.util.RedisKeyUtil;
import com.sparta.concurrencycontrolproject.security.UserDetailsImpl;
import com.sun.jdi.request.InvalidRequestStateException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.concurrencycontrolproject.domain.concert.entity.Concert;
import com.sparta.concurrencycontrolproject.domain.concert.repository.ConcertRepository;
import com.sparta.concurrencycontrolproject.domain.member.entity.Member;
import com.sparta.concurrencycontrolproject.domain.member.repository.MemberRepository;
import com.sparta.concurrencycontrolproject.domain.seat.entity.Seat;
import com.sparta.concurrencycontrolproject.domain.seat.repository.SeatRepository;
import com.sparta.concurrencycontrolproject.domain.ticket.dto.request.TicketingRequest;
import com.sparta.concurrencycontrolproject.domain.ticket.dto.response.SeatResponse;
import com.sparta.concurrencycontrolproject.domain.ticket.dto.response.TicketDetailResponse;
import com.sparta.concurrencycontrolproject.domain.ticket.dto.response.TicketResponse;
import com.sparta.concurrencycontrolproject.domain.ticket.entity.Ticket;
import com.sparta.concurrencycontrolproject.domain.ticket.repository.TicketRepository;
import com.sparta.concurrencycontrolproject.security.UserDetailsImpl;
import com.sun.jdi.request.InvalidRequestStateException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {
	private final MemberRepository memberRepository;
	private final TicketRepository ticketRepository;
	private final ConcertRepository concertRepository;
	private final SeatRepository seatRepository;
	private final RedisTemplate<String, Long> redisTemplate;

	public List<SeatResponse> getAvailableSeats(Long concertId) {
		List<Seat> seats = seatRepository.findByConcertId(concertId);

		return seats.stream()
			.filter(seat -> !seat.isBooked())
			.map(seat -> new SeatResponse(seat.getSeatNumber(), seat.isBooked()))
			.collect(Collectors.toList());
	}

	@Transactional
	public void selectSeat(Long concertId, String seatNumber, String name) {
		Seat seat = seatRepository.findByConcertIdAndSeatNumber(concertId, seatNumber)
			.orElseThrow(() -> new IllegalArgumentException("공연 또는 좌석이 유효하지 않습니다."));

		if (seat.isBooked()) {
			throw new IllegalStateException("좌석이 이미 예약되었습니다.");
		}
		seat.book(name);
		seatRepository.save(seat);
	}

	@Transactional
	public TicketResponse createTicket(Long concertId, TicketingRequest request, String userName) {
		//좌석 확인
		Seat seat = seatRepository.findByConcertIdAndSeatNumber(concertId, request.getSeatNumber())
			.orElseThrow(() -> new IllegalArgumentException("좌석 또는 콘서트가 유효하지 않습니다."));

		Concert concert = concertRepository.findById(concertId)
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 공연 입니다."));

		if (!seat.isBooked() || !userName.equals(seat.getBookedBy())) {
			throw new IllegalArgumentException("좌석이 예약 상태가 아니거나, 좌석에 예약된 예약자와 다릅니다.");
		}

		Member member = memberRepository.findByName(userName)
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않는 유저입니다."));
		Ticket ticket = new Ticket(member, concert, seat.getSeatNumber(), request.getDate(), false);
		ticketRepository.save(ticket);

		String lockKey = RedisKeyUtil.generateSeatLockKey(concertId, request.getSeatNumber());
		releaseLock(lockKey);

		return new TicketResponse(
			concert.getConcertName(),
			member.getName(),
			seat.getSeatNumber(),
			request.getDate(),
			ticket.isCancelled()
		);
	}

	@Transactional
	public void deleteTicket(UserDetailsImpl authMember, Long ticketId, Long concertId) {
		Member member = memberRepository.findById(authMember.getMember().getId())
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 입니다."));
		Concert concert = concertRepository.findById(concertId)
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 공연 입니다."));
		Ticket ticket = ticketRepository.findById(ticketId)
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않는 티켓입니다."));

		if (ticket.isCancelled()) {
			throw new InvalidRequestStateException("이미 취소된 티켓입니다.");
		}
		if (!ticket.getMember().equals(member)) {
			throw new InvalidRequestStateException("로그인한 사용자의 티켓이 아닙니다.");
		}

		ticket.cancel();
		ticketRepository.save(ticket);
	}

	@Transactional(readOnly = true)
	public Page<TicketDetailResponse> getAllTickets(UserDetailsImpl authMember, int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size);

		Page<Ticket> tickets = ticketRepository.findByMemberId(authMember, pageable);

		return tickets.map(ticket -> new TicketDetailResponse(
				ticket.getId(),
				ticket.getConcert().getConcertName(),
				ticket.getSeatNumber(),
				ticket.getDate(),
				ticket.isCancelled(),
				authMember.getMember().getName()
		));
	}

	public TicketDetailResponse getTickets(UserDetailsImpl authMember, Long ticketId) {
		Member member = memberRepository.findById(authMember.getMember().getId())
				.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 입니다."));
		Ticket ticket = ticketRepository.findById(ticketId)
				.orElseThrow(() -> new IllegalArgumentException("유효하지 않는 티켓입니다."));

		return new TicketDetailResponse(
				ticket.getId(),
				ticket.getConcert().getConcertName(),
				ticket.getSeatNumber(),
				ticket.getDate(),
				ticket.isCancelled(),
				authMember.getMember().getName()
		);
	}

	//Lock 적용 좌석선택
	@Transactional
	public void selectSeatWithLock(Long concertId, String seatNumber, Long memberId) {
		String lockKey = RedisKeyUtil.generateSeatLockKey(concertId, seatNumber); //CONCERT-123-SEAT-A10 / memberId
		try {
			//setIfAbsent 는 성공적으로 Lock 이 설정되었는지 True or False 반환
			Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(lockKey, memberId, 10, TimeUnit.MINUTES);
			if (lockAcquired == null || !lockAcquired) {
				throw new IllegalArgumentException("다른 유저가 해당 좌석을 예약중입니다.");
			}

			Seat seat = seatRepository.findByConcertIdAndSeatNumber(concertId, seatNumber)
				.orElseThrow(() -> new IllegalArgumentException("공연 또는 좌석이 유효햐지 않습니다."));

			if (seat.isBooked()) {
				throw new IllegalArgumentException("좌석이 이미 예약되었습니다.");
			}

			seat.book(memberId);
			seatRepository.save(seat);
		} catch (IllegalArgumentException e) {
			//lock 을 얻지 못했으면
			throw new IllegalStateException("좌석을 예약할 수 없습니다. 다른 유저가 이미 예약중입니다.");
		}
	}

	private void releaseLock(String lockKey) {
		redisTemplate.delete(lockKey);
	}
}
