package com.sparta.concurrencycontrolproject.domain.concert.service;

import com.sparta.concurrencycontrolproject.domain.concert.dto.ConcertRequest;
import com.sparta.concurrencycontrolproject.domain.concert.dto.ConcertResponse;
import com.sparta.concurrencycontrolproject.domain.concert.entity.Concert;
import com.sparta.concurrencycontrolproject.domain.concert.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository; // Lombok이 생성자를 자동 생성

    public ConcertResponse registerConcert(ConcertRequest request) {
        // Concert 엔티티 생성
        Concert concert = new Concert(
                request.getConcertName(),
                request.getPrice(),
                request.getDescription(),
                request.getImage(),
                request.getDate(),
                request.getStartAt()
        );

        // 좌석 추가 (seating 개수 만큼)
        for (int i = 1; i <= request.getSeating(); i++) {
            concert.addSeat("S" + i);
        }

        // 엔티티 저장
        Concert savedConcert = concertRepository.save(concert);

        // 응답 객체 생성
        return new ConcertResponse(
                savedConcert.getId(),
                savedConcert.getConcertName(),
                savedConcert.getPrice(),
                savedConcert.getDescription(),
                savedConcert.getImage(),
                savedConcert.getDate(),
                savedConcert.getStartAt(),
                savedConcert.getSeats().size() // 저장된 좌석 개수 반환
        );
    }
}
