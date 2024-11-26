package com.sparta.concurrencycontrolproject.domain.ticket.entity;

import java.time.LocalDateTime;

import com.sparta.concurrencycontrolproject.domain.common.entity.Timestamped;
import com.sparta.concurrencycontrolproject.domain.concert.entity.Concert;
import com.sparta.concurrencycontrolproject.domain.member.entity.Member;

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
@Table(name = "tickets")
public class Ticket extends Timestamped {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "concert_id")
	private Concert concert;

	private String seatNumber;
	private LocalDateTime date;

	public Ticket(Member member, Concert concert, String seatNumber, LocalDateTime date) {
		this.member = member;
		this.concert = concert;
		this.seatNumber = seatNumber;
		this.date = date;
	}


}
