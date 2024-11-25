package com.sparta.concurrencycontrolproject.domain.concert.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.concurrencycontrolproject.domain.concert.entity.Concert;

public interface ConcertRepository extends JpaRepository<Concert, Long> {
}
