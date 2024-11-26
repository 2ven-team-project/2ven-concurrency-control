package com.sparta.concurrencycontrolproject.domain.concert.entity;

import jakarta.persistence.*;

@Entity
public class Concert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String price;
    private String image;
    private String description;
    private String date;
    private int seating;

    // Getters and Setters
}
