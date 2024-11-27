package com.sparta.concurrencycontrolproject.domain.concert.service;

import com.sparta.concurrencycontrolproject.domain.concert.dto.ConcertRequest;
import com.sparta.concurrencycontrolproject.domain.concert.dto.ConcertResponse;
import com.sparta.concurrencycontrolproject.domain.concert.dto.ConcertUpdateRequest;
import com.sparta.concurrencycontrolproject.domain.concert.entity.Concert;
import com.sparta.concurrencycontrolproject.domain.concert.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;

    // 공연 등록
    public ConcertResponse registerConcert(ConcertRequest request) {
        Concert concert = new Concert(
                request.getConcertName(),
                request.getPrice(),
                request.getDescription(),
                request.getImage(),
                request.getDate(),
                request.getStartAt()
        );
        for (int i = 1; i <= request.getSeating(); i++) {
            concert.addSeat("S" + i);
        }
        Concert savedConcert = concertRepository.save(concert);
        return new ConcertResponse(
                savedConcert.getId(),
                savedConcert.getConcertName(),
                savedConcert.getPrice(),
                savedConcert.getDescription(),
                savedConcert.getImage(),
                savedConcert.getDate(),
                savedConcert.getStartAt(),
                savedConcert.getSeats().size()
        );
    }

    // 공연 수정
    @Transactional
    public ConcertResponse updateConcert(Long concertId, ConcertUpdateRequest request) {
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공연입니다."));
        concert.updateConcert(
                request.getConcertName(),
                request.getPrice(),
                request.getDescription(),
                request.getImage(),
                request.getDate(),
                request.getSeating()
        );
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

    // 공연 전체 조회 및 검색
    @Transactional(readOnly = true)
    public Page<ConcertResponse> getConcerts(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        // 이름으로 검색이 있는 경우
        if (name != null && !name.isBlank()) {
            return concertRepository.findByConcertNameContaining(name, pageable).map(concert ->
                    new ConcertResponse(
                            concert.getId(),
                            concert.getConcertName(),
                            concert.getPrice(),
                            concert.getDescription(),
                            concert.getImage(),
                            concert.getDate(),
                            concert.getStartAt(),
                            concert.getSeats().size()
                    )
            );
        }

        // 전체 조회
        return concertRepository.findAll(pageable).map(concert ->
                new ConcertResponse(
                        concert.getId(),
                        concert.getConcertName(),
                        concert.getPrice(),
                        concert.getDescription(),
                        concert.getImage(),
                        concert.getDate(),
                        concert.getStartAt(),
                        concert.getSeats().size()
                )
        );
    }
}
