package com.sparta.concurrencycontrolproject.domain.concert.service;

import com.sparta.concurrencycontrolproject.domain.concert.dto.ConcertRequest;
import com.sparta.concurrencycontrolproject.domain.concert.dto.ConcertResponse;
import com.sparta.concurrencycontrolproject.domain.concert.dto.ConcertUpdateRequest;
import com.sparta.concurrencycontrolproject.domain.concert.entity.Concert;
import com.sparta.concurrencycontrolproject.domain.concert.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository; // Lombok이 생성자를 자동 생성

    // 공연 등록
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

    // 공연 수정
    @Transactional
    public ConcertResponse updateConcert(Long concertId, ConcertUpdateRequest request) {
        // 공연 엔티티 조회
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공연입니다."));

        // 공연 정보 업데이트
        concert.updateConcert(
                request.getConcertName(),
                request.getPrice(),
                request.getDescription(),
                request.getImage(),
                request.getDate(),
                request.getSeating()
        );

        // 수정된 공연 정보 반환
        return new ConcertResponse(
                concert.getId(),
                concert.getConcertName(),
                concert.getPrice(),
                concert.getDescription(),
                concert.getImage(),
                concert.getDate(),
                concert.getStartAt(),
                concert.getSeats().size()
        );
    }
}
