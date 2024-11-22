package com.sparta.concurrencycontrolproject.domain.concert.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.SessionAttribute;

import com.sparta.concurrencycontrolproject.domain.common.entity.Timestamped;
import com.sparta.concurrencycontrolproject.domain.seat.entity.Seat;
import com.sparta.concurrencycontrolproject.domain.ticket.entity.Ticket;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "concerts")
public class Concert extends Timestamped {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String concertName;
	private int price;
	private String description;
	private String image;
	private LocalDateTime date;
	private LocalDateTime startAt;

	@OneToMany
	private Seat seat;

	@OneToMany(mappedBy = "concert", cascade = CascadeType.ALL)
	private List<Ticket> tickets = new ArrayList<>();

	public Concert(String concertName, int price, String description, String image, LocalDateTime date, LocalDateTime startAt) {
		this.concertName = concertName;
		this.price = price;
		this.description = description;
		this.image = image;
		this.date = date;
		this.startAt = startAt;
	}
}
