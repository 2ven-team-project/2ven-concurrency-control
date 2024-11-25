package com.sparta.concurrencycontrolproject.domain.concert.entity;

import com.sparta.concurrencycontrolproject.domain.common.entity.Timestamped;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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

//	@OneToMany
//	private Seat seat;
//
//	@OneToMany(mappedBy = "tickets", cascade = CascadeType.ALL)
//	private List<Ticket> tickets = new ArrayList<>();

    public Concert(String concertName, int price, String description, String image,
        LocalDateTime date, LocalDateTime startAt) {
        this.concertName = concertName;
        this.price = price;
        this.description = description;
        this.image = image;
        this.date = date;
        this.startAt = startAt;
    }
}
