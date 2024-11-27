package com.sparta.concurrencycontrolproject.domain.seat.repository;

import com.sparta.concurrencycontrolproject.domain.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
	@Query("SELECT s FROM Seat s WHERE s.concert.id = :concertId AND s.seatNumber = :seatNumber")
	List<Seat> findAllByConcertIdAndSeatNumber(@Param("concertId") Long concertId, @Param("seatNumber") String seatNumber);

	List<Seat> findByConcertId(Long concertId);
}
