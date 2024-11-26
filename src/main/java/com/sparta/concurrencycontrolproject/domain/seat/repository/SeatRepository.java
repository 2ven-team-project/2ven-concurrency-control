package com.sparta.concurrencycontrolproject.domain.seat.repository;

import com.sparta.concurrencycontrolproject.domain.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
	Optional<Seat> findByConcertIdAndSeatNumber(Long concertId, String seatNumber);

	List<Seat> findByConcertId(Long concertId);
}
