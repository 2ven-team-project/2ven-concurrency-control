package com.sparta.concurrencycontrolproject.domain.concert.service;

import com.sparta.concurrencycontrolproject.domain.concert.entity.Concert;
import com.sparta.concurrencycontrolproject.domain.concert.repository.ConcertRepository;
import com.sparta.concurrencycontrolproject.domain.concert.dto.ConcertRequest;
import com.sparta.concurrencycontrolproject.domain.concert.dto.ConcertResponse;
import org.springframework.stereotype.Service;

@Service
public class ConcertService {
    private final ConcertRepository concertRepository;

    public ConcertService(ConcertRepository concertRepository) {
        this.concertRepository = concertRepository;
    }

    public ConcertResponse registerConcert(ConcertRequest request) {
        Concert concert = new Concert();
        concert.setName(request.getName());
        concert.setPrice(request.getPrice());
        concert.setImage(request.getImage());
        concert.setDescription(request.getDescription());
        concert.setDate(request.getDate());
        concert.setSeating(request.getSeating());

        Concert savedConcert = concertRepository.save(concert);

        return new ConcertResponse(
                savedConcert.getId(),
                savedConcert.getName(),
                savedConcert.getPrice(),
                savedConcert.getImage(),
                savedConcert.getDescription(),
                savedConcert.getDate(),
                savedConcert.getSeating()
        );
    }
}
