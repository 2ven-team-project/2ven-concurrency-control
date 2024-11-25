package com.sparta.concurrencycontrolproject.domain.seat.entity;

import com.sparta.concurrencycontrolproject.domain.concert.entity.Concert;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "seats")
public class Seat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String seatNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "concert_id")
	private Concert concert;

	private boolean isBooked;

	public Seat(Concert concert, String seatNumber) {
		if(concert == null) {
			throw new IllegalArgumentException("공연을 찾을 수 없습니다.");
		}
		this.concert = concert;
		this.seatNumber = seatNumber;
	}

	//좌석 예약시
	public void book() {
		if (isBooked) {
			throw new IllegalStateException("이미 예약된 좌석입니다.");
		}
		this.isBooked = true;
	}

	//좌석 예약 취소시
	public void cancelBooking() {
		if(!isBooked) {
			throw new IllegalArgumentException("이용 가능한 좌석입니다.");
		}
		this.isBooked = false;
	}

}
