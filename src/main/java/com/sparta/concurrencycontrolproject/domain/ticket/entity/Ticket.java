package com.sparta.concurrencycontrolproject.domain.ticket.entity;

import com.sparta.concurrencycontrolproject.domain.common.entity.Timestamped;
import com.sparta.concurrencycontrolproject.domain.concert.entity.Concert;
import com.sparta.concurrencycontrolproject.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
	private boolean isCancelled;  // 취소 여부를 관리하는 필드

	public Ticket(Member member, Concert concert, String seatNumber, LocalDateTime date, boolean isCancelled) {
		this.member = member;
		this.concert = concert;
		this.seatNumber = seatNumber;
		this.date = date;
		this.isCancelled = false; // 기본적으로 티켓은 취소되지 않은 상태로 시작
	}

	public void cancel() {
		if (this.isCancelled) {
			throw new IllegalStateException("이 티켓은 이미 취소된 상태입니다.");
		}
		this.isCancelled = true;  // 취소 상태로 변경
	}


}
