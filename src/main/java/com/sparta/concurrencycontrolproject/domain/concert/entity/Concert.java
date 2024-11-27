package com.sparta.concurrencycontrolproject.domain.concert.entity;

import com.sparta.concurrencycontrolproject.domain.common.entity.Timestamped;
import com.sparta.concurrencycontrolproject.domain.seat.entity.Seat;
import com.sparta.concurrencycontrolproject.domain.ticket.entity.Ticket;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

	@OneToMany(mappedBy = "concert", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Seat> seats = new ArrayList<>();

	@OneToMany(mappedBy = "concert", cascade = CascadeType.ALL)
	private List<Ticket> tickets = new ArrayList<>();


    public Concert(String concertName, int price, String description, String image,
        LocalDateTime date, LocalDateTime startAt) {
        this.concertName = concertName;
        this.price = price;
        this.description = description;
        this.image = image;
        this.date = date;
        this.startAt = startAt;
    }

    public void updateConcert(String concertName, int price, String description, String image, LocalDateTime date, int seating) {
        this.concertName = concertName;
        this.price = price;
        this.description = description;
        this.image = image;
        this.date = date;
        // 좌석 정보 수정은 추가 구현 필요
    }

}