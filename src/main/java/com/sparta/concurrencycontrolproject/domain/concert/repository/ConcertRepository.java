package com.sparta.concurrencycontrolproject.domain.concert.repository;

import com.sparta.concurrencycontrolproject.domain.concert.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertRepository extends JpaRepository<Concert, Long> {
}
