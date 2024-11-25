package com.sparta.concurrencycontrolproject.domain.seat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.concurrencycontrolproject.domain.seat.entity.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {
	Optional<Seat> findByConcertIdAndSeatNumber(Long concertId, String seatNumber);

	List<Seat> findByConcertId(Long concertId);
}
