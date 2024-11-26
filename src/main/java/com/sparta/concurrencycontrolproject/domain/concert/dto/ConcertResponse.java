package com.sparta.concurrencycontrolproject.domain.concert.dto;

public class ConcertResponse {
    private Long concertId;
    private String name;
    private String price;
    private String image;
    private String description;
    private String date;
    private int seating;

    public ConcertResponse(Long concertId, String name, String price, String image, String description, String date, int seating) {
        this.concertId = concertId;
        this.name = name;
        this.price = price;
        this.image = image;
        this.description = description;
        this.date = date;
        this.seating = seating;
    }

    // Getters and Setters
}
